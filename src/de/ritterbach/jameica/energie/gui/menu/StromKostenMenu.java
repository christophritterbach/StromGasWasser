package de.ritterbach.jameica.energie.gui.menu;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.action.StromKostenDelete;
import de.ritterbach.jameica.energie.gui.action.StromKostenDetail;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;

public class StromKostenMenu extends ContextMenu {

	public StromKostenMenu() {
		// CheckedContextMenuItems will be disabled, if the user clicks into an empty space of the table
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open..."),new StromKostenDetail()));
		// separator
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new ContextMenuItem(Settings.i18n().tr("New..."),new Action()
		{
			public void handleAction(Object context) throws ApplicationException
			{
				// we force the context to be null to create a new project in any case
				new StromKostenDetail().handleAction(null);
			}
		}));
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Delete..."),new StromKostenDelete()));
	}

}
