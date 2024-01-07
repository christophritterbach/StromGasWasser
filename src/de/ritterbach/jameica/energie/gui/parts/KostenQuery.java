package de.ritterbach.jameica.energie.gui.parts;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.datasource.rmi.ResultSetExtractor;

public class KostenQuery {

	public static Date[] getMinDateMaxdateNurOffeneRechnungen(Zaehler zaehler) throws RemoteException {
		String sql = "SELECT MIN(gueltig_von), MAX(gueltig_bis) FROM kosten WHERE zaehler_id=? AND abgerechnet=0";
		ResultSetExtractor rs = new ResultSetExtractor() {
			public Object extract(ResultSet rs) throws RemoteException, SQLException {
                Date vonBis[] = new Date[2];
				if (rs.next()) {
					vonBis[0] = rs.getDate(1);
					vonBis[1] = rs.getDate(2);
                    return vonBis;
				}
				return null;
			}
		};
		Object[] params = {zaehler.getID()};
		Date vonBis[] = (Date[]) Settings.getDBService().execute(sql, params, rs);
		return vonBis;
	}
}
