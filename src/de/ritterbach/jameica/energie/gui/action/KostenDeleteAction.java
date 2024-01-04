package de.ritterbach.jameica.energie.gui.action;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Kosten;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class KostenDeleteAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		if (context == null || !(context instanceof Kosten))
			throw new ApplicationException(Settings.i18n().tr("Please choose a kosten"));
		Kosten kosten = (Kosten) context;
		try {
			// before deleting the kosten, we show up a confirm dialog ;)
			String question = Settings.i18n().tr("Do you really want to delete this kosten?");
			if (!Application.getCallback().askUser(question))
				return;
			kosten.delete();
			// Send Status update message
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					Settings.i18n().tr("kosten deleted successfully"), StatusBarMessage.TYPE_SUCCESS));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("error while deleting kosten", e);
			throw new ApplicationException(Settings.i18n().tr("error while deleting kosten"));
		}
	}
}
