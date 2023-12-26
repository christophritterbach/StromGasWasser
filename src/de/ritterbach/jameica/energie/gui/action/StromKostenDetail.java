package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.KostenStrom;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class StromKostenDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		KostenStrom kosten = null;

		if (context != null && (context instanceof KostenStrom)) {
			kosten = (KostenStrom) context;
		} else {
			try {
				kosten = (KostenStrom) Settings.getDBService().createObject(KostenStrom.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new kosten"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.StromKostenDetail.class.getName(), kosten);
	}

}
