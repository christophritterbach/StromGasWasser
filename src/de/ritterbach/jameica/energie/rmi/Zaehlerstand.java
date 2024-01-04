package de.ritterbach.jameica.energie.rmi;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBObject;

public interface Zaehlerstand extends DBObject {
	public Zaehler getZaehler() throws RemoteException;
	public void setZaehler(Zaehler zaehler) throws RemoteException;
	public Date getAbleseDatum() throws RemoteException;
	public void setAbleseDatum(Date ableseDatum) throws RemoteException;
	public BigDecimal getAbleseWert() throws RemoteException;
	public void setAbleseWert(BigDecimal ableseWert) throws RemoteException;
	public BigDecimal getVerbrauch() throws RemoteException;
	public void setVerbrauch(BigDecimal verbrauch) throws RemoteException;
	public Boolean isSchaetzung() throws RemoteException;
	public void setSchaetzung(Boolean schaetzung) throws RemoteException;
	public Boolean isZaehlerwechselAus() throws RemoteException;
	public void setZaehlerwechselAus(Boolean zaehlerwechsleAus) throws RemoteException;
	public Boolean isZaehlerwechselEin() throws RemoteException;
	public void setZaehlerwechselEin(Boolean zaehlerwechsleEin) throws RemoteException;
	public String getNotiz() throws RemoteException;
	public void setNotiz(String Notiz) throws RemoteException;
}
