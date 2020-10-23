package de.herrmannR.easydatabase.GUI.components.table;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class ContentTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -3373131989330542492L;

	public static final Color HOVER_COLOR = new Color(190, 241, 249);
	public static final Color DEFAULT_COLOR = Color.WHITE;

	private List<Color> rowColours = new ArrayList<Color>();

	public void initColors() {
		for (int i = 0; i < this.getRowCount(); i++) {
			rowColours.add(i, DEFAULT_COLOR);
		}
	}

	public void setRowColour(int row, Color c) {
		rowColours.set(row, c);
		fireTableRowsUpdated(row, row);
	}

	public void resetOtherRows(int row, Color colorToReset) {
		for (int i = 0; i < this.getRowCount(); i++) {
			if (i != row && this.getRowColour(i).equals(colorToReset)) {
				this.setRowColour(i, DEFAULT_COLOR);
			}
		}
	}

	public Color getRowColour(int row) {
		return rowColours.get(row);
	}
}
