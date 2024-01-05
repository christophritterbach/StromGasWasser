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
import de.ritterbach.jameica.energie.gui.menu.AbschlagMenu;
import de.ritterbach.jameica.energie.rmi.Abschlag;
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

public class AbschlagListPart extends TablePart implements Part {
	private I18N i18n = null;
	private Listener listener;
	private DBService service = null;
	private SelectInput zaehlerAuswahl = null;
	private Input from = null;
	private Input to = null;

	public AbschlagListPart(Action action) throws RemoteException {
		this(init(), action);
	}

	public AbschlagListPart(GenericIterator<Abschlag> list, Action action) throws RemoteException {
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
		addColumn(i18n.tr("Abschlag_Datum"), "abschlag_datum", new DateFormatter());
		addColumn(i18n.tr("Abschlag_Betrag"), "abschlag_betrag", new CurrencyFormatter(Settings.CURRENCY, null), true);
		addColumn(i18n.tr("Notiz"), "notiz", null, true, Column.ALIGN_LEFT);
		setContextMenu(new AbschlagMenu());
		addChangeListener(new TableChangeListener() {
			public void itemChanged(Object object, String attribute, String newValue) throws ApplicationException {
				try {
					Abschlag z = (Abschlag) object;
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
		setRememberOrder(true);
		setRememberColWidths(true);
	}

	private static GenericIterator<Abschlag> init() throws RemoteException {
		DBIterator<Abschlag> abschlag = Settings.getDBService().createList(Abschlag.class);
		abschlag.setOrder("ORDER BY zaehler_id, abschlag_datum");
		return abschlag;
	}

	public synchronized void paint(Composite parent) throws RemoteException {
		final TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		TabGroup tab = new TabGroup(folder, i18n.tr("Anzeige_einschraenken"));
		ColumnLayout cols = new ColumnLayout(tab.getComposite(), 2);
		Container left = new SimpleContainer(cols.getComposite());
		left.addInput(this.getZaehlerAuswahl());
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

						DBIterator<Abschlag> abschlag = service.createList(Abschlag.class);
						abschlag.addFilter("zaehler_id=?", zaehler.getID());
						// Liste neu laden
						GenericIterator<Abschlag> items = abschlag;
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
