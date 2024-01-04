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
import de.ritterbach.jameica.energie.rmi.Kosten;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class KostenImpl extends AbstractDBObject implements Kosten {

	public KostenImpl() throws RemoteException {
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
	public Date getAbschlagBis() throws RemoteException {
		return (Date) getAttribute("abschlag_bis");
	}

	@Override
	public void setAbschlagBis(Date abschlagBis) throws RemoteException {
		setAttribute("abschlag_bis", abschlagBis);
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
	public BigDecimal getFaktor() throws RemoteException {
		return (BigDecimal) getAttribute("faktor");
	}

	@Override
	public void setFaktor(BigDecimal faktor) throws RemoteException {
		setAttribute("faktor", faktor);
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
	public Boolean isNeuePeriode() throws RemoteException {
		return Settings.getBoolFromDatabase(getAttribute("neue_periode"), false);
	}

	@Override
	public void setNeuePeriode(Boolean neuePeriode) throws RemoteException {
		setAttribute("neue_periode", neuePeriode ? 1 : 0);
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
		return "kosten";
	}

	protected void deleteCheck() throws ApplicationException {
	}

	protected boolean checktTimeframe(Date datum) throws RemoteException {
		String sql = "SELECT COUNT(*) FROM kosten WHERE zaehler_id = ? AND id != ? AND ? BETWEEN gueltig_von AND gueltig_bis";
		EnergieDBService service = Settings.getDBService();
		List<Object> params = new ArrayList<Object>();
		params.add(getZaehler().getID());
		params.add(this.getID());
		params.add(datum);
		ResultSetExtractor rs = new ResultSetExtractor() {
			public Object extract(ResultSet rs) throws RemoteException, SQLException {
				if (rs.next())
					return rs.getInt(1);
				return 0;
			}
		};
		return ((((int) service.execute(sql, params.toArray(), rs)) > 0));
	}

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

			// Wenn es keinen Faktor gibt - den gibt es nur bei Gas - dann ist dieser immer 1
			if (!getZaehler().getNutztFaktor())
				setFaktor(BigDecimal.ONE);
		} catch (RemoteException e) {
			Logger.error("insert check of project failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store kosten"));
		}
	}

	protected void updateCheck() throws ApplicationException {
		insertCheck();
	}

}
