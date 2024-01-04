package de.ritterbach.jameica.energie.gui.control;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Kosten;
import de.ritterbach.jameica.energie.rmi.Zaehler;
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

public class KostenControl extends AbstractControl {

	private Kosten kosten;
	private DateInput gueltigVon;
	private DateInput gueltigBis;
	private DateInput abschlagBis;
	private DecimalInput grundpreis;
	private DecimalInput arbeitspreis;
	private DecimalInput faktor;
	private CheckboxInput istAbgerechnet;
	private CheckboxInput istNeuePeriode;
	private TextAreaInput notiz;

	public KostenControl(AbstractView view) {
		super(view);
	}

	private Kosten getKosten() {
		if (kosten != null) {
			return kosten;
		}
		kosten = (Kosten) getCurrentObject();
		return kosten;
	}

	public Input getGueltigVon() throws RemoteException {
		if (gueltigVon != null)
			return gueltigVon;

		Date gueltig = getKosten().getGueltigVon();
		if (gueltig == null)
			gueltig = new Date();
		this.gueltigVon = new DateInput(gueltig, Settings.DATEFORMAT);
		this.gueltigVon.setName(Settings.i18n().tr("Gueltig_von"));
		return this.gueltigVon;
	}

	public Input getGueltigBis() throws RemoteException {
		if (gueltigBis != null)
			return gueltigBis;

		Date gueltig = getKosten().getGueltigBis();
		if (gueltig == null)
			gueltig = new Date();
		this.gueltigBis = new DateInput(gueltig, Settings.DATEFORMAT);
		this.gueltigBis.setName(Settings.i18n().tr("Gueltig_bis"));
		return this.gueltigBis;
	}

	public Input getAbschlagBis() throws RemoteException {
		if (abschlagBis != null)
			return abschlagBis;

		Date abschlag = getKosten().getAbschlagBis();
		this.abschlagBis = new DateInput(abschlag, Settings.DATEFORMAT);
		this.abschlagBis.setName(Settings.i18n().tr("Abschlag_bis"));
		this.abschlagBis.setComment(Settings.i18n().tr("Abschlag_bis_comment"));
		return this.abschlagBis;
	}

	public Input getGrundpreis() throws RemoteException {
		if (grundpreis != null)
			return grundpreis;

		grundpreis = new DecimalInput(getKosten().getGrundpreis(), Settings.DECIMALFORMAT);
		grundpreis.setComment(Settings.i18n().tr("{0}_pro_Jahr", Settings.CURRENCY));
		grundpreis.setName(Settings.i18n().tr("Grundpreis"));
		grundpreis.isMandatory();
		grundpreis.setHint(Settings.i18n().tr("Grundpreis inkl. Steuern"));
		return this.grundpreis;
	}

	public Input getArbeitspreis() throws RemoteException {
		if (arbeitspreis != null)
			return arbeitspreis;

		Zaehler z = getKosten().getZaehler();
		arbeitspreis = new DecimalInput(getKosten().getArbeitspreis(), Settings.ARBEITSPREISFORMAT);
		arbeitspreis.setComment(Settings.i18n().tr("{0}_pro_{1}", Settings.CURRENCY, z.getMessEinheit()));
		arbeitspreis.setName(Settings.i18n().tr("Arbeitspreis"));
		arbeitspreis.setHint(Settings.i18n().tr("Arbeitspreis inkl. Steuern"));
		arbeitspreis.isMandatory();
		return this.arbeitspreis;
	}

	public Input getFaktor() throws RemoteException {
		if (faktor != null)
			return faktor;

		Zaehler z = getKosten().getZaehler();
		faktor = new DecimalInput(getKosten().getFaktor(), Settings.ARBEITSPREISFORMAT);
		faktor.setComment(Settings.i18n().tr("{0}_pro_{1}", z.getMessEinheit(), z.getAbleseEinheit()));
		faktor.setName(Settings.i18n().tr("Faktor"));
		faktor.isMandatory();
		faktor.setHint(Settings.i18n().tr("Faktor_hint"));
		return this.faktor;
	}

	public Boolean nutztFaktor() throws RemoteException {
		Zaehler z = getKosten().getZaehler();
		return z.getNutztFaktor();
	}
	
	public String getZaehlerName() throws RemoteException {
		Zaehler z = getKosten().getZaehler();
		return z.getName();
	}

	public Input getIstAbgerechnet() throws RemoteException {
		if (istAbgerechnet != null)
			return istAbgerechnet;

		istAbgerechnet = new CheckboxInput(getKosten().isAbgerechnet());
		istAbgerechnet.setComment(Settings.i18n().tr("Rechnung wurde erstellt"));
		istAbgerechnet.setName(Settings.i18n().tr("Abgerechnet"));
		return this.istAbgerechnet;
	}

	public Input getIstNeuePeriode() throws RemoteException {
		if (istNeuePeriode != null)
			return istNeuePeriode;

		istNeuePeriode = new CheckboxInput(getKosten().isNeuePeriode());
		istNeuePeriode.setComment(Settings.i18n().tr("Anfang_Abrechnungsperiode"));
		istNeuePeriode.setName(Settings.i18n().tr("Neue_Periode"));
		return this.istNeuePeriode;
	}

	public Input getNotiz() throws RemoteException {
		if (notiz != null)
			return notiz;
		notiz = new TextAreaInput(getKosten().getNotiz());
		notiz.setName("");
		return notiz;
	}

	public void handleStore() {
		try {
			Kosten kost = getKosten();
			kost.setGueltigVon((Date) getGueltigVon().getValue());
			kost.setGueltigBis((Date) getGueltigBis().getValue());
			kost.setAbschlagBis((Date) getAbschlagBis().getValue());
			Double gp = (Double) getGrundpreis().getValue();
			kost.setGrundpreis(gp == null ? BigDecimal.ZERO : BigDecimal.valueOf(gp));
			Double ap = (Double) getArbeitspreis().getValue();
			kost.setArbeitspreis(ap == null ? BigDecimal.ZERO : BigDecimal.valueOf(ap));
			Zaehler z = kost.getZaehler();
			if (z.getNutztFaktor()) {
				Double fakt = (Double) getFaktor().getValue();
				kost.setFaktor(fakt == null ? BigDecimal.ONE : BigDecimal.valueOf(fakt));
			} else {
				kost.setFaktor(BigDecimal.ONE);
			}
			kost.setAbgerechnet((Boolean) getIstAbgerechnet().getValue());
			kost.setNeuePeriode((Boolean) getIstNeuePeriode().getValue());
			kost.setNotiz((String) getNotiz().getValue());
			try {
				kost.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("kosten stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing kosten", e);
			Application.getMessagingFactory().sendMessage(
					new StatusBarMessage(Settings.i18n().tr("error while storing kosten: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}
