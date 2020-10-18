package de.herrmannR.easydatabase.GUI.components;

import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.structure.DataPackage;

public class ContentTable extends JTable {

	private static final long serialVersionUID = -4312393451897838251L;

	private DefaultTableModel model = new DefaultTableModel();

	/**
	 * Use it displaying all tables with descriptions.
	 */
	public ContentTable(MouseListener clickHandling) {
		Object[] tables = {};
		Object[] tableRowCounts = {};
		Object[] tableDescriptions = {};
		Object[] tableDependencies = {};
		try {
			tables = DatabaseManager.getInstance().getTables().toArray();
			tableRowCounts = new Object[tables.length];
			tableDescriptions = new Object[tables.length];
			tableDependencies = new Object[tables.length];
			String table = "";
			for (int i = 0; i < tableRowCounts.length; i++) {
				table = (String) tables[i];
				tableRowCounts[i] = DatabaseManager.getInstance().getRowCount(table);
				tableDescriptions[i] = DatabaseManager.getInstance().getTableDescription(table);
				tableDependencies[i] = DatabaseManager.getInstance().getTableDependencies(table);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.model.addColumn("table", tables);
		this.model.addColumn("rows", tableRowCounts);
		this.model.addColumn("description", tableDescriptions);
		this.model.addColumn("references", tableDependencies);
		this.setModel(model);
		this.sizing(clickHandling);
	}

	/**
	 * Use it displaying a tables content.
	 * 
	 * @param table
	 */
	public ContentTable(String table, MouseListener clickHandling) {
		try {
			DataPackage selection = DatabaseManager.getInstance().selectFrom(table);
			this.model.setDataVector(selection.getDataArray(), selection.getColumnNames());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.sizing(clickHandling);
		this.setModel(model);
	}

	private void sizing(MouseListener clickHandling) {
		this.setRowHeight(30);
		this.setDefaultEditor(Object.class, null);
		this.setAutoCreateRowSorter(true);
		this.addMouseListener(clickHandling);
	}
}
