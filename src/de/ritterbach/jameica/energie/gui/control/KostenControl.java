package de.ritterbach.jameica.energie.gui.control;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

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
		this.gueltigVon.setMandatory(true);
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
		this.gueltigBis.setMandatory(true);
		return this.gueltigBis;
	}

	public Input getAbschlagBis() throws RemoteException {
		if (abschlagBis != null)
			return abschlagBis;

		Date abschlag = getKosten().getAbschlagBis();
		this.abschlagBis = new DateInput(abschlag, Settings.DATEFORMAT);
		this.abschlagBis.setName(Settings.i18n().tr("Abschlag_bis"));
		this.abschlagBis.setComment(Settings.i18n().tr("Abschlag_bis_comment"));
		this.abschlagBis.addListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					if (getAbschlagBis() != null) {
						abschlagBis.setValue(getGueltigBis().getValue());
					}
				} catch (RemoteException e) {
				}
				
			}
		});
		return this.abschlagBis;
	}

	public Input getGrundpreis() throws RemoteException {
		if (grundpreis != null)
			return grundpreis;

		this.grundpreis = new DecimalInput(getKosten().getGrundpreis(), Settings.DECIMALFORMAT);
		this.grundpreis.setComment(Settings.i18n().tr("{0}_pro_Jahr", Settings.CURRENCY));
		this.grundpreis.setName(Settings.i18n().tr("Grundpreis"));
		this.grundpreis.setMandatory(true);
		this.grundpreis.setHint(Settings.i18n().tr("Grundpreis inkl. Steuern"));
		this.grundpreis.setValue(0d);
		return this.grundpreis;
	}

	public Input getArbeitspreis() throws RemoteException {
		if (arbeitspreis != null)
			return arbeitspreis;

		Zaehler z = getKosten().getZaehler();
		this.arbeitspreis = new DecimalInput(getKosten().getArbeitspreis(), Settings.ARBEITSPREISFORMAT);
		this.arbeitspreis.setComment(Settings.i18n().tr("{0}_pro_{1}", Settings.CURRENCY, z.getMessEinheit()));
		this.arbeitspreis.setName(Settings.i18n().tr("Arbeitspreis"));
		this.arbeitspreis.setHint(Settings.i18n().tr("Arbeitspreis inkl. Steuern"));
		this.arbeitspreis.setMandatory(true);
		return this.arbeitspreis;
	}

	public Input getFaktor() throws RemoteException {
		if (faktor != null)
			return faktor;

		Zaehler z = getKosten().getZaehler();
		this.faktor = new DecimalInput(getKosten().getFaktor(), Settings.ARBEITSPREISFORMAT);
		this.faktor.setComment(Settings.i18n().tr("{0}_pro_{1}", z.getMessEinheit(), z.getAbleseEinheit()));
		this.faktor.setName(Settings.i18n().tr("Faktor"));
		this.faktor.setMandatory(true);
		this.faktor.setHint(Settings.i18n().tr("Faktor_hint"));
		this.faktor.setValue(1d);
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

	public CheckboxInput getIstAbgerechnet() throws RemoteException {
		if (istAbgerechnet != null)
			return istAbgerechnet;

		istAbgerechnet = new CheckboxInput(getKosten().isAbgerechnet());
		istAbgerechnet.setComment(Settings.i18n().tr("Rechnung wurde erstellt"));
		istAbgerechnet.setName(Settings.i18n().tr("Abgerechnet"));
		return this.istAbgerechnet;
	}

	public CheckboxInput getIstNeuePeriode() throws RemoteException {
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
