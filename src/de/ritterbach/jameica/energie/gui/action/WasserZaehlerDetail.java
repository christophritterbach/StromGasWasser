package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.ZaehlerWasser;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class WasserZaehlerDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		ZaehlerWasser zaehler = null;

		if (context != null && (context instanceof ZaehlerWasser)) {
			zaehler = (ZaehlerWasser) context;
		} else {
			try {
				zaehler = (ZaehlerWasser) Settings.getDBService().createObject(ZaehlerWasser.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new zaehler"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.WasserZaehlerDetail.class.getName(), zaehler);
	}
}
