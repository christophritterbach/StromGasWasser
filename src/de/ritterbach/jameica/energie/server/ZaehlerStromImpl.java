package de.ritterbach.jameica.energie.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.ZaehlerStrom;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class ZaehlerStromImpl extends AbstractDBObject implements ZaehlerStrom {

	public ZaehlerStromImpl() throws RemoteException {
		super();
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
		return "zaehler_strom";
	}

	/**
	 * This method will be called, before delete() is executed. Here you can make
	 * some dependency checks. If you dont want to delete the project (in case of
	 * failed dependencies) you have to throw an ApplicationException. The message
	 * of this one will be shown in users UI. So please translate the text into the
	 * users language.
	 * 
	 * @see de.willuhn.datasource.db.AbstractDBObject#deleteCheck()
	 */
	protected void deleteCheck() throws ApplicationException {
	}

	/**
	 * This method is invoked before executing insert(). So lets check the entered
	 * data.
	 * 
	 * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
	 */
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
			throw new ApplicationException(Settings.i18n().tr("unable to store zaehler"));
		}
	}

	/**
	 * This method is invoked before every update().
	 * 
	 * @see de.willuhn.datasource.db.AbstractDBObject#updateCheck()
	 */
	protected void updateCheck() throws ApplicationException {
		// we simply call the insertCheck here ;)
		insertCheck();
	}
}
