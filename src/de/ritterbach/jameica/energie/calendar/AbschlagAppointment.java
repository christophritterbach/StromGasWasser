package de.ritterbach.jameica.energie.calendar;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import org.eclipse.swt.graphics.RGB;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Abschlag;
import de.willuhn.jameica.gui.calendar.Appointment;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AbschlagAppointment implements Appointment {

	public AbschlagAppointment(Abschlag abschlag) {
		super();
		this.abschlag = abschlag;
	}

	protected Abschlag abschlag = null;
	@Override
	public Date getDate() {
		Date datum = null;
		try {
			datum = abschlag.getAbschlagDatum();
		} catch (RemoteException e) {
			e.printStackTrace();
		};
		return datum;
	}

	@Override
	public String getName() {
		String name = "";
		try {
			BigDecimal betrag = abschlag.getAbschlagBetrag();
			name = Settings.i18n().tr("{0} {1} fuer Abschlag {2}", Settings.DECIMALFORMAT.format(betrag.negate()), Settings.CURRENCY, abschlag.getZaehler().getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public String getDescription() {
		String desc = "Abschlag";
		try {
			desc = Settings.i18n().tr("Abschlag {0}", abschlag.getZaehler().getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return desc;
	}

	@Override
	public void execute() throws ApplicationException {

	}

	@Override
	public RGB getColor() {
		return new RGB(226,102,38);
	}

	@Override
	public boolean hasAlarm() {
		return false;
	}

	@Override
	public String getUid() {
		try {
			return abschlag.getClass().getName() + "." + abschlag.getID();
		} catch (RemoteException re) {
			Logger.error("unable to create uid", re);
			return this.getName() + "/" + this.getDate();
		}
	}

}
