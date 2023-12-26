package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.AbschlagGas;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class GasAbschlagDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		AbschlagGas abschlag = null;

		if (context != null && (context instanceof AbschlagGas)) {
			abschlag = (AbschlagGas) context;
		} else {
			try {
				abschlag = (AbschlagGas) Settings.getDBService().createObject(AbschlagGas.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new abschlag"), e);
			}
		}
		GUI.startView(de.ritterbach.jameica.energie.gui.views.GasAbschlagDetail.class.getName(), abschlag);
	}

}
