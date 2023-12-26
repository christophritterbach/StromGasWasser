package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.ZaehlerGas;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class GasZaehlerDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		ZaehlerGas zaehler = null;

		if (context != null && (context instanceof ZaehlerGas)) {
			zaehler = (ZaehlerGas) context;
		} else {
			try {
				zaehler = (ZaehlerGas) Settings.getDBService().createObject(ZaehlerGas.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new zaehler"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.GasZaehlerDetail.class.getName(), zaehler);
	}
}
