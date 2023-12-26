package de.ritterbach.jameica.energie.rmi;
import java.math.BigDecimal;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBObject;

public interface AbschlagWasser extends DBObject {
	public Date getAbschlagDatum() throws RemoteException;
	public void setAbschlagDatum(Date abschlagDatum) throws RemoteException;
	public BigDecimal getAbschlagBetrag() throws RemoteException;
	public void setAbschlagbetrag(BigDecimal abschlagBetrag) throws RemoteException;
	public String getNotiz() throws RemoteException;
	public void setNotiz(String Notiz) throws RemoteException;
}
