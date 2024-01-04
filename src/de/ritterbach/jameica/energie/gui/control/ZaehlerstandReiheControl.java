package de.ritterbach.jameica.energie.gui.control;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.EnergieDBService;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.ritterbach.jameica.energie.rmi.Zaehlerstand;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandReiheControl extends AbstractControl {

	private DateInput zaehlerstandDatumAblesung;
	private DateInput zaehlerstandDatumEnde;
	private DecimalInput zaehlerstandVerbrauch;

	public ZaehlerstandReiheControl(AbstractView view) {
		super(view);
	}

	public Input getZaehlerstandDatumAnfang() throws RemoteException {
		if (zaehlerstandDatumAblesung != null)
			return zaehlerstandDatumAblesung;

		Date datum = new Date();
		this.zaehlerstandDatumAblesung = new DateInput(datum, Settings.DATEFORMAT);
		this.zaehlerstandDatumAblesung.setName(Settings.i18n().tr("Ablese_Datum_Anfang"));
		return this.zaehlerstandDatumAblesung;
	}

	public Input getZaehlerstandDatumEnde() throws RemoteException {
		if (zaehlerstandDatumEnde != null)
			return zaehlerstandDatumEnde;

		Date datum = new Date();
		this.zaehlerstandDatumEnde = new DateInput(datum, Settings.DATEFORMAT);
		this.zaehlerstandDatumEnde.setName(Settings.i18n().tr("Ablese_Datum_Ende"));
		return this.zaehlerstandDatumEnde;
	}

	public Input getZaehlerstandVerbrauch() throws RemoteException {
		if (zaehlerstandVerbrauch != null)
			return zaehlerstandVerbrauch;

		Zaehler zaehler = Settings.getZaehler();
		zaehlerstandVerbrauch = new DecimalInput(BigDecimal.ZERO, Settings.DECIMALFORMAT);
		zaehlerstandVerbrauch.setComment(zaehler.getAbleseEinheit());
		zaehlerstandVerbrauch.setName(Settings.i18n().tr("Verbrauch"));
		return this.zaehlerstandVerbrauch;
	}

	public Zaehler getZaehlerName() {
		return Settings.getZaehler();
	}

	public void handleStore() {
		try {
			Date datum = (Date) getZaehlerstandDatumAnfang().getValue();
			String sql = "SELECT ablese_wert FROM zaehlerstand WHERE ablese_datum < ? ORDER BY ablese_datum DESC";
			EnergieDBService service = Settings.getDBService();
			List<Object> params = new ArrayList<Object>();
			params.add(datum);
			ResultSetExtractor rs = new ResultSetExtractor() {
				public Object extract(ResultSet rs) throws RemoteException, SQLException {
					if (rs.next())
						return rs.getBigDecimal(1);
					return BigDecimal.ZERO;
				}
			};
			BigDecimal letzteAblesung = (BigDecimal) service.execute(sql, params.toArray(), rs);
			Calendar cal = Calendar.getInstance();
			cal.setTime(datum);
			boolean isMonatsLetzter = (cal.getActualMaximum(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));

			Date endeDatum = (Date) getZaehlerstandDatumEnde().getValue();
			Double ver = (Double) getZaehlerstandVerbrauch().getValue();
			BigDecimal verbrauch = (ver == null ? new BigDecimal(0) : BigDecimal.valueOf(ver));
			while (datum.before(endeDatum)) {
				Zaehlerstand zaehlerstand = (Zaehlerstand) Settings.getDBService().createObject(Zaehlerstand.class, null);
				zaehlerstand.setZaehler(Settings.getZaehler());
				zaehlerstand.setAbleseDatum(datum);
				letzteAblesung = letzteAblesung.add(verbrauch);
				zaehlerstand.setAbleseWert(letzteAblesung);
				zaehlerstand.setVerbrauch(verbrauch);
				zaehlerstand.setSchaetzung(true);
				zaehlerstand.setZaehlerwechselEin(false);
				zaehlerstand.setZaehlerwechselAus(false);
				try {
					zaehlerstand.store();
					Application.getMessagingFactory().sendMessage(new StatusBarMessage(
							Settings.i18n().tr("zaehlerstand stored successfully"), StatusBarMessage.TYPE_SUCCESS));
				} catch (ApplicationException e) {
					Application.getMessagingFactory()
							.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
				}
				// Nächster Monat
				cal.add(Calendar.MONTH, 1);
				if (isMonatsLetzter)
					cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				datum = cal.getTime();
			}
		} catch (RemoteException e) {
			Logger.error("error while storing zaehler", e);
			Application.getMessagingFactory().sendMessage(
					new StatusBarMessage(Settings.i18n().tr("error while storing zaehlerstand: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}

}
