package de.ritterbach.jameica.energie.gui.parts;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.StromWasserGasPlugin;
import de.ritterbach.jameica.energie.gui.menu.KostenMenu;
import de.ritterbach.jameica.energie.rmi.Kosten;
import de.ritterbach.jameica.energie.rmi.Zaehler;
import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.Column;
import de.willuhn.jameica.gui.parts.TableChangeListener;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.gui.util.TabGroup;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class KostenListPart extends TablePart implements Part {
	private I18N i18n = null;
	private Listener listener;
	private DBService service = null;
	private SelectInput zaehlerAuswahl = null;
	private CheckboxInput nurOffeneRechnungen; 
	private Input from = null;
	private Input to = null;

	public KostenListPart(Action action) throws RemoteException {
		this(init(), action);
	}

	public KostenListPart(GenericIterator<Kosten> list, Action action) throws RemoteException {
		super(list, action);
		this.service = Settings.getDBService();
		this.listener = new Listener() {
			public void handleEvent(Event event) {
				// Wenn das event "null" ist, kann es nicht von SWT ausgeloest worden sein
				// sondern manuell von uns. In dem Fall machen wir ein forciertes Update
				// - ohne zu beruecksichtigen, ob in den Eingabe-Feldern wirklich was
				// geaendert wurde
				handleReload(event == null);
			}
		};

		this.i18n = Application.getPluginLoader().getPlugin(StromWasserGasPlugin.class).getResources().getI18N();
		addColumn(i18n.tr("Gueltig_von"), "gueltig_von", new DateFormatter());
		addColumn(i18n.tr("Gueltig_bis"), "gueltig_bis", new DateFormatter());
		addColumn(i18n.tr("Grundpreis"), "grundpreis", new CurrencyFormatter(Settings.CURRENCY, null));
		addColumn(i18n.tr("Arbeitspreis"), "arbeitspreis",
				new CurrencyFormatter(Settings.CURRENCY, Settings.ARBEITSPREISFORMAT));
		addColumn(i18n.tr("Faktor"), "faktor");
		addColumn(i18n.tr("Abgerechnet"), "abgerechnet");
		addColumn(i18n.tr("Neue_Periode"), "neue_periode");
		addColumn(i18n.tr("Notiz"), "notiz", null, true, Column.ALIGN_LEFT);
		setContextMenu(new KostenMenu());
		setRememberOrder(true);
		setRememberColWidths(true);
		addChangeListener(new TableChangeListener() {
			public void itemChanged(Object object, String attribute, String newValue) throws ApplicationException {
				try {
					Kosten z = (Kosten) object;
					BeanUtil.set(z, attribute, newValue);
					z.store();
				} catch (ApplicationException ae) {
					throw ae;
				} catch (Exception e) {
					Logger.error("unable to apply changes", e);
					throw new ApplicationException(i18n.tr("Fehlgeschlagen: {0}", e.getMessage()));
				}
			}
		});
	}

	private static GenericIterator<Kosten> init() throws RemoteException {
		DBIterator<Kosten> kosten = Settings.getDBService().createList(Kosten.class);
		kosten.setOrder("ORDER BY zaehler_id, gueltig_von");
		return kosten;
	}

	public synchronized void paint(Composite parent) throws RemoteException {
		final TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		TabGroup tab = new TabGroup(folder, i18n.tr("Anzeige_einschraenken"));
		ColumnLayout cols = new ColumnLayout(tab.getComposite(), 2);
		Container left = new SimpleContainer(cols.getComposite());
		left.addInput(this.getZaehlerAuswahl());
		left.addInput(this.getIstNurOffeneRechnungen());
		Container right = new SimpleContainer(cols.getComposite());
		right.addInput(getFrom());
		right.addInput(getTo());
		ButtonArea buttons = new ButtonArea();
		buttons.addButton(i18n.tr("Aktualisieren"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				handleReload(true);
			}
		}, null, true, "view-refresh.png");
		buttons.paint(parent);

		this.handleReload(true);
		super.paint(parent);
	}

	private Input getZaehlerAuswahl() throws RemoteException {
		if (this.zaehlerAuswahl != null)
			return this.zaehlerAuswahl;
		DBIterator<Zaehler> zaehler = service.createList(Zaehler.class);
		zaehler.addFilter("ist_aktiv = 1");
		zaehler.setOrder("ORDER BY id");
		List<Zaehler> liste = new ArrayList<>();
		while(zaehler.hasNext()) {
			Zaehler z = (Zaehler) zaehler.next();
			liste.add(z);
		}
		Zaehler preselected = liste.get(0);
		if (Settings.getZaehler() != null) {
			preselected = Settings.getZaehler();
		}
		this.zaehlerAuswahl = new SelectInput(liste, preselected);
		this.zaehlerAuswahl.addListener(this.listener);
		return this.zaehlerAuswahl;
	}

	public CheckboxInput getIstNurOffeneRechnungen() throws RemoteException {
		if (nurOffeneRechnungen != null)
			return nurOffeneRechnungen;

		nurOffeneRechnungen = new CheckboxInput(true);
		nurOffeneRechnungen.setName(Settings.i18n().tr("nur_offene_rechnungen"));
		nurOffeneRechnungen.setComment(Settings.i18n().tr("keine_bereits_abgerechneten"));
		nurOffeneRechnungen.addListener(this.listener);
		return this.nurOffeneRechnungen;
	}

	private Input getFrom() {
		if (this.from != null)
			return this.from;
		Calendar datum = Calendar.getInstance();
		datum.set(Calendar.HOUR_OF_DAY, 0);
		datum.set(Calendar.MINUTE, 0);
		datum.set(Calendar.SECOND, 0);
		datum.set(Calendar.MILLISECOND, 0);
		datum.add(Calendar.MONTH, -6);
		datum.set(Calendar.DAY_OF_MONTH, datum.getActualMinimum(Calendar.DAY_OF_MONTH));
		this.from = new DateInput(datum.getTime());
		this.from.setName(i18n.tr("von"));
		this.from.setComment(null);
		this.from.addListener(this.listener);
		return this.from;
	}

	private Input getTo() {
		if (this.to != null)
			return this.to;
		Calendar datum = Calendar.getInstance();
		datum.set(Calendar.HOUR_OF_DAY, 0);
		datum.set(Calendar.MINUTE, 0);
		datum.set(Calendar.SECOND, 0);
		datum.set(Calendar.MILLISECOND, 0);
		datum.add(Calendar.MONTH, 6);
		datum.set(Calendar.DAY_OF_MONTH, datum.getActualMinimum(Calendar.DAY_OF_MONTH));
		this.to = new DateInput(datum.getTime());
		this.to.setName(i18n.tr("bis"));
		this.to.setComment(null);
		this.to.addListener(this.listener);
		return this.to;
	}

	private synchronized void handleReload(boolean force) {
		try {
			final Zaehler zaehler = (Zaehler) getZaehlerAuswahl().getValue();
			Settings.setZaehler(zaehler);
			GUI.startSync(new Runnable() { // Sanduhr anzeigen
				public void run() {
					try {
						removeAll();

						DBIterator<Kosten> kosten = service.createList(Kosten.class);
						kosten.addFilter("zaehler_id=?", zaehler.getID());
						if ((Boolean)getIstNurOffeneRechnungen().getValue())
							kosten.addFilter("abgerechnet=0");
						// Liste neu laden
						GenericIterator<Kosten> items = kosten;
						if (items == null)
							return;

						items.begin();
						while (items.hasNext())
							addItem(items.next());
						sort();
					} catch (Exception e) {
						Logger.error("error while reloading table", e);
						Application.getMessagingFactory().sendMessage(new StatusBarMessage(
								i18n.tr("Fehler beim Aktualisieren der Tabelle"), StatusBarMessage.TYPE_ERROR));
					}
				}
			});
		} catch (Exception e) {
			Logger.error("error while reloading data", e);
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					i18n.tr("Fehler beim Aktualisieren der Tabelle"), StatusBarMessage.TYPE_ERROR));
		}
	}
}
