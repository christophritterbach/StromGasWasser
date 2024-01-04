package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.views.AbschlagDetail;
import de.ritterbach.jameica.energie.rmi.Abschlag;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class AbschlagDetailAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Abschlag abschlag = null;

		if (context != null && (context instanceof Abschlag)) {
			abschlag = (Abschlag) context;
		} else {
			try {
				abschlag = (Abschlag) Settings.getDBService().createObject(Abschlag.class, null);
				Zaehler zaehler = Settings.getZaehler();
				if (zaehler != null) {
					abschlag.setZaehler(zaehler);
				}
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new abschlag"), e);
			}
		}
		GUI.startView(AbschlagDetail.class.getName(), abschlag);
	}

}
