package de.ritterbach.jameica.energie.gui.control;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.gui.parts.ZaehlerstandListPart;
import de.ritterbach.jameica.energie.rmi.Zaehlerstand;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.Part;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandListControl extends AbstractControl {
	private Part list = null;

	  public ZaehlerstandListControl(AbstractView view) {
		super(view);
	}

	  public Part getListe() throws RemoteException
		{
	    if (list != null)
	      return list;
	    list = new ZaehlerstandListPart(new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
			    if (!(context instanceof Zaehlerstand))
			        return;
			}
	    });
	    return list;
		}
}
