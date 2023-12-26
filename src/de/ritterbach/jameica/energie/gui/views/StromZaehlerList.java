package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.StromWasserGasPlugin;
import de.ritterbach.jameica.energie.gui.menu.StromZaehlerMenu;
import de.ritterbach.jameica.energie.rmi.ZaehlerStrom;
import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.Column;
import de.willuhn.jameica.gui.parts.TableChangeListener;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class StromZaehlerList extends AbstractView {

	private final static I18N i18n = Application.getPluginLoader().getPlugin(StromWasserGasPlugin.class).getResources()
			.getI18N();
	private TablePart tableZaehler = null;

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("ZaehlerStrom list"));

		DBService service = Settings.getDBService();
		this.tableZaehler = new TablePart(service.createList(ZaehlerStrom.class), null);
		this.tableZaehler.addColumn(i18n.tr("Ablese_Datum"), "ablese_datum", new DateFormatter());
		this.tableZaehler.addColumn(i18n.tr("Ablese_Wert_W"), "ablese_wert", null, true);
		this.tableZaehler.addColumn(i18n.tr("Verbrauch_W"), "verbrauch", null, true);
		this.tableZaehler.addColumn(i18n.tr("Schaetzung"), "schaetzung");
		this.tableZaehler.addColumn(i18n.tr("Notiz"), "notiz", null, true, Column.ALIGN_LEFT);
		this.tableZaehler.setContextMenu(new StromZaehlerMenu());
		this.tableZaehler.setRememberOrder(true);
		this.tableZaehler.setRememberColWidths(true);
		this.tableZaehler.addChangeListener(new TableChangeListener() {
			public void itemChanged(Object object, String attribute, String newValue) throws ApplicationException {
				try {
					ZaehlerStrom z = (ZaehlerStrom) object;
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
		this.tableZaehler.paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}
}
