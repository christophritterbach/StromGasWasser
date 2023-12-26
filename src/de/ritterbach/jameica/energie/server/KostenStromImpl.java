package de.ritterbach.jameica.energie.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.EnergieDBService;
import de.ritterbach.jameica.energie.rmi.KostenStrom;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class KostenStromImpl extends AbstractDBObject implements KostenStrom {

	public KostenStromImpl() throws RemoteException {
		super();
	}

	@Override
	public Date getGueltigVon() throws RemoteException {
		return (Date) getAttribute("gueltig_von");
	}

	@Override
	public void setGueltigVon(Date gueltigVon) throws RemoteException {
		setAttribute("gueltig_von", gueltigVon);
	}

	@Override
	public Date getGueltigBis() throws RemoteException {
		return (Date) getAttribute("gueltig_bis");
	}

	@Override
	public void setGueltigBis(Date gueltigBis) throws RemoteException {
		setAttribute("gueltig_bis", gueltigBis);
	}

	@Override
	public BigDecimal getGrundpreis() throws RemoteException {
		return (BigDecimal) getAttribute("grundpreis");
	}

	@Override
	public void setGrundpreis(BigDecimal grundpreis) throws RemoteException {
		setAttribute("grundpreis", grundpreis);
	}

	@Override
	public BigDecimal getArbeitspreis() throws RemoteException {
		return (BigDecimal) getAttribute("arbeitspreis");
	}

	@Override
	public void setArbeitspreis(BigDecimal arbeitspreis) throws RemoteException {
		setAttribute("arbeitspreis", arbeitspreis);
	}

	@Override
	public Boolean isAbgerechnet() throws RemoteException {
		return Settings.getBoolFromDatabase(getAttribute("abgerechnet"), false);
	}

	@Override
	public void setAbgerechnet(Boolean abgerechnet) throws RemoteException {
		setAttribute("abgerechnet", abgerechnet ? 1 : 0);
	}

	@Override
	public Boolean isRechnungsabschluss() throws RemoteException {
		return Settings.getBoolFromDatabase(getAttribute("rechnungsabschluss"), false);
	}

	@Override
	public void setRechnungsabschluss(Boolean rechnungsabschluss) throws RemoteException {
		setAttribute("rechnungsabschluss", rechnungsabschluss ? 1 : 0);
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
		return "gueltig_von";
	}

	@Override
	protected String getTableName() {
		return "kosten_strom";
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

	protected boolean checktTimeframe(Date datum) throws RemoteException {
		String sql = "SELECT COUNT(*) FROM kosten_strom where ? BETWEEN gueltig_von AND gueltig_bis AND id != ?";
		EnergieDBService service = Settings.getDBService();
		List<Object> params = new ArrayList<Object>();
		params.add(datum);
		params.add(this.getID());
		ResultSetExtractor rs = new ResultSetExtractor() {
			public Object extract(ResultSet rs) throws RemoteException, SQLException {
				if (rs.next())
					return rs.getInt(1);
				return 0;
			}
		};
		return ((((int) service.execute(sql, params.toArray(), rs)) > 0));
	}

	/**
	 * This method is invoked before executing insert(). So lets check the entered
	 * data.
	 * 
	 * @see de.willuhn.datasource.db.AbstractDBObject#insertCheck()
	 */
	protected void insertCheck() throws ApplicationException {
		try {
			if (getGueltigVon() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter gueltig_von"));
			else {
				if (checktTimeframe(getGueltigVon()))
					throw new ApplicationException(Settings.i18n().tr("gueltig_von overlaps"));
			}

			if (getGueltigBis() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter gueltig_bis"));
			else {
				if (checktTimeframe(getGueltigBis()))
					throw new ApplicationException(Settings.i18n().tr("gueltig_bis overlaps"));
				if ((getGueltigBis().getTime() - getGueltigVon().getTime()) <= 0) {
					throw new ApplicationException(Settings.i18n().tr("gueltig_von und gueltig_bis falsch"));
				}
			}

			if (getGrundpreis().equals(BigDecimal.ZERO))
				throw new ApplicationException(Settings.i18n().tr("Please enter grundpreis"));

			if (getArbeitspreis().equals(BigDecimal.ZERO))
				throw new ApplicationException(Settings.i18n().tr("Please enter arbeitspreis"));

		} catch (RemoteException e) {
			Logger.error("insert check of project failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store kosten"));
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
