/**********************************************************************
 *
 * Copyright (c) 2004 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.ritterbach.jameica.energie.server;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Connection;

import de.ritterbach.jameica.energie.StromWasserGasPlugin;
import de.ritterbach.jameica.energie.rmi.Version;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.sql.version.UpdateProvider;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;

/**
 * Implementierung eines Update-Providers fuer StromGasWasser.
 */
public class EnergieUpdateProvider implements UpdateProvider
{
  private Version version     = null;
  private Connection conn     = null;
  private Manifest manifest   = null;
  private PluginResources res = null;

  /**
   * ct
   * @param conn Datenbank-Verbindung.
   * @param version Version der Datenbank.
   */
  protected EnergieUpdateProvider(Connection conn, Version version)
  {
    this.conn    = conn;
    this.version = version;
    
    AbstractPlugin p = Application.getPluginLoader().getPlugin(StromWasserGasPlugin.class);
    this.manifest    = p.getManifest();
    this.res         = p.getResources();
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#getConnection()
   */
  public synchronized Connection getConnection() throws ApplicationException
  {
    return this.conn;
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#getCurrentVersion()
   */
  public int getCurrentVersion() throws ApplicationException
  {
    try
    {
      return this.version.getVersion();
    }
    catch (RemoteException re)
    {
      Logger.error("unable to read current version number");
      throw new ApplicationException(res.getI18N().tr("Fehler beim Ermitteln der aktuellen Versionsnummer"));
    }
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#getProgressMonitor()
   */
  public ProgressMonitor getProgressMonitor()
  {
    // Liefert den Splashscreen oder im Servermode einen
    // Pseudo-Monitor.
    return Application.getController().getApplicationCallback().getStartupMonitor();
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#getUpdatePath()
   */
  public File getUpdatePath() throws ApplicationException
  {
    // Ist das Unterverzeichnis "plugins" im Plugin
    return new File(manifest.getPluginDir(),"updates");
  }

  /**
   * @see de.willuhn.sql.version.UpdateProvider#setNewVersion(int)
   */
  public void setNewVersion(int newVersion) throws ApplicationException
  {
    int current = getCurrentVersion();
    try
    {
      Logger.info("applying new version [" + this.version.getName() + "]: " + newVersion);
      this.version.setVersion(newVersion);
      this.version.store();
    }
    catch (Exception e)
    {
      // Im Fehlerfall Versionsnummer zuruecksetzen
      try
      {
        this.version.setVersion(current);
      }
      catch (Exception e2)
      {
        Logger.error("unable to rollback version",e2);
        // Werfen wir nicht, weil es sonst die eigentliche Exception verdecken wuerde
      }

      if (e instanceof ApplicationException)
        throw (ApplicationException) e;
      
      Logger.error("unable to read current version number",e);
      throw new ApplicationException(res.getI18N().tr("Fehler beim Ermitteln der aktuellen Versionsnummer"));
    }
  }
  
  /**
   * Liefert die Plugin-Ressourcen.
   * @return die Plugin-Ressourcen.
   */
  public PluginResources getResources()
  {
    return this.res;
  }

}
