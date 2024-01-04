package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.control.ZaehlerstandReiheControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandReiheDetail extends AbstractView {

	@Override
	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Zaehlerstand list details"));
		final ZaehlerstandReiheControl control = new ZaehlerstandReiheControl(this);
		Container c = new SimpleContainer(getParent());
		// layout with 2 columns
		ColumnLayout columns = new ColumnLayout(c.getComposite(), 1);

		// left side
		Container left = new SimpleContainer(columns.getComposite());
		left.addHeadline(control.getZaehlerName().getName());
		left.addInput(control.getZaehlerstandDatumAnfang());
		left.addInput(control.getZaehlerstandDatumEnde());
		left.addInput(control.getZaehlerstandVerbrauch());

		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Store"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button
		buttons.paint(getParent());
	}

}
