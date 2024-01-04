package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class ZaehlerDetail implements Action {

	public void handleAction(Object context) throws ApplicationException {

		Zaehler zaehler = null;

		// check if the context is a zaheler
		if (context != null && (context instanceof Zaehler)) {
			zaehler = (Zaehler) context;
		} else {
			try {
				// create new zaehler
				zaehler = (Zaehler) Settings.getDBService().createObject(Zaehler.class, null);
				zaehler.setIstAktiv(true);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new zaehler"), e);
			}
		}

		// ok, lets start the dialog
		GUI.startView(de.ritterbach.jameica.energie.gui.views.ZaehlerDetail.class.getName(), zaehler);
	}
}
