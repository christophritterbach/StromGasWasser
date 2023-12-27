package de.ritterbach.jameica.energie.gui.views;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.StromWasserGasPlugin;
import de.ritterbach.jameica.energie.rmi.KostenStrom;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class StromAuswertung extends AbstractView {

	static final long ONE_HOUR = (60 * 60 * 1000L);
	static final long ONE_DAY = ONE_HOUR * 24L;
	static final BigDecimal einJahr = new BigDecimal(365);
	private final static I18N i18n = Application.getPluginLoader().getPlugin(StromWasserGasPlugin.class).getResources()
			.getI18N();
	private TablePart tableAuswertung = null;
	private KostenStrom kosten = null;
	private BigDecimal summe = BigDecimal.ZERO;
	private BigDecimal letzterZaehler = BigDecimal.ZERO;
	private Date letztesDatum;

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
						if (zaehlerAus == 1) {
						}
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
					BigDecimal ap = verbrauch.multiply(kosten.getArbeitspreis());
					zaehlerElement.setArbeitspreis(ap);
					zaehlerElement.setGenutzt(anteilGrund.add(ap));
					summe = summe.add(zaehlerElement.getGenutzt());
					zaehlerElement.setSumme(summe);
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

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Strom Auswertung"));

		DBService service = Settings.getDBService();
		DBIterator kostenListe = service.createList(KostenStrom.class);
		kostenListe.addFilter("abgerechnet = 0");
		kostenListe.setOrder("order by gueltig_von");
		List<Auswertung> auswertung = new ArrayList<>();
		while (kostenListe.hasNext()) {
			// Start der Abrechnung
			this.kosten = (KostenStrom) kostenListe.next();
			String notiz = this.kosten.getNotiz();
			if (this.kosten.isNeuePeriode()) {
				this.summe = BigDecimal.ZERO;
				if (notiz == null || notiz.length() < 1)
					notiz = "";
				else
					notiz += ". ";
				notiz = notiz + i18n.tr("Neue Abrechnungsperiode");
			}
			auswertung.add(new Auswertung(this.kosten.getGueltigVon(), summe, notiz));
			this.letztesDatum = this.kosten.getGueltigVon();
			// Alle Abschläge und Zählerstände
			auswertung.addAll((List<Auswertung>) service.execute(
					"SELECT abschlag_datum AS datum, 1 as sortierung, NULL as ablese_wert, NULL as verbrauch, NULL as schaetzung, 0 as zaehlerwechsel_aus, 0 AS zaehlerwechsel_ein, abschlag_betrag, notiz "
							+ "FROM abschlag_strom " + "WHERE abschlag_datum >= ? AND abschlag_datum <= ? " + "UNION "
							+ "SELECT ablese_datum AS datum, 2 as sortierung, ablese_wert, verbrauch, schaetzung, zaehlerwechsel_aus, zaehlerwechsel_ein, NULL AS abschlag_betrag, notiz "
							+ "FROM zaehler_strom " + "WHERE ablese_datum >= ? AND ablese_datum <= ? "
							+ "ORDER BY 1, 2, 7",
					new Date[]{this.kosten.getGueltigVon(), (this.kosten.getAbschlagBis() != null) ? this.kosten.getAbschlagBis() : this.kosten.getGueltigBis(), this.kosten.getGueltigVon(), this.kosten.getGueltigBis()},
					this.rse));
			// Alles bis zum Ende der Abrechnung
			Long anzahlTage = (this.kosten.getGueltigBis().getTime() - letztesDatum.getTime() + ONE_HOUR) / ONE_DAY;
			BigDecimal anteilGrund = kosten.getGrundpreis().multiply(new BigDecimal(anzahlTage)).divide(einJahr, 2,
					RoundingMode.HALF_UP);
			this.summe = this.summe.add(anteilGrund);
			if (anzahlTage > 0)
				auswertung.add(new Auswertung(this.kosten.getGueltigBis(), anzahlTage, anteilGrund, this.summe, this.kosten.getNotiz()));
		}

		this.tableAuswertung = new TablePart(auswertung, null);
		this.tableAuswertung.addColumn(i18n.tr("Datum"), "datum", new DateFormatter());
		this.tableAuswertung.addColumn(i18n.tr("Anzahl_Tage"), "anzahlTage");
		this.tableAuswertung.addColumn(i18n.tr("Anteil_Grundpreis"), "grundpreisAnteil",
				new CurrencyFormatter(Settings.CURRENCY, null));
		this.tableAuswertung.addColumn(i18n.tr("Arbeitspreis"), "arbeitspreis",
				new CurrencyFormatter(Settings.CURRENCY, null));
		this.tableAuswertung.addColumn(i18n.tr("Gezahlt"), "gezahlt", new CurrencyFormatter(Settings.CURRENCY, null));
		this.tableAuswertung.addColumn(i18n.tr("Genutzt"), "genutzt", new CurrencyFormatter(Settings.CURRENCY, null));
		this.tableAuswertung.addColumn(i18n.tr("Summe"), "summe", new CurrencyFormatter(Settings.CURRENCY, null));
		this.tableAuswertung.addColumn(i18n.tr("Notiz"), "notiz");
		this.tableAuswertung.setRememberOrder(true);
		this.tableAuswertung.setRememberColWidths(true);
		this.tableAuswertung.paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}
}
