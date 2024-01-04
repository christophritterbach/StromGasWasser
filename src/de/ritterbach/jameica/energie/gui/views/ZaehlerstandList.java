package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.control.ZaehlerstandListControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandList extends AbstractView {

	public ZaehlerstandList() {
		super();
	}

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Zaehlerstand list"));
		ZaehlerstandListControl control = new ZaehlerstandListControl(this);
		control.getListe().paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
