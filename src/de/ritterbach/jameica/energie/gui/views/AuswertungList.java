package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.control.AuswertungListControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class AuswertungList extends AbstractView {

	public AuswertungList() {
		super();
	}

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Auswertung list"));
		AuswertungListControl control = new AuswertungListControl(this);
		control.getListe().paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
