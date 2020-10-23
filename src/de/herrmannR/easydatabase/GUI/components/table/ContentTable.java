package de.herrmannR.easydatabase.GUI.components.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public abstract class ContentTable extends JTable implements MouseMotionListener {

	private static final long serialVersionUID = -1542077939750034032L;

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
		for (int i = 0; i < this.getColumnCount(); i++) {
			this.getColumnModel().getColumn(i).setCellRenderer(this.createCellRenderer(i));
		}
	}

	/**
	 * Should return the ContenTableModel for this table with all data freshly
	 * pulled from the database. It is used to reload the table´s content.
	 */
	protected abstract ContentTableModel loadContent();

	/**
	 * This method is automatically called after the {@ TableModel} is loaded in
	 * {@code init()}. It should return the CellRenderer for the specific column.
	 */
	protected abstract TableCellRenderer createCellRenderer(int column);

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int currentRow = rowAtPoint(e.getPoint());
		this.model.setRowColour(currentRow, ContentTableModel.HOVER_COLOR);
		this.model.resetOtherRows(currentRow, ContentTableModel.HOVER_COLOR);
	}
}
