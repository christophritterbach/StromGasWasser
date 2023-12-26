/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.ritterbach.jameica.energie.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBObject;

/**
 * Interface fuer einen einzelnen datenbank-gestuetzten Parameter.
 */
public interface DBProperty extends DBObject
{
  /**
   * Liefert den Namen des Parameters.
   * @return Name des Parameters.
   * @throws RemoteException
   */
  public String getName() throws RemoteException;
  
  /**
   * Speichert den Namen des Parameters.
   * @param name Name des Parameters.
   * @throws RemoteException
   */
  public void setName(String name) throws RemoteException;
  
  /**
   * Liefert den Wert des Parameters.
   * @return Wert des Parameters.
   * @throws RemoteException
   */
  public String getValue() throws RemoteException;
  
  /**
   * Speichert den Wert des Parameters.
   * @param value Wert des Parameters.
   * @throws RemoteException
   */
  public void setValue(String value) throws RemoteException;

}
