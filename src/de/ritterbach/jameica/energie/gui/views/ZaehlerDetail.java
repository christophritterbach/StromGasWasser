package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
//import de.willuhn.jameica.example.gui.action.ProjectDelete;
//import de.willuhn.jameica.example.gui.action.TaskDetail;
import de.ritterbach.jameica.energie.gui.control.ZaehlerControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

public class ZaehlerDetail extends AbstractView {

	/**
	 * @see de.willuhn.jameica.gui.AbstractView#bind()
	 */
	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Zaehler details"));


		Container c = new SimpleContainer(getParent());

		// layout with 2 columns
		ColumnLayout columns = new ColumnLayout(c.getComposite(), 2);

		final ZaehlerControl control = new ZaehlerControl(this);
		// left side
		Container left = new SimpleContainer(columns.getComposite());
		left.addHeadline(Settings.i18n().tr("Details"));
		left.addInput(control.getName());
		left.addInput(control.getMessEinheit());
		left.addInput(control.getAbleseEinheit());
		left.addInput(control.getNutztFaktor());

		// add some buttons
		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Store"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button

		buttons.paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
