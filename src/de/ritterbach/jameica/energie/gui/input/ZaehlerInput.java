package de.ritterbach.jameica.energie.gui.input;

import java.util.List;

import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.jameica.gui.input.SelectInput;

public class ZaehlerInput extends SelectInput {
	public ZaehlerInput(List<Zaehler> list, Object preselected) {
		super(list, preselected);
	}
}
