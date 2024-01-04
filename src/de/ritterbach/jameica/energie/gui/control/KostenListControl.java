package de.ritterbach.jameica.energie.gui.control;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.gui.parts.KostenListPart;
import de.ritterbach.jameica.energie.rmi.Kosten;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.Part;
import de.willuhn.util.ApplicationException;

public class KostenListControl extends AbstractControl {
	private Part list = null;

	  public KostenListControl(AbstractView view) {
		super(view);
	}

	  public Part getListe() throws RemoteException
		{
	    if (list != null)
	      return list;
	    list = new KostenListPart(new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
			    if (!(context instanceof Kosten))
			        return;
			}
	    });
	    return list;
		}
}
