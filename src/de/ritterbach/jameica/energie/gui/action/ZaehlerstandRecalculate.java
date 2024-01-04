package de.ritterbach.jameica.energie.gui.action;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.EnergieDBService;
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
			list.setOrder("ORDER BY " + service.getSQLTimestamp("ablese_datum") + " ASC, zaehlerwechsel_ein ASC");
			BigDecimal letzterZaehlerstand = null;
			while (list.hasNext()) {
				Zaehlerstand zaehler = (Zaehlerstand) list.next();
				if (letzterZaehlerstand == null) {
					letzterZaehlerstand = zaehler.getAbleseWert().add(BigDecimal.ZERO);
				}
				if (zaehler.isZaehlerwechselAus()) {
					// Wenn der Zähler ausgebaut wird, ist es keine Schätzung mehr.
					// Es kann auch nicht gleichzeitig der Wert des Einbaus sein.
					// Aber wir können den Verbrauch berechnen.
					zaehler.setSchaetzung(false);
					zaehler.setZaehlerwechselEin(false);
					zaehler.setVerbrauch(zaehler.getAbleseWert().subtract(letzterZaehlerstand));
					zaehler.store();
					continue;
				}
				if (zaehler.isZaehlerwechselEin()) {
					// Wenn ein neuer Zähler eingebaut wird, ist es keine Schätzung mehr.
					// Es kann auch nicht gleichzeitig der Wert des Ausbaus sein.
					// Es gab noch keinen Verbrauch. Wir kennen nur den Zählerstand beim Einbau.
					// Und den müssen wir uns merken
					zaehler.setSchaetzung(false);
					zaehler.setZaehlerwechselAus(false);
					zaehler.setVerbrauch(BigDecimal.ZERO);
					letzterZaehlerstand = zaehler.getAbleseWert().add(BigDecimal.ZERO);
					zaehler.store();
					continue;
				}
				if (zaehler.isSchaetzung()) {
					letzterZaehlerstand = letzterZaehlerstand.add(zaehler.getVerbrauch());
					zaehler.setAbleseWert(letzterZaehlerstand);
				} else {
					zaehler.setVerbrauch(zaehler.getAbleseWert().subtract(letzterZaehlerstand));
					letzterZaehlerstand = zaehler.getAbleseWert().add(BigDecimal.ZERO);
				}
				zaehler.store();
			}
			GUI.getCurrentView().reload();
		} catch (RemoteException e) {
			Logger.error("error while updating zaehlerstand", e);
			throw new ApplicationException(Settings.i18n().tr("error while updating zaehlerstand"));
		}
	}
}
