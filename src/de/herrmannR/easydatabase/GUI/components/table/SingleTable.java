package de.herrmannR.easydatabase.GUI.components.table;

import java.sql.SQLException;

import javax.swing.table.TableCellRenderer;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.structure.DataPackage;

public class SingleTable extends ContentTable {

	private static final long serialVersionUID = -4477745770731280306L;

	private final String table;

	public SingleTable(String table) {
		super(false);
		this.table = table;
		this.init();
	}

	@Override
	protected ContentTableModel loadContent() {
		ContentTableModel model = new ContentTableModel();
		try {
			DataPackage selection = DatabaseManager.getInstance().selectFrom(this.table);
			model.setDataVector(selection.getDataArray(), selection.getColumnNames());
			return model;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ContentTableModel();
		}
	}

	@Override
	protected TableCellRenderer createCellRenderer(int column) {
		return new ContentTableCellRenderer(false);
	}

	@Override
	protected void sizing() {
		this.setRowHeight(30);
	}

}
