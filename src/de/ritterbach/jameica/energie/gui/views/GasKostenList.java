package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.StromWasserGasPlugin;
import de.ritterbach.jameica.energie.gui.menu.GasKostenMenu;
import de.ritterbach.jameica.energie.rmi.KostenGas;
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

public class GasKostenList extends AbstractView {

	private final static I18N i18n = Application.getPluginLoader().getPlugin(StromWasserGasPlugin.class).getResources().getI18N();
	private TablePart tableKosten = null;

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("KostenGas list"));

		DBService service = Settings.getDBService();
		this.tableKosten = new TablePart(service.createList(KostenGas.class), null);
		this.tableKosten.addColumn(i18n.tr("Gueltig_von"), "gueltig_von", new DateFormatter());
		this.tableKosten.addColumn(i18n.tr("Gueltig_bis"), "gueltig_bis", new DateFormatter());
		this.tableKosten.addColumn(i18n.tr("Grundpreis_G"), "grundpreis",
				new CurrencyFormatter(Settings.CURRENCY, null));
		this.tableKosten.addColumn(i18n.tr("Arbeitspreis_G"), "arbeitspreis",
				new CurrencyFormatter(Settings.CURRENCY, Settings.ARBEITSPREISFORMAT));
		this.tableKosten.addColumn(i18n.tr("Faktor_G"), "faktor");
		this.tableKosten.addColumn(i18n.tr("Abgerechnet"), "abgerechnet");
		this.tableKosten.addColumn(i18n.tr("Neue_Periode"), "neue_periode");
		this.tableKosten.addColumn(i18n.tr("Notiz"), "notiz", null, true, Column.ALIGN_LEFT);
		this.tableKosten.setContextMenu(new GasKostenMenu());
		this.tableKosten.setRememberOrder(true);
		this.tableKosten.setRememberColWidths(true);
		this.tableKosten.paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}
}
