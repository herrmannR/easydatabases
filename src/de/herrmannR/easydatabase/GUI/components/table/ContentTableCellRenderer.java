package de.herrmannR.easydatabase.GUI.components.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ContentTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -2197947502817677866L;

	private boolean showTooltips;

	public ContentTableCellRenderer(boolean showTooltips) {
		this.showTooltips = showTooltips;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		ContentTableModel model = ((ContentTableModel) table.getModel());
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setBackground(model.getRowColour(row));
		if (showTooltips) {
			((JLabel) c).setToolTipText(((JLabel) c).getText());
		}
		return c;
	}
}
