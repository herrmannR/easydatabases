package de.herrmannR.easydatabase.GUI.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.GUI.util.ContentTableClickListener;
import de.herrmannR.easydatabase.structure.DataPackage;

public class ContentTable extends JTable implements MouseMotionListener {

	private static final long serialVersionUID = -4312393451897838251L;
	private static final Color HOVER_COLOR = new Color(190, 241, 249);

	private ContentTableModel model = new ContentTableModel();

	/**
	 * Use it displaying all tables with descriptions.
	 */
	public ContentTable(DatabaseView parent) {
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
		this.init();
	}

	/**
	 * Use it to display a tables content.
	 * 
	 * @param table
	 */
	public ContentTable(String table) {
		try {
			DataPackage selection = DatabaseManager.getInstance().selectFrom(table);
			this.model.setDataVector(selection.getDataArray(), selection.getColumnNames());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.init();
	}

	public void addContentTableClickListener(ContentTableClickListener listener) {
		this.addMouseListener(listener);
	}

	private void init() {
		this.model.initColors();
		this.setModel(model);
		this.setRowHeight(30);
		this.setDefaultEditor(Object.class, null);
		this.setAutoCreateRowSorter(true);
		for (int i = 0; i < this.getColumnCount(); i++) {
			this.getColumnModel().getColumn(i).setCellRenderer(new ContentTableCellRenderer(false));
		}
		this.addMouseMotionListener(this);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int currentRow = rowAtPoint(e.getPoint());
		this.model.setRowColour(currentRow, HOVER_COLOR);
		this.model.resetOtherRows(currentRow);
	}

	private static class ContentTableModel extends DefaultTableModel {

		private static final long serialVersionUID = -3373131989330542492L;

		private List<Color> rowColours = new ArrayList<Color>();

		public void initColors() {
			for (int i = 0; i < this.getRowCount(); i++) {
				rowColours.add(i, Color.WHITE);
			}
		}

		public void setRowColour(int row, Color c) {
			rowColours.set(row, c);
			fireTableRowsUpdated(row, row);
		}

		public void resetOtherRows(int row) {
			for (int i = 0; i < this.getRowCount(); i++) {
				if (i != row) {
					setRowColour(i, Color.WHITE);
				}
			}
		}

		public Color getRowColour(int row) {
			return rowColours.get(row);
		}
	}

	public static class ContentTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -2197947502817677866L;

		private boolean showTooltips;

		public ContentTableCellRenderer(boolean showTooltips) {
			this.showTooltips = showTooltips;
		}

		public void setShowTooltips(boolean showTooltips) {
			this.showTooltips = showTooltips;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			ContentTableModel model = (ContentTableModel) table.getModel();
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBackground(model.getRowColour(row));
			if (showTooltips) {
				((JLabel) c).setToolTipText(((JLabel) c).getText());
			}
			return c;
		}
	}

	public void refresh() {
		this.model = new ContentTableModel();
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
		this.init();
		this.model.fireTableRowsUpdated(0, this.getRowCount() - 1);
	}

	public void refresh(String table) {
		this.model = new ContentTableModel();
		try {
			DataPackage selection = DatabaseManager.getInstance().selectFrom(table);
			this.model.setDataVector(selection.getDataArray(), selection.getColumnNames());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.init();
		this.model.fireTableRowsUpdated(0, this.getRowCount() - 1);
	}
}
