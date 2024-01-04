package de.ritterbach.jameica.energie.gui.control;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.gui.parts.AuswertungListPart;
import de.ritterbach.jameica.energie.gui.views.data.Auswertung;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.Part;
import de.willuhn.util.ApplicationException;

public class AuswertungListControl extends AbstractControl {
	private Part list = null;

	  public AuswertungListControl(AbstractView view) {
		super(view);
	}

	  public Part getListe() throws RemoteException
		{
	    if (list != null)
	      return list;
	    list = new AuswertungListPart(new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
			    if (!(context instanceof Auswertung))
			        return;
			}
	    });
	    return list;
		}
}
