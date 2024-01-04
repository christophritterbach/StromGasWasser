package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.views.KostenDetail;
import de.ritterbach.jameica.energie.rmi.Kosten;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class KostenDetailAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Kosten kosten = null;
		if (context != null && (context instanceof Kosten)) {
			kosten = (Kosten) context;
		} else {
			try {
				kosten = (Kosten) Settings.getDBService().createObject(Kosten.class, null);
				Zaehler zaehler = Settings.getZaehler();
				if (zaehler != null) {
					kosten.setZaehler(zaehler);
				}
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new kosten"), e);
			}
		}
		GUI.startView(KostenDetail.class.getName(), kosten);
	}
}
