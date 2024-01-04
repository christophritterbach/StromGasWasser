package de.ritterbach.jameica.energie.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.views.ZaehlerstandDetail;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.ritterbach.jameica.energie.rmi.Zaehlerstand;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandDetailAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Zaehlerstand zaehlerstand = null;

		if (context != null && (context instanceof Zaehlerstand)) {
			zaehlerstand = (Zaehlerstand) context;
		} else {
			try {
				zaehlerstand = (Zaehlerstand) Settings.getDBService().createObject(Zaehlerstand.class, null);
				Zaehler zaehler = Settings.getZaehler();
				if (zaehler != null) {
					zaehlerstand.setZaehler(zaehler);
				}
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new zaehlerstand"), e);
			}
		}
		GUI.startView(ZaehlerstandDetail.class.getName(), zaehlerstand);
	}

}
