package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.control.KostenListControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class KostenList extends AbstractView {

	public KostenList() {
		super();
	}

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Kosten list"));
		KostenListControl control = new KostenListControl(this);
		control.getListe().paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
