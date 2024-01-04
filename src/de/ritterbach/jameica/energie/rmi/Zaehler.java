package de.ritterbach.jameica.energie.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;

public interface Zaehler extends DBObject {
	public String getName() throws RemoteException;
	public void setName (String name) throws RemoteException;
	public String getMessEinheit() throws RemoteException;
	public void setMessEinheit (String messEinheit) throws RemoteException;
	public String getAbleseEinheit() throws RemoteException;
	public void setAbleseEinheit(String ableseEinheit) throws RemoteException;
	public Boolean getNutztFaktor() throws RemoteException;
	public void setNutztFaktor(Boolean nutztFaktor) throws RemoteException;
	public Boolean getIstAktiv() throws RemoteException;
	public void setIstAktiv(Boolean istAktiv) throws RemoteException;
	public DBIterator<Kosten> getKosten() throws RemoteException;
	public DBIterator<Abschlag> getAbschlaege() throws RemoteException;
	public DBIterator<Zaehlerstand> getZaehlerstaende() throws RemoteException;
}
