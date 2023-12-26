package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.ZaehlerStrom;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class StromZaehlerDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		ZaehlerStrom zaehler = null;

		if (context != null && (context instanceof ZaehlerStrom)) {
			zaehler = (ZaehlerStrom) context;
		} else {
			try {
				zaehler = (ZaehlerStrom) Settings.getDBService().createObject(ZaehlerStrom.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new zaehler"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.StromZaehlerDetail.class.getName(), zaehler);
	}
}
