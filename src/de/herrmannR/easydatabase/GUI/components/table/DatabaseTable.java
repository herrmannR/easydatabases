package de.herrmannR.easydatabase.GUI.components.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableCellRenderer;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.DatabaseView;

public class DatabaseTable extends ContentTable {

	private static final long serialVersionUID = 1087753113624975634L;

	private static final String NAME_COLUMN = "table";
	private static final String ROW_COUNT_COLUMN = "rows";
	private static final String DESCRIPTION_COLUMN = "description";
	private static final String REFERENCES_COLUMN = "references";

	private DatabaseView owner;

	public DatabaseTable(DatabaseView owner) {
		super(true);
		this.owner = owner;
	}

	@Override
	protected ContentTableModel loadContent() {
		ContentTableModel model = new ContentTableModel();
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

		model.addColumn(NAME_COLUMN, tables);
		model.addColumn(ROW_COUNT_COLUMN, tableRowCounts);
		model.addColumn(DESCRIPTION_COLUMN, tableDescriptions);
		model.addColumn(REFERENCES_COLUMN, tableDependencies);

		return model;
	}

	@Override
	protected TableCellRenderer createCellRenderer(int column) {
		if (column == this.columnModel.getColumnIndex(DESCRIPTION_COLUMN)) {
			return new ContentTableCellRenderer(true);
		}
		return new ContentTableCellRenderer(false);
	}

	@Override
	protected void sizing() {
		this.setRowHeight(30);
		this.columnModel.getColumn(this.columnModel.getColumnIndex(NAME_COLUMN)).setPreferredWidth(150);
		this.columnModel.getColumn(this.columnModel.getColumnIndex(ROW_COUNT_COLUMN)).setPreferredWidth(50);
		this.columnModel.getColumn(this.columnModel.getColumnIndex(DESCRIPTION_COLUMN)).setPreferredWidth(500);
		this.columnModel.getColumn(this.columnModel.getColumnIndex(REFERENCES_COLUMN)).setPreferredWidth(300);
	}

	@Override
	public void mouseDoubleClicked(MouseEvent e) {
		int row = this.rowAtPoint(e.getPoint());
		this.owner.showTable((String) this.getValueAt(row, 0));
	}

	@Override
	protected JPopupMenu getPopupMenu(MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();
		int row = DatabaseTable.this.rowAtPoint(e.getPoint());
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DatabaseTable.this.owner.showTable((String) DatabaseTable.this.getValueAt(row, 0));
			}
		});
		popupMenu.add(open);
		return popupMenu;
	}
}
