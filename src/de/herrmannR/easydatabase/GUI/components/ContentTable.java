package de.herrmannR.easydatabase.GUI.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public abstract class ContentTable extends JTable implements MouseMotionListener {

	private static final long serialVersionUID = -1542077939750034032L;
	private static final Color HOVER_COLOR = new Color(190, 241, 249);
	private static final Color DEFAULT_COLOR = Color.WHITE;

	private ContentTableModel model;

	public ContentTable() {
		this.addMouseMotionListener(this);
		this.setAutoCreateRowSorter(true);
		this.init();
	}

	/**
	 * Reloads the table data. Therefore the TableModel will be overwritten. Because
	 * of that the table also has to be rescaled, which this method also do.
	 * 
	 * After calling this method one should repaint the table, by calling
	 * {@code repaint()}.
	 */
	private void init() {
		this.model = this.loadContent();
		this.model.initColors();
		this.setModel(model);
		this.setRowHeight(30);
		this.setDefaultEditor(Object.class, null);
		this.setCellRenderer();
	}

	protected abstract ContentTableModel loadContent();

	protected abstract void setCellRenderer();

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
				rowColours.add(i, ContentTable.DEFAULT_COLOR);
			}
		}

		public void setRowColour(int row, Color c) {
			rowColours.set(row, c);
			fireTableRowsUpdated(row, row);
		}

		public void resetOtherRows(int row) {
			for (int i = 0; i < this.getRowCount(); i++) {
				if (i != row) {
					setRowColour(i, ContentTable.DEFAULT_COLOR);
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
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBackground(ContentTable.this.model.getRowColour(row));
			if (showTooltips) {
				((JLabel) c).setToolTipText(((JLabel) c).getText());
			}
			return c;
		}
	}
}
