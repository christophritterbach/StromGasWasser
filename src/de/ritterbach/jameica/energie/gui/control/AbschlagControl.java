package de.ritterbach.jameica.energie.gui.control;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Abschlag;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextAreaInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AbschlagControl extends AbstractControl {

	private Abschlag abschlag;
	private DateInput abschlagDatum;
	private DecimalInput abschlagBetrag;
	private TextAreaInput notiz;

	public AbschlagControl(AbstractView view) {
		super(view);
	}

	private Abschlag getAbschlag() {
		if (abschlag != null)
			return abschlag;
		abschlag = (Abschlag) getCurrentObject();
		return abschlag;
	}

	public Input getAbschlagDatum() throws RemoteException {
		if (abschlagDatum != null)
			return abschlagDatum;

		Date datum = getAbschlag().getAbschlagDatum();
		if (datum == null)
			datum = new Date();
		this.abschlagDatum = new DateInput(datum, Settings.DATEFORMAT);
		this.abschlagDatum.setName(Settings.i18n().tr("Abschlag_Datum"));
		return this.abschlagDatum;
	}

	public Input getAbschlagBetrag() throws RemoteException {
		if (abschlagBetrag != null)
			return abschlagBetrag;

		abschlagBetrag = new DecimalInput(getAbschlag().getAbschlagBetrag(), Settings.DECIMALFORMAT);
		abschlagBetrag.setName(Settings.i18n().tr("Abschlag_Betrag"));
		abschlagBetrag.setComment(Settings.CURRENCY);
		return this.abschlagBetrag;
	}

	public Input getNotiz() throws RemoteException {
		if (notiz != null)
			return notiz;
		notiz = new TextAreaInput(getAbschlag().getNotiz());
		notiz.setName("");
		return notiz;
	}

	public String getZaehlerName() throws RemoteException {
		Zaehler z = getAbschlag().getZaehler();
		return z.getName();
	}

	public void handleStore() {
		try {
			Abschlag abschl = getAbschlag();
			abschl.setAbschlagDatum((Date) getAbschlagDatum().getValue());
			Double ab = (Double) getAbschlagBetrag().getValue();
			abschl.setAbschlagbetrag(ab == null ? new BigDecimal(0) : BigDecimal.valueOf(ab));
			abschl.setNotiz((String) getNotiz().getValue());
			try {
				abschl.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("abschlag stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing abschlag", e);
			Application.getMessagingFactory()
					.sendMessage(new StatusBarMessage(
							Settings.i18n().tr("error while storing abschlag: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}
