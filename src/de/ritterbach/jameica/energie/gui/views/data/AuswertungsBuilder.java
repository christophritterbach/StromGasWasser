package de.ritterbach.jameica.energie.gui.views.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.EnergiePlugin;
import de.ritterbach.jameica.energie.rmi.Kosten;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

public class AuswertungsBuilder {
	private final static I18N i18n = Application.getPluginLoader().getPlugin(EnergiePlugin.class).getResources()
			.getI18N();
	static final long ONE_HOUR = (60 * 60 * 1000L);
	static final long ONE_DAY = ONE_HOUR * 24L;
	static final BigDecimal einJahr = new BigDecimal(365);
	private Kosten kosten = null;
	private BigDecimal summe = BigDecimal.ZERO;
	private BigDecimal letzterZaehler = BigDecimal.ZERO;
	private Date letztesDatum;

	private Zaehler zaehler;

	public AuswertungsBuilder(Zaehler zaehler) {
		super();
		this.zaehler = zaehler;
	}

	private ResultSetExtractor rse = new ResultSetExtractor() {
		@Override
		public Object extract(ResultSet rs) throws RemoteException, SQLException {
			List<Auswertung> liste = new ArrayList<Auswertung>();
			if (kosten.isNeuePeriode())
				summe = BigDecimal.ZERO;
			while (rs.next()) {
				Auswertung zaehlerElement = new Auswertung();
				Date datum = rs.getDate("datum");
				zaehlerElement.setDatum(datum);
				zaehlerElement.setNotiz(rs.getString("notiz"));
				Integer schaetzung = rs.getInt("schaetzung");
				if (!rs.wasNull()) {
					BigDecimal ableseWert = rs.getBigDecimal("ablese_wert");
					BigDecimal verbrauch = rs.getBigDecimal("verbrauch");
					Integer zaehlerAus = rs.getInt("zaehlerwechsel_aus");
					Integer zaehlerEin = rs.getInt("zaehlerwechsel_ein");

					if (schaetzung == 1 && zaehlerAus == 0 && zaehlerEin == 0) {
						ableseWert = letzterZaehler.add(verbrauch);
					} else {
						if (zaehlerEin == 1) {
							verbrauch = BigDecimal.ZERO;
						} else {
							// wenn dieses das erste Element ist, dann rechnen wir nicht sondern glauben,
							// dass der Verbauch korrekt ist.
							// Bei allen anderen rechnen wir gerne
							if (liste.size() > 0) {
								verbrauch = ableseWert.subtract(letzterZaehler);
							}
						}
					}
					Long anzahlTage = (datum.getTime() - letztesDatum.getTime() + ONE_HOUR) / ONE_DAY;
					zaehlerElement.setAnzahlTage(anzahlTage);
					BigDecimal anteilGrund = kosten.getGrundpreis().multiply(new BigDecimal(anzahlTage)).divide(einJahr,
							2, RoundingMode.HALF_UP);
					zaehlerElement.setGrundpreisAnteil(anteilGrund);
					BigDecimal ap = verbrauch.multiply(kosten.getArbeitspreis()).multiply(kosten.getFaktor());
					zaehlerElement.setArbeitspreis(ap);
					zaehlerElement.setGenutzt(anteilGrund.add(ap));
					summe = summe.add(zaehlerElement.getGenutzt());
					zaehlerElement.setSumme(summe);
					zaehlerElement.setNotiz(rs.getString("notiz"));
					letztesDatum = datum;
					letzterZaehler = ableseWert;
					liste.add(zaehlerElement);
				}
				BigDecimal abschlag = rs.getBigDecimal("abschlag_betrag");
				if (!rs.wasNull()) {
					zaehlerElement.setGezahlt(abschlag);
					summe = summe.add(abschlag);
					zaehlerElement.setSumme(summe);
					liste.add(zaehlerElement);
				}
			}
			return liste;
		}
	};

	public List<Auswertung> getListe() throws RemoteException {
		List<Auswertung> auswertungen = new ArrayList<>();
		if (zaehler == null) {
			// Dann nimm den ersten Zaehler
			DBIterator<Zaehler> z = Settings.getDBService().createList(Zaehler.class);
			z.addFilter("ist_aktiv = 1");
			z.setOrder("ORDER BY id");
			if (z.hasNext())
				this.zaehler = z.next();
			Settings.setZaehler(this.zaehler);
		}
		if (this.zaehler != null) {
			DBService service = Settings.getDBService();
			DBIterator<Kosten> kostenListe = service.createList(Kosten.class);
			kostenListe.addFilter("abgerechnet = 0");
			kostenListe.addFilter("zaehler_id = ?", this.zaehler.getID());
			kostenListe.setOrder("order by gueltig_von");
			while (kostenListe.hasNext()) {
				// Start der Abrechnung
				this.kosten = (Kosten) kostenListe.next();
				String notiz = kosten.getNotiz();
				if (this.kosten.isNeuePeriode()) {
					summe = BigDecimal.ZERO;
					if (notiz == null || notiz.length() < 1)
						notiz = "";
					else
						notiz += ". ";
					notiz = notiz + i18n.tr("Neue Abrechnungsperiode");
				}
				auswertungen.add(new Auswertung(this.kosten.getGueltigVon(), summe, notiz));
				letztesDatum = new Date(this.kosten.getGueltigVon().getTime() - ONE_DAY);// Dieser Tag zählt auch
				// Alle Abschläge und Zählerstände
				auswertungen.addAll((List<Auswertung>) service.execute(
						"SELECT abschlag_datum AS datum, 1 as sortierung, NULL as ablese_wert, NULL as verbrauch, NULL as schaetzung, 0 as zaehlerwechsel_aus, 0 AS zaehlerwechsel_ein, abschlag_betrag, notiz "
								+ "FROM abschlag " + "WHERE zaehler_id = ? "
								+ "AND abschlag_datum >= ? AND abschlag_datum <= ? " + "UNION "
								+ "SELECT ablese_datum AS datum, 2 as sortierung, ablese_wert, verbrauch, schaetzung, zaehlerwechsel_aus, zaehlerwechsel_ein, NULL AS abschlag_betrag, notiz "
								+ "FROM zaehlerstand " + "WHERE zaehler_id = ? "
								+ "AND ablese_datum >= ? AND ablese_datum <= ? " + "ORDER BY 1, 2, 7",
						new Object[] { this.zaehler.getID(), this.kosten.getGueltigVon(),
								(this.kosten.getAbschlagBis() != null) ? this.kosten.getAbschlagBis()
										: this.kosten.getGueltigBis(),
								this.zaehler.getID(), this.kosten.getGueltigVon(), this.kosten.getGueltigBis() },
						rse));
				// Alles bis zum Ende der Abrechnung
				Long anzahlTage = (this.kosten.getGueltigBis().getTime() - letztesDatum.getTime() + ONE_HOUR) / ONE_DAY;
				BigDecimal anteilGrund = kosten.getGrundpreis().multiply(new BigDecimal(anzahlTage)).divide(einJahr, 2,
						RoundingMode.HALF_UP);
				this.summe = this.summe.add(anteilGrund);
				if (anzahlTage > 0)
					auswertungen.add(new Auswertung(this.kosten.getGueltigBis(), anzahlTage, anteilGrund, this.summe,
							this.kosten.getNotiz()));

			}
		}
		return auswertungen;
	}
}
