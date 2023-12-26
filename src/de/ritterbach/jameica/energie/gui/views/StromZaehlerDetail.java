package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.action.StromZaehlerDelete;
import de.ritterbach.jameica.energie.gui.control.StromZaehlerControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

public class StromZaehlerDetail extends AbstractView {

	public StromZaehlerDetail() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bind() throws Exception {
		// draw the title
		GUI.getView().setTitle(Settings.i18n().tr("ZaehlerStrom details"));
		final StromZaehlerControl control = new StromZaehlerControl(this);
		Container c = new SimpleContainer(getParent());
		// layout with 2 columns
		ColumnLayout columns = new ColumnLayout(c.getComposite(), 2);

		// left side
		Container left = new SimpleContainer(columns.getComposite());
		left.addHeadline(Settings.i18n().tr("Details"));
		left.addInput(control.getAbleseDatum());
		left.addInput(control.getAbleseWert());
		left.addInput(control.getVerbrauch());
		left.addInput(control.getIstSchaetzung());
		left.addInput(control.getIstZaehlerAusbau());
		left.addInput(control.getIstZaehlerEinbau());

		// right side
		Container right = new SimpleContainer(columns.getComposite(), true);
		right.addHeadline(Settings.i18n().tr("Notiz"));
		right.addInput(control.getNotiz());

		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Delete"), new StromZaehlerDelete(), control.getCurrentObject());
		buttons.addButton(Settings.i18n().tr("Store"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button
		// Don't forget to paint the button area
		buttons.paint(getParent());
	}
}
