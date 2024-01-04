package de.ritterbach.jameica.energie.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class AbschlagAction implements Action {

	@Override
	public void handleAction(Object arg0) throws ApplicationException {
		GUI.startView(de.ritterbach.jameica.energie.gui.views.AbschlagList.class.getName(), null);
	}
}
