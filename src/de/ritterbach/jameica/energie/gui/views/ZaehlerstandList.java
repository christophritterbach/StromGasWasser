package de.ritterbach.jameica.energie.gui.views;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.gui.control.ZaehlerstandListControl;
import de.ritterbach.jameica.energie.gui.parts.ZaehlerstandListPart;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandList extends AbstractView {

	private ZaehlerstandListControl control;

	public ZaehlerstandList() {
		super();
		control = new ZaehlerstandListControl(this);
	}

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Zaehlerstand list"));
		control.getListe().paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

	@Override
	public void reload() throws ApplicationException {
		Logger.info("Reload");
		try {
			((ZaehlerstandListPart)control.getListe()).handleReload(true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
