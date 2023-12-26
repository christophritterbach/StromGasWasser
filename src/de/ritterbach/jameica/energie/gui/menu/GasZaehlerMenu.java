package de.ritterbach.jameica.energie.gui.menu;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.action.GasZaehlerDelete;
import de.ritterbach.jameica.energie.gui.action.GasZaehlerDetail;
import de.ritterbach.jameica.energie.gui.action.GasZaehlerRecalculate;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;

public class GasZaehlerMenu extends ContextMenu {

	public GasZaehlerMenu() {
		// CheckedContextMenuItems will be disabled, if the user clicks into an empty
		// space of the table
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open..."), new GasZaehlerDetail()));
		// separator
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new ContextMenuItem(Settings.i18n().tr("New..."), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				// we force the context to be null to create a new project in any case
				new GasZaehlerDetail().handleAction(null);
			}
		}));
		addItem(new ContextMenuItem(Settings.i18n().tr("New List..."), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				GUI.startView(de.ritterbach.jameica.energie.gui.views.GasZaehlerReiheDetail.class.getName(), null);
				// we force the context to be null to create a new project in any case
			}
		}));
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Delete..."), new GasZaehlerDelete()));
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Calculate..."), new GasZaehlerRecalculate()));
	}

}
