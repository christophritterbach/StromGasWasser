package de.ritterbach.jameica.energie.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Abschlag;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AbschlagImpl extends AbstractDBObject implements Abschlag {

	public AbschlagImpl() throws RemoteException {
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
		return "abschlag";
	}

	protected void deleteCheck() throws ApplicationException {
	}

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

	protected void updateCheck() throws ApplicationException {
		insertCheck();
	}
}
