package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.KostenGas;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class GasKostenDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		KostenGas kosten = null;

		if (context != null && (context instanceof KostenGas)) {
			kosten = (KostenGas) context;
		} else {
			try {
				kosten = (KostenGas) Settings.getDBService().createObject(KostenGas.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new kosten"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.GasKostenDetail.class.getName(), kosten);
	}

}
