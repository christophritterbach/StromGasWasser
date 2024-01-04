package de.ritterbach.jameica.energie.gui.action;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Abschlag;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AbschlagDeleteAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		if (context == null || !(context instanceof Abschlag))
			throw new ApplicationException(Settings.i18n().tr("Please choose a abschlag"));
		Abschlag abschlag = (Abschlag) context;
		try {
			// before deleting the abschlag, we show up a confirm dialog ;)
			String question = Settings.i18n().tr("Do you really want to delete this abschlag?");
			if (!Application.getCallback().askUser(question))
				return;
			abschlag.delete();
			// Send Status update message
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					Settings.i18n().tr("abschlag deleted successfully"), StatusBarMessage.TYPE_SUCCESS));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("error while deleting abschlag", e);
			throw new ApplicationException(Settings.i18n().tr("error while deleting abschlag"));
		}
	}
}
