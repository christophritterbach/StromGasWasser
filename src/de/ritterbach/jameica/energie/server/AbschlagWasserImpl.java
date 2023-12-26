package de.ritterbach.jameica.energie.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.AbschlagWasser;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AbschlagWasserImpl extends AbstractDBObject implements AbschlagWasser {

	public AbschlagWasserImpl() throws RemoteException {
		super();
	}

	@Override
	public Date getAbschlagDatum() throws RemoteException {
		return (Date) getAttribute("abschlag_datum");
	}

	@Override
	public void setAbschlagDatum(Date abschlagDatum) throws RemoteException {
		setAttribute("abschlag_datum", abschlagDatum);
	}

	@Override
	public BigDecimal getAbschlagBetrag() throws RemoteException {
		return (BigDecimal) getAttribute("abschlag_betrag");
	}

	@Override
	public void setAbschlagbetrag(BigDecimal abschlagBetrag) throws RemoteException {
		setAttribute("abschlag_betrag", abschlagBetrag);
	}

	public void setAbschlag_betrag(String sAbschlagBetrag) throws RemoteException, ApplicationException {
		setAttribute("abschlag_betrag", Settings.parseTableEntry(sAbschlagBetrag, "abschlag"));
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
		return "abschlag_datum";
	}

	@Override
	protected String getTableName() {
		return "abschlag_wasser";
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
			if (getAbschlagDatum() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter abschlag_datum"));

			if (getAbschlagBetrag().equals(BigDecimal.ZERO))
				throw new ApplicationException(Settings.i18n().tr("Please enter abschlag_betrag"));

		} catch (RemoteException e) {
			Logger.error("insert check of project failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store abschlag"));
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
