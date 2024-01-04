package de.ritterbach.jameica.energie.gui.control;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.ritterbach.jameica.energie.rmi.Zaehlerstand;
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

public class ZaehlerstandControl extends AbstractControl {

	private Zaehlerstand zaehler;
	private DateInput ablesedatum;
	private DecimalInput ableseWert;
	private DecimalInput verbrauch;
	private CheckboxInput istSchaetzung;
	private CheckboxInput istZaehlerAusbau;
	private CheckboxInput istZaehlerEinbau;
	private TextAreaInput notiz;

	public ZaehlerstandControl(AbstractView view) {
		super(view);
	}

	private Zaehlerstand getZaehlerstand() {
		if (zaehler != null)
			return zaehler;
		zaehler = (Zaehlerstand) getCurrentObject();
		return zaehler;
	}

	public Input getAbleseDatum() throws RemoteException {
		if (ablesedatum != null)
			return ablesedatum;

		Date datum = getZaehlerstand().getAbleseDatum();
		if (datum == null)
			datum = new Date();
		this.ablesedatum = new DateInput(datum, Settings.DATEFORMAT);
		this.ablesedatum.setName(Settings.i18n().tr("Ablese_Datum"));
		return this.ablesedatum;
	}

	public Input getAbleseWert() throws RemoteException {
		if (ableseWert != null)
			return ableseWert;

		Zaehler zaehler = Settings.getZaehler();
		ableseWert = new DecimalInput(getZaehlerstand().getAbleseWert(), Settings.DECIMALFORMAT);
		ableseWert.setComment(zaehler.getAbleseEinheit());
		ableseWert.setName(Settings.i18n().tr("Ablese_Wert", getAbleseEinheit()));
		return this.ableseWert;
	}

	public Input getVerbrauch() throws RemoteException {
		if (verbrauch != null)
			return verbrauch;

		Zaehler zaehler = Settings.getZaehler();
		verbrauch = new DecimalInput(getZaehlerstand().getVerbrauch(), Settings.DECIMALFORMAT);
		verbrauch.setComment(zaehler.getAbleseEinheit());
		verbrauch.setName(Settings.i18n().tr("Verbrauch", getAbleseEinheit()));
		return this.verbrauch;
	}

	public Input getIstSchaetzung() throws RemoteException {
		if (istSchaetzung != null)
			return istSchaetzung;

		istSchaetzung = new CheckboxInput(getZaehlerstand().isSchaetzung());
		istSchaetzung.setComment(Settings.i18n().tr("Verbrauch geschaetzt"));
		istSchaetzung.setName(Settings.i18n().tr("Schaetzung"));
		return this.istSchaetzung;
	}

	public Input getIstZaehlerAusbau() throws RemoteException {
		if (istZaehlerAusbau != null)
			return istZaehlerAusbau;

		istZaehlerAusbau = new CheckboxInput(getZaehlerstand().isZaehlerwechselAus());
		istZaehlerAusbau.setComment(Settings.i18n().tr("Endstand Ausbau"));
		istZaehlerAusbau.setName(Settings.i18n().tr("Endstand"));
		return this.istZaehlerAusbau;
	}

	public Input getIstZaehlerEinbau() throws RemoteException {
		if (istZaehlerEinbau != null)
			return istZaehlerEinbau;

		istZaehlerEinbau = new CheckboxInput(getZaehlerstand().isZaehlerwechselEin());
		istZaehlerEinbau.setComment(Settings.i18n().tr("Anfangsstand Einbau"));
		istZaehlerEinbau.setName(Settings.i18n().tr("Anfangsstand"));
		return this.istZaehlerEinbau;
	}

	public Input getNotiz() throws RemoteException {
		if (notiz != null)
			return notiz;
		notiz = new TextAreaInput(getZaehlerstand().getNotiz());
		notiz.setName("");
		return notiz;
	}

	public String getZaehlerName() throws RemoteException {
		Zaehler z = getZaehlerstand().getZaehler();
		return z.getName();
	}

	public String getAbleseEinheit() throws RemoteException {
		Zaehler z = getZaehlerstand().getZaehler();
		return z.getAbleseEinheit();
	}

	public void handleStore() {
		try {
			Zaehlerstand zaehlerstand = getZaehlerstand();
			zaehlerstand.setAbleseDatum((Date) getAbleseDatum().getValue());
			Double gp = (Double) getAbleseWert().getValue();
			zaehlerstand.setAbleseWert(gp == null ? new BigDecimal(0) : BigDecimal.valueOf(gp));
			Double v = (Double) getVerbrauch().getValue();
			zaehlerstand.setVerbrauch(v == null ? new BigDecimal(0) : BigDecimal.valueOf(v));
			zaehlerstand.setSchaetzung((Boolean) getIstSchaetzung().getValue());
			zaehlerstand.setZaehlerwechselAus((Boolean) getIstZaehlerAusbau().getValue());
			zaehlerstand.setZaehlerwechselEin((Boolean) getIstZaehlerEinbau().getValue());
			zaehlerstand.setNotiz((String) getNotiz().getValue());
			try {
				zaehlerstand.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("zaehlerstand stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing zaehlerstand", e);
			Application.getMessagingFactory().sendMessage(
					new StatusBarMessage(Settings.i18n().tr("error while storing zaehlerstand: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}
