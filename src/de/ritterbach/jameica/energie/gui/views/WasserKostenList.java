package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.StromWasserGasPlugin;
import de.ritterbach.jameica.energie.gui.menu.WasserKostenMenu;
import de.ritterbach.jameica.energie.rmi.KostenWasser;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.Column;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class WasserKostenList extends AbstractView {

	private final static I18N i18n = Application.getPluginLoader().getPlugin(StromWasserGasPlugin.class).getResources().getI18N();
	private TablePart tableKosten = null;

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("KostenWasser list"));

		DBService service = Settings.getDBService();
		this.tableKosten = new TablePart(service.createList(KostenWasser.class), null);
		this.tableKosten.addColumn(i18n.tr("Gueltig_von"), "gueltig_von", new DateFormatter());
		this.tableKosten.addColumn(i18n.tr("Gueltig_bis"), "gueltig_bis", new DateFormatter());
		this.tableKosten.addColumn(i18n.tr("Grundpreis_W"), "grundpreis",
				new CurrencyFormatter(Settings.CURRENCY, null));
		this.tableKosten.addColumn(i18n.tr("Arbeitspreis_W"), "arbeitspreis",
				new CurrencyFormatter(Settings.CURRENCY, Settings.ARBEITSPREISFORMAT));
		this.tableKosten.addColumn(i18n.tr("Abgerechnet"), "abgerechnet");
		this.tableKosten.addColumn(i18n.tr("Notiz"), "notiz", null, true, Column.ALIGN_LEFT);
		this.tableKosten.addColumn(i18n.tr("Rechnungsabschluss"), "rechnungsabschluss");
		this.tableKosten.setContextMenu(new WasserKostenMenu());
		this.tableKosten.setRememberOrder(true);
		this.tableKosten.setRememberColWidths(true);
		this.tableKosten.paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}
}
