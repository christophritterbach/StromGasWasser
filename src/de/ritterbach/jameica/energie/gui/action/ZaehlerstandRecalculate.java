package de.ritterbach.jameica.energie.gui.action;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.EnergieDBService;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.ritterbach.jameica.energie.rmi.Zaehlerstand;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandRecalculate implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		try {
			EnergieDBService service = Settings.getDBService();
			DBIterator<Zaehlerstand> list = service.createList(Zaehlerstand.class);
			Zaehler zaehler = Settings.getZaehler();
			list.addFilter("zaehler_id=?", zaehler.getID());
			list.setOrder("ORDER BY " + service.getSQLTimestamp("ablese_datum") + " ASC, zaehlerwechsel_ein ASC");
			BigDecimal letzterZaehlerstand = null;
			while (list.hasNext()) {
				Zaehlerstand zaehlerstand = (Zaehlerstand) list.next();
				if (letzterZaehlerstand == null) {
					letzterZaehlerstand = zaehlerstand.getAbleseWert().add(BigDecimal.ZERO);
				}
				if (zaehlerstand.isZaehlerwechselAus()) {
					// Wenn der Zähler ausgebaut wird, ist es keine Schätzung mehr.
					// Es kann auch nicht gleichzeitig der Wert des Einbaus sein.
					// Aber wir können den Verbrauch berechnen.
					zaehlerstand.setSchaetzung(false);
					zaehlerstand.setZaehlerwechselEin(false);
					zaehlerstand.setVerbrauch(zaehlerstand.getAbleseWert().subtract(letzterZaehlerstand));
					zaehlerstand.store();
					continue;
				}
				if (zaehlerstand.isZaehlerwechselEin()) {
					// Wenn ein neuer Zähler eingebaut wird, ist es keine Schätzung mehr.
					// Es kann auch nicht gleichzeitig der Wert des Ausbaus sein.
					// Es gab noch keinen Verbrauch. Wir kennen nur den Zählerstand beim Einbau.
					// Und den müssen wir uns merken
					zaehlerstand.setSchaetzung(false);
					zaehlerstand.setZaehlerwechselAus(false);
					zaehlerstand.setVerbrauch(BigDecimal.ZERO);
					letzterZaehlerstand = zaehlerstand.getAbleseWert().add(BigDecimal.ZERO);
					zaehlerstand.store();
					continue;
				}
				if (zaehlerstand.isSchaetzung()) {
					letzterZaehlerstand = letzterZaehlerstand.add(zaehlerstand.getVerbrauch());
					zaehlerstand.setAbleseWert(letzterZaehlerstand);
				} else {
					zaehlerstand.setVerbrauch(zaehlerstand.getAbleseWert().subtract(letzterZaehlerstand));
					letzterZaehlerstand = zaehlerstand.getAbleseWert().add(BigDecimal.ZERO);
				}
				zaehlerstand.store();
			}
			GUI.getCurrentView().reload();
		} catch (RemoteException e) {
			Logger.error("error while updating zaehlerstand", e);
			throw new ApplicationException(Settings.i18n().tr("error while updating zaehlerstand"));
		}
	}
}
