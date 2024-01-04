package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.control.AbschlagListControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class AbschlagList extends AbstractView {

	public AbschlagList() {
		super();
	}

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Abschlag list"));
		AbschlagListControl control = new AbschlagListControl(this);
		control.getListe().paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
