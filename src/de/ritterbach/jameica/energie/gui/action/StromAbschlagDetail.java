package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.AbschlagStrom;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class StromAbschlagDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		AbschlagStrom abschlag = null;

		if (context != null && (context instanceof AbschlagStrom)) {
			abschlag = (AbschlagStrom) context;
		} else {
			try {
				abschlag = (AbschlagStrom) Settings.getDBService().createObject(AbschlagStrom.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new abschlag"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.StromAbschlagDetail.class.getName(), abschlag);
	}

}
