package de.ritterbach.jameica.energie.gui.action;

import de.ritterbach.jameica.energie.Settings;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class About implements Action{

	@Override
	public void handleAction(Object arg0) throws ApplicationException {
	  	try
	  	{
				new de.ritterbach.jameica.energie.gui.dialog.About(AbstractDialog.POSITION_CENTER).open();
	  	}
	    catch (ApplicationException ae)
	    {
	      throw ae;
	    }
	  	catch (Exception e)
	  	{
	  		Logger.error("error while opening about dialog",e);
	  		throw new ApplicationException(Settings.i18n().tr("Error while opening the About dialog"));
	  	}
	}

}
