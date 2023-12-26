package de.ritterbach.jameica.energie.gui.action;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.ZaehlerStrom;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class StromZaehlerDelete implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		if (context == null || !(context instanceof ZaehlerStrom))
			throw new ApplicationException(Settings.i18n().tr("Please choose a zaehler"));
		ZaehlerStrom zw = (ZaehlerStrom) context;
		try {
			String question = Settings.i18n().tr("Do you really want to delete this zaehler?");
			if (!Application.getCallback().askUser(question))
				return;
			zw.delete();
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					Settings.i18n().tr("zaehler deleted successfully"), StatusBarMessage.TYPE_SUCCESS));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("error while deleting zaehler", e);
			throw new ApplicationException(Settings.i18n().tr("error while deleting zaehler"));
		}
	}
}
