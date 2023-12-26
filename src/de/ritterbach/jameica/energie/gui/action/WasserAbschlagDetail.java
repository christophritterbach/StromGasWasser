package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.AbschlagWasser;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class WasserAbschlagDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		AbschlagWasser abschlag = null;

		if (context != null && (context instanceof AbschlagWasser)) {
			abschlag = (AbschlagWasser) context;
		} else {
			try {
				abschlag = (AbschlagWasser) Settings.getDBService().createObject(AbschlagWasser.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new abschlag"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.WasserAbschlagDetail.class.getName(), abschlag);
	}

}
