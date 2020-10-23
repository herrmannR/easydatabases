package de.herrmannR.easydatabase.GUI.components.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JTable;

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
		this.model.setRowColour(currentRow, ContentTableModel.HOVER_COLOR);
		this.model.resetOtherRows(currentRow, ContentTableModel.HOVER_COLOR);
	}
}
