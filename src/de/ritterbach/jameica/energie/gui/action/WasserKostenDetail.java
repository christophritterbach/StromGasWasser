package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.KostenWasser;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class WasserKostenDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		KostenWasser kosten = null;

		if (context != null && (context instanceof KostenWasser)) {
			kosten = (KostenWasser) context;
		} else {
			try {
				kosten = (KostenWasser) Settings.getDBService().createObject(KostenWasser.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new kosten"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.WasserKostenDetail.class.getName(), kosten);
	}

}
