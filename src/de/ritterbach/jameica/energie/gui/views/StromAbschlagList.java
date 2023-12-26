package de.ritterbach.jameica.energie.gui.views;

import de.ritterbach.jameica.energie.Settings;
import de.ritterbach.jameica.energie.StromWasserGasPlugin;
import de.ritterbach.jameica.energie.gui.menu.StromAbschlagMenu;
import de.ritterbach.jameica.energie.rmi.AbschlagStrom;
import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.Column;
import de.willuhn.jameica.gui.parts.TableChangeListener;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class StromAbschlagList extends AbstractView {

	private final static I18N i18n = Application.getPluginLoader().getPlugin(StromWasserGasPlugin.class).getResources()
			.getI18N();
	private TablePart tableAbschlag = null;

	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("AbschlagStrom list"));

		DBService service = Settings.getDBService();
		this.tableAbschlag = new TablePart(service.createList(AbschlagStrom.class), null);
		this.tableAbschlag.addColumn(i18n.tr("Abschlag_Datum"), "abschlag_datum", new DateFormatter());
		this.tableAbschlag.addColumn(i18n.tr("Abschlag_Betrag"), "abschlag_betrag",
				new CurrencyFormatter(Settings.CURRENCY, null), true);
		this.tableAbschlag.addColumn(i18n.tr("Notiz"), "notiz", null, true, Column.ALIGN_LEFT);
		this.tableAbschlag.setContextMenu(new StromAbschlagMenu());
		this.tableAbschlag.setRememberOrder(true);
		this.tableAbschlag.setRememberColWidths(true);
		this.tableAbschlag.addChangeListener(new TableChangeListener() {
			public void itemChanged(Object object, String attribute, String newValue) throws ApplicationException {
				try {
					AbschlagStrom z = (AbschlagStrom) object;
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
		this.tableAbschlag.paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
