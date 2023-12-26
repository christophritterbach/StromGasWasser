package de.ritterbach.jameica.energie.gui.control;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.ZaehlerStrom;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextAreaInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class StromZaehlerControl extends AbstractControl {

	private ZaehlerStrom zaehler;
	private DateInput ablesedatum;
	private DecimalInput ableseWert;
	private DecimalInput verbrauch;
	private CheckboxInput istSchaetzung;
	private CheckboxInput istZaehlerAusbau;
	private CheckboxInput istZaehlerEinbau;
	private TextAreaInput notiz;

	public StromZaehlerControl(AbstractView view) {
		super(view);
	}

	private ZaehlerStrom getZaehlerStrom() {
		if (zaehler != null)
			return zaehler;
		zaehler = (ZaehlerStrom) getCurrentObject();
		return zaehler;
	}

	public Input getAbleseDatum() throws RemoteException {
		if (ablesedatum != null)
			return ablesedatum;

		Date datum = getZaehlerStrom().getAbleseDatum();
		if (datum == null)
			datum = new Date();
		this.ablesedatum = new DateInput(datum, Settings.DATEFORMAT);
		this.ablesedatum.setName(Settings.i18n().tr("Ablese_Datum"));
		return this.ablesedatum;
	}

	public Input getAbleseWert() throws RemoteException {
		if (ableseWert != null)
			return ableseWert;

		ableseWert = new DecimalInput(getZaehlerStrom().getAbleseWert(), Settings.DECIMALFORMAT);
		ableseWert.setComment(Settings.i18n().tr("kWh"));
		ableseWert.setName(Settings.i18n().tr("Ablese_Wert"));
		return this.ableseWert;
	}

	public Input getVerbrauch() throws RemoteException {
		if (verbrauch != null)
			return verbrauch;

		verbrauch = new DecimalInput(getZaehlerStrom().getVerbrauch(), Settings.DECIMALFORMAT);
		verbrauch.setComment(Settings.i18n().tr("kWh"));
		verbrauch.setName(Settings.i18n().tr("Verbrauch"));
		return this.verbrauch;
	}

	public Input getIstSchaetzung() throws RemoteException {
		if (istSchaetzung != null)
			return istSchaetzung;

		istSchaetzung = new CheckboxInput(getZaehlerStrom().isSchaetzung());
		istSchaetzung.setComment(Settings.i18n().tr("Verbrauch geschaetzt"));
		istSchaetzung.setName(Settings.i18n().tr("Schaetzung"));
		return this.istSchaetzung;
	}

	public Input getIstZaehlerAusbau() throws RemoteException {
		if (istZaehlerAusbau != null)
			return istZaehlerAusbau;

		istZaehlerAusbau = new CheckboxInput(getZaehlerStrom().isZaehlerwechselAus());
		istZaehlerAusbau.setComment(Settings.i18n().tr("Endstand Ausbau"));
		istZaehlerAusbau.setName(Settings.i18n().tr("Endstand"));
		return this.istZaehlerAusbau;
	}

	public Input getIstZaehlerEinbau() throws RemoteException {
		if (istZaehlerEinbau != null)
			return istZaehlerEinbau;

		istZaehlerEinbau = new CheckboxInput(getZaehlerStrom().isZaehlerwechselEin());
		istZaehlerEinbau.setComment(Settings.i18n().tr("Anfangsstand Einbau"));
		istZaehlerEinbau.setName(Settings.i18n().tr("Anfangsstand"));
		return this.istZaehlerEinbau;
	}

	public Input getNotiz() throws RemoteException {
		if (notiz != null)
			return notiz;
		notiz = new TextAreaInput(getZaehlerStrom().getNotiz());
		notiz.setName("");
		return notiz;
	}

	public void handleStore() {
		try {
			ZaehlerStrom zaehl = getZaehlerStrom();
			zaehl.setAbleseDatum((Date) getAbleseDatum().getValue());
			Double gp = (Double) getAbleseWert().getValue();
			zaehl.setAbleseWert(gp == null ? new BigDecimal(0) : BigDecimal.valueOf(gp));
			Double v = (Double) getVerbrauch().getValue();
			zaehl.setVerbrauch(v == null ? new BigDecimal(0) : BigDecimal.valueOf(v));
			zaehl.setSchaetzung((Boolean) getIstSchaetzung().getValue());
			zaehl.setZaehlerwechselAus((Boolean) getIstZaehlerAusbau().getValue());
			zaehl.setZaehlerwechselEin((Boolean) getIstZaehlerEinbau().getValue());
			zaehl.setNotiz((String) getNotiz().getValue());
			try {
				zaehl.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("zaehler stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing zaehler", e);
			Application.getMessagingFactory().sendMessage(
					new StatusBarMessage(Settings.i18n().tr("error while storing zaehler: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}
