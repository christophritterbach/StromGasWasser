package de.ritterbach.jameica.energie.calendar;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.rmi.Abschlag;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.calendar.Appointment;
import de.willuhn.jameica.gui.calendar.AppointmentProvider;

public class AbschlagAppointmentProvider implements AppointmentProvider {

	@Override
	public String getName() {
		return "Energie";
	}

	@Override
	public List<Appointment> getAppointments(Date from, Date to) {
		List<Appointment> liste = new ArrayList<>();
		try {
			DBIterator<Abschlag> abschlagListe = Settings.getDBService().createList(Abschlag.class);
			abschlagListe.addFilter("abschlag_datum >= ?", from);
			abschlagListe.addFilter("abschlag_datum <= ?", to);
			while(abschlagListe.hasNext()) {
				Abschlag abschlag = abschlagListe.next();
				liste.add(new AbschlagAppointment(abschlag));
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return liste;
	}

}
