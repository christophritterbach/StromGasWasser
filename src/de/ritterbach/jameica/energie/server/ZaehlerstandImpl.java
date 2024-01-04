package de.ritterbach.jameica.energie.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.ritterbach.jameica.energie.rmi.Zaehlerstand;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class ZaehlerstandImpl extends AbstractDBObject implements Zaehlerstand {

	public ZaehlerstandImpl() throws RemoteException {
		super();
	}

	public Zaehler getZaehler() throws RemoteException {
		try {
			return (Zaehler) getAttribute("zaehler_id");
		} catch (ObjectNotFoundException e) {
			return null;
		}
	}

	public void setZaehler(Zaehler zaehler) throws RemoteException {
		setAttribute("zaehler_id", zaehler);
	}

	protected Class<Zaehler> getForeignObject(String field) throws RemoteException {
		// the system is able to resolve foreign keys and loads
		// the according objects automatically. You only have to
		// define which class handles which foreign key.
		if ("zaehler_id".equals(field))
			return Zaehler.class;
		return null;
	}

	@Override
	public Date getAbleseDatum() throws RemoteException {
		return (Date) getAttribute("ablese_datum");
	}

	@Override
	public void setAbleseDatum(Date ableseDatum) throws RemoteException {
		setAttribute("ablese_datum", ableseDatum);
	}

	@Override
	public BigDecimal getAbleseWert() throws RemoteException {
		return (BigDecimal) getAttribute("ablese_wert");
	}

	@Override
	public void setAbleseWert(BigDecimal ableseWert) throws RemoteException {
		setAttribute("ablese_wert", ableseWert);
	}

	public void setAblese_wert(String sAbleseWert) throws RemoteException, ApplicationException {
		setAttribute("ablese_wert", Settings.parseTableEntry(sAbleseWert, "ablese_wert"));
	}

	@Override
	public BigDecimal getVerbrauch() throws RemoteException {
		return (BigDecimal) getAttribute("verbrauch");
	}

	@Override
	public void setVerbrauch(BigDecimal verbrauch) throws RemoteException {
		setAttribute("verbrauch", verbrauch);
	}

	public void setVerbrauch(String sVerbrauch) throws RemoteException, ApplicationException {
		setAttribute("verbrauch", Settings.parseTableEntry(sVerbrauch, "verbrauch"));
	}

	@Override
	public Boolean isSchaetzung() throws RemoteException {
		return Settings.getBoolFromDatabase(getAttribute("schaetzung"), true);
	}

	@Override
	public void setSchaetzung(Boolean schaetzung) throws RemoteException {
		setAttribute("schaetzung", schaetzung ? (int) 1 : (int) 0);
	}

	@Override
	public Boolean isZaehlerwechselAus() throws RemoteException {
		return Settings.getBoolFromDatabase(getAttribute("zaehlerwechsel_aus"), false);
	}

	@Override
	public void setZaehlerwechselAus(Boolean zaehlerwechsleAus) throws RemoteException {
		setAttribute("zaehlerwechsel_aus", zaehlerwechsleAus ? (int) 1 : (int) 0);
	}

	@Override
	public Boolean isZaehlerwechselEin() throws RemoteException {
		return Settings.getBoolFromDatabase(getAttribute("zaehlerwechsel_ein"), false);
	}

	@Override
	public void setZaehlerwechselEin(Boolean zaehlerwechsleEin) throws RemoteException {
		setAttribute("zaehlerwechsel_ein", zaehlerwechsleEin ? (int) 1 : (int) 0);
	}

	@Override
	public String getNotiz() throws RemoteException {
		return (String) getAttribute("notiz");
	}

	@Override
	public void setNotiz(String notiz) throws RemoteException {
		setAttribute("notiz", notiz);
	}

	@Override
	public String getPrimaryAttribute() throws RemoteException {
		return "ablese_datum";
	}

	@Override
	protected String getTableName() {
		return "zaehlerstand";
	}

	protected void deleteCheck() throws ApplicationException {
	}

	protected void insertCheck() throws ApplicationException {
		try {
			if (getAbleseDatum() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter ablese_datum"));

			if (isSchaetzung()) {
				if (getVerbrauch().compareTo(BigDecimal.ZERO) < 0)
					throw new ApplicationException(Settings.i18n().tr("Please enter verbrauch"));
			} else {
				if (getAbleseWert().compareTo(BigDecimal.ZERO) < 0)
					throw new ApplicationException(Settings.i18n().tr("Please enter ablese_wert"));
			}

		} catch (RemoteException e) {
			Logger.error("insert check of ZaehlerWasser failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store zaehlerstand"));
		}
	}

	protected void updateCheck() throws ApplicationException {
		insertCheck();
	}
}
