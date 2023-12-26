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
import de.ritterbach.jameica.energie.rmi.ZaehlerGas;
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

public class GasZaehlerReiheControl extends AbstractControl {

	private DateInput zaehlerDatumAnfang;
	private DateInput zaehlerDatumEnde;
	private DecimalInput zaehlerVerbrauch;

	public GasZaehlerReiheControl(AbstractView view) {
		super(view);
	}

	public Input getZaehlerDatumAnfang() throws RemoteException {
		if (zaehlerDatumAnfang != null)
			return zaehlerDatumAnfang;

		Date datum = new Date();
		this.zaehlerDatumAnfang = new DateInput(datum, Settings.DATEFORMAT);
		this.zaehlerDatumAnfang.setName(Settings.i18n().tr("Ablese_Datum_Anfang"));
		return this.zaehlerDatumAnfang;
	}

	public Input getZaehlerDatumEnde() throws RemoteException {
		if (zaehlerDatumEnde != null)
			return zaehlerDatumEnde;

		Date datum = new Date();
		this.zaehlerDatumEnde = new DateInput(datum, Settings.DATEFORMAT);
		this.zaehlerDatumEnde.setName(Settings.i18n().tr("Ablese_Datum_Ende"));
		return this.zaehlerDatumEnde;
	}

	public Input getZaehlerVerbrauch() throws RemoteException {
		if (zaehlerVerbrauch != null)
			return zaehlerVerbrauch;

		zaehlerVerbrauch = new DecimalInput(BigDecimal.ZERO, Settings.DECIMALFORMAT);
		zaehlerVerbrauch.setComment(Settings.i18n().tr("qm"));
		zaehlerVerbrauch.setName(Settings.i18n().tr("Verbrauch"));
		return this.zaehlerVerbrauch;
	}

	public void handleStore() {
		try {
			Date datum = (Date) getZaehlerDatumAnfang().getValue();
			String sql = "SELECT ablese_wert FROM zaehler_wasser WHERE ablese_datum < ? ORDER BY ablese_wert DESC";
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

			Date endeDatum = (Date) getZaehlerDatumEnde().getValue();
			Double ver = (Double) getZaehlerVerbrauch().getValue();
			BigDecimal verbrauch = (ver == null ? new BigDecimal(0) : BigDecimal.valueOf(ver));
			while (datum.before(endeDatum)) {
				ZaehlerGas zw = (ZaehlerGas) Settings.getDBService().createObject(ZaehlerGas.class, null);
				zw.setAbleseDatum(datum);
				letzteAblesung = letzteAblesung.add(verbrauch);
				zw.setAbleseWert(letzteAblesung);
				zw.setVerbrauch(verbrauch);
				zw.setSchaetzung(true);
				zw.setZaehlerwechselEin(false);
				zw.setZaehlerwechselAus(false);
				try {
					zw.store();
					Application.getMessagingFactory().sendMessage(new StatusBarMessage(
							Settings.i18n().tr("zaehler stored successfully"), StatusBarMessage.TYPE_SUCCESS));
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
					new StatusBarMessage(Settings.i18n().tr("error while storing zaehler: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}

}
