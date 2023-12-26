package de.ritterbach.jameica.energie.rmi;
import java.math.BigDecimal;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBObject;

public interface KostenGas extends DBObject {
	public Date getGueltigVon() throws RemoteException;
	public void setGueltigVon(Date gueltigVon) throws RemoteException;
	public Date getGueltigBis() throws RemoteException;
	public void setGueltigBis(Date gueltigBis) throws RemoteException;
	public BigDecimal getGrundpreis() throws RemoteException;
	public void setGrundpreis(BigDecimal grundpreis) throws RemoteException;
	public BigDecimal getArbeitspreis() throws RemoteException;
	public void setArbeitspreis(BigDecimal arbeitspreis) throws RemoteException;
	public BigDecimal getFaktor() throws RemoteException;
	public void setFaktor(BigDecimal faktor) throws RemoteException;
	public Boolean isAbgerechnet() throws RemoteException;
	public void setAbgerechnet(Boolean abgerechnet) throws RemoteException;
	public Boolean isRechnungsabschluss() throws RemoteException;
	public void setRechnungsabschluss(Boolean rechnungsabschluss) throws RemoteException;
	public String getNotiz() throws RemoteException;
	public void setNotiz(String Notiz) throws RemoteException;
}
