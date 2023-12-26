package de.ritterbach.jameica.energie.gui.views;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.StromWasserGasPlugin;
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
	private final static I18N i18n = Application.getPluginLoader().getPlugin(StromWasserGasPlugin.class).getResources().getI18N();
	private TablePart tableAuswertung = null;

	private ResultSetExtractor rse = new ResultSetExtractor() {
		@Override
		public Object extract(ResultSet rs) throws RemoteException, SQLException {
			List<Auswertung> liste = new ArrayList<Auswertung>();
			Date letztesDatum = new Date();
			BigDecimal summe = BigDecimal.ZERO;
			BigDecimal grundpreis = BigDecimal.ZERO;
			BigDecimal arbeitspreis = BigDecimal.ZERO;
			BigDecimal letzterZaehler = BigDecimal.ZERO;
			while (rs.next()) {
				Auswertung zaehlerElement = new Auswertung();
				Date datum = rs.getDate("datum");
				zaehlerElement.setDatum(datum);
				BigDecimal gp = rs.getBigDecimal("grundpreis");
				if (!rs.wasNull()) {
					letztesDatum = datum;
					grundpreis = gp;
					BigDecimal ap = rs.getBigDecimal("arbeitspreis");
					if (!rs.wasNull()) {
						arbeitspreis = ap;
					}
					Integer rechnungsabschluss = rs.getInt("rechnungsabschluss");
					if (rs.wasNull()) {
						rechnungsabschluss = 0;
					}
					if (rechnungsabschluss > 0) {
						Logger.info(getHelp());
						summe = BigDecimal.ZERO;
					}
				}
				Integer schaetzung = rs.getInt("schaetzung");
				if (!rs.wasNull()) {
					BigDecimal ableseWert = rs.getBigDecimal("ablese_wert");
					BigDecimal verbrauch = rs.getBigDecimal("verbrauch");
					Integer zaehlerAus = rs.getInt("zaehler_aus");
					Integer zaehlerEin = rs.getInt("zaehler_ein");
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
					BigDecimal anteilGrund = grundpreis.multiply(new BigDecimal(anzahlTage)).divide(einJahr, 2,
							RoundingMode.HALF_UP);
					zaehlerElement.setGrundpreisAnteil(anteilGrund);
					BigDecimal ap = verbrauch.multiply(arbeitspreis);
					zaehlerElement.setArbeitspreis(ap);
					zaehlerElement.setGenutzt(anteilGrund.add(ap));
					summe = summe.subtract(zaehlerElement.getGenutzt());
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
		List<Auswertung> auswertung = (List<Auswertung>) service.execute(
				"WITH kosten AS (SELECT id, gueltig_von, gueltig_bis, grundpreis, arbeitspreis FROM kosten_strom WHERE abgerechnet = 0) "
						+ "SELECT kosten_strom.gueltig_von AS datum, kosten_strom.grundpreis, kosten_strom.arbeitspreis, NULL AS ablese_wert, NULL AS verbrauch, NULL AS schaetzung, 0 AS zaehler_aus, 0 AS zaehler_ein, NULL AS abschlag_betrag, kosten_strom.rechnungsabschluss "
						+ "FROM kosten_strom JOIN kosten ON kosten.id = kosten_strom.id "
						+ "UNION SELECT ablese_datum AS datum, NULL, NULL, ablese_wert, verbrauch, schaetzung, zaehlerwechsel_aus, zaehlerwechsel_ein, NULL AS abschlag_betrag, 0 "
						+ "FROM zaehler_strom JOIN kosten ON ablese_datum >= kosten.gueltig_von AND ablese_datum <= kosten.gueltig_bis "
						+ "UNION SELECT abschlag_datum AS datum, NULL, NULL, NULL, NULL, NULL, 0, 0, abschlag_betrag, 0 "
						+ "FROM abschlag_strom JOIN kosten ON abschlag_datum >= kosten.gueltig_von AND abschlag_datum <= kosten.gueltig_bis "
						+ "ORDER BY 1, 8",
				null, this.rse);

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
		this.tableAuswertung.setRememberOrder(true);
		this.tableAuswertung.setRememberColWidths(true);
		this.tableAuswertung.paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

	public class Auswertung implements Serializable {
		private Date datum;
		private Long anzahlTage;
		private BigDecimal grundpreisAnteil;
		private BigDecimal arbeitspreis;
		private BigDecimal gezahlt;
		private BigDecimal genutzt;
		private BigDecimal summe;

		public Auswertung() {
			super();
		}

		public Date getDatum() {
			return datum;
		}

		public void setDatum(Date datum) {
			this.datum = datum;
		}

		public Long getAnzahlTage() {
			return anzahlTage;
		}

		public void setAnzahlTage(long anzahlTage) {
			this.anzahlTage = anzahlTage;
		}

		public BigDecimal getGrundpreisAnteil() {
			return grundpreisAnteil;
		}

		public void setGrundpreisAnteil(BigDecimal grundpreisAnteil) {
			this.grundpreisAnteil = grundpreisAnteil;
		}

		public BigDecimal getArbeitspreis() {
			return arbeitspreis;
		}

		public void setArbeitspreis(BigDecimal arbeitspreis) {
			this.arbeitspreis = arbeitspreis;
		}

		public BigDecimal getGezahlt() {
			return gezahlt;
		}

		public void setGezahlt(BigDecimal gezahlt) {
			this.gezahlt = gezahlt;
		}

		public BigDecimal getGenutzt() {
			return genutzt;
		}

		public void setGenutzt(BigDecimal genutzt) {
			this.genutzt = genutzt;
		}

		public BigDecimal getSumme() {
			return summe;
		}

		public void setSumme(BigDecimal summe) {
			this.summe = summe;
		}

	}
}
