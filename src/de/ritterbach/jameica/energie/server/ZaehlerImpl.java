package de.ritterbach.jameica.energie.server;

import java.rmi.RemoteException;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Abschlag;
import de.ritterbach.jameica.energie.rmi.Kosten;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.ritterbach.jameica.energie.rmi.Zaehlerstand;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class ZaehlerImpl extends AbstractDBObject implements Zaehler {

	public ZaehlerImpl() throws RemoteException {
		super();
		this.setIstAktiv(true);
	}

	@Override
	public String getName() throws RemoteException {
		return (String) getAttribute("name");
	}

	@Override
	public void setName(String name) throws RemoteException {
		setAttribute("name", name);
	}

	@Override
	public String getMessEinheit() throws RemoteException {
		return (String) getAttribute("mess_einheit");
	}

	@Override
	public void setMessEinheit(String messEinheit) throws RemoteException {
		setAttribute("mess_einheit", messEinheit);
	}

	@Override
	public String getAbleseEinheit() throws RemoteException {
		return (String) getAttribute("ablese_einheit");
	}

	@Override
	public void setAbleseEinheit(String ableseEinheit) throws RemoteException {
		setAttribute("ablese_einheit", ableseEinheit);
	}
	@Override
	public Boolean getNutztFaktor() throws RemoteException {
		return Settings.getBoolFromDatabase(getAttribute("nutzt_faktor"), false);
	}

	@Override
	public void setNutztFaktor(Boolean nutztFaktor) throws RemoteException {
		setAttribute("nutzt_faktor", nutztFaktor ? (int) 1 : (int) 0);
	}

	public Boolean getIstAktiv() throws RemoteException {
		return Settings.getBoolFromDatabase(getAttribute("ist_aktiv"), false);
	}
	
	public void setIstAktiv(Boolean istAktiv) throws RemoteException {
		setAttribute("ist_aktiv", istAktiv ? (int) 1 : (int) 0);
	}

	@Override
	public String getPrimaryAttribute() throws RemoteException {
		return "name";
	}

	@Override
	protected String getTableName() {
		return "zaehler";
	}

	@Override
	public DBIterator<Kosten> getKosten() throws RemoteException {
		try {
			DBService service = this.getService();
			DBIterator<Kosten> kosten = service.createList(Kosten.class);
			kosten.addFilter("zaehler_id = " + this.getID());
			return kosten;
		} catch (Exception e) {
			throw new RemoteException("unable to load kosten list", e);
		}
	}

	@Override
	public DBIterator<Abschlag> getAbschlaege() throws RemoteException {
		try {
			DBService service = this.getService();
			DBIterator<Abschlag> abschlag = service.createList(Abschlag.class);
			abschlag.addFilter("zaehler_id = " + this.getID());
			return abschlag;
		} catch (Exception e) {
			throw new RemoteException("unable to load abschlag list", e);
		}
	}

	@Override
	public DBIterator<Zaehlerstand> getZaehlerstaende() throws RemoteException {
		try {
			DBService service = this.getService();
			DBIterator<Zaehlerstand> zaehlerstand = service.createList(Zaehlerstand.class);
			zaehlerstand.addFilter("zaehler_id = " + this.getID());
			return zaehlerstand;
		} catch (Exception e) {
			throw new RemoteException("unable to load zaehlerstand list", e);
		}
	}

	public void delete() throws RemoteException, ApplicationException {
		try {
			this.transactionBegin();
			DBIterator<Zaehlerstand> zaehlerstaende = getZaehlerstaende();
			while (zaehlerstaende.hasNext()) {
				Zaehlerstand z = zaehlerstaende.next();
				z.delete();
			}
			DBIterator<Abschlag> abschlaege = getAbschlaege();
			while (abschlaege.hasNext()) {
				Abschlag a = abschlaege.next();
				a.delete();
			}
			DBIterator<Kosten> kosten = getKosten();
			while (kosten.hasNext()) {
				Kosten k = kosten.next();
				k.delete();
			}
			super.delete(); // we delete the zaehler itself
			this.transactionCommit();

		} catch (RemoteException re) {
			this.transactionRollback();
			throw re;
		} catch (ApplicationException ae) {
			this.transactionRollback();
			throw ae;
		} catch (Throwable t) {
			this.transactionRollback();
			throw new ApplicationException(Settings.i18n().tr("error while deleting zaehler"), t);
		}
	}

	/**
	 * This method is invoked before executing insert(). So lets check the entered
	 * data.
	 * 
	 * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
	 */
	protected void insertCheck() throws ApplicationException {
		try {
			if (getName() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter zaehler_name"));

		} catch (RemoteException e) {
			Logger.error("insert check of Zaehler failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store zaehler"));
		}
	}

	protected void updateCheck() throws ApplicationException {
		insertCheck();
	}

	protected void deleteCheck() throws ApplicationException {
	}
}
