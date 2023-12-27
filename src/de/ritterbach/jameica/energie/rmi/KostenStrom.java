package de.ritterbach.jameica.energie.rmi;
import java.math.BigDecimal;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBObject;

public interface KostenStrom extends DBObject {
	public Date getGueltigVon() throws RemoteException;
	public void setGueltigVon(Date gueltigVon) throws RemoteException;
	public Date getGueltigBis() throws RemoteException;
	public void setGueltigBis(Date gueltigBis) throws RemoteException;
	public Date getAbschlagBis() throws RemoteException;
	public void setAbschlagBis(Date abschlagBis) throws RemoteException;
	public BigDecimal getGrundpreis() throws RemoteException;
	public void setGrundpreis(BigDecimal grundpreis) throws RemoteException;
	public BigDecimal getArbeitspreis() throws RemoteException;
	public void setArbeitspreis(BigDecimal arbeitspreis) throws RemoteException;
	public Boolean isAbgerechnet() throws RemoteException;
	public void setAbgerechnet(Boolean abgerechnet) throws RemoteException;
	public Boolean isNeuePeriode() throws RemoteException;
	public void setNeuePeriode(Boolean neuePeriode) throws RemoteException;
	public String getNotiz() throws RemoteException;
	public void setNotiz(String Notiz) throws RemoteException;
}
