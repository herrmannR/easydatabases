package de.herrmannR.easydatabase.GUI.components.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public abstract class ContentTable extends JTable implements MouseMotionListener, ContentTableClickListener {

	private static final long serialVersionUID = -1542077939750034032L;

	/**
	 * If {@code autoInit} is set false the {@code init()} method must be called
	 * manually. Use this if you want to set some content relevant variables before
	 * loading tables content.
	 */
	public ContentTable(boolean autoInit) {
		this.addMouseMotionListener(this);
		this.addContentTableClickListener(this);
		this.setAutoCreateRowSorter(true);
		if (autoInit) {
			this.init();
		}
	}

	/**
	 * Reloads the table data. Therefore the TableModel will be overwritten. Because
	 * of that the table also has to be rescaled, which this method also do.
	 * 
	 * After calling this method one should repaint the table, by calling
	 * {@code repaint()}.
	 */
	protected void init() {
		this.setModel(this.loadContent());
		((ContentTableModel) this.getModel()).initColors();
		this.sizing();
		this.setDefaultEditor(Object.class, null);
		this.setCellRenderes();
	}

	public void refresh() {
		this.init();
		this.repaint();
	}

	public void addContentTableClickListener(ContentTableClickListener listener) {
		this.addMouseListener(listener);
	}

	private void setCellRenderes() {
		for (int i = 0; i < this.getColumnCount(); i++) {
			this.getColumnModel().getColumn(i).setCellRenderer(this.createCellRenderer(i));
		}
	}

	protected abstract void sizing();

	/**
	 * Should reset the ContenTableModel for this table with all data freshly pulled
	 * from the database. It is used to reload the table´s content.
	 */
	protected abstract ContentTableModel loadContent();

	/**
	 * This method is automatically called after the {@ TableModel} is loaded in
	 * {@code init()}. It should return the CellRenderer for the specific column.
	 */
	protected abstract TableCellRenderer createCellRenderer(int column);

	protected abstract JPopupMenu getPopupMenu(MouseEvent e);

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int currentRow = rowAtPoint(e.getPoint());
		if (currentRow != -1) {
			((ContentTableModel) this.getModel()).setRowColour(currentRow, ContentTableModel.HOVER_COLOR);
		}
		((ContentTableModel) this.getModel()).resetOtherRows(currentRow, ContentTableModel.HOVER_COLOR);
	}

	@Override
	public void showPopUpMenu(MouseEvent e) {
		this.getPopupMenu(e).show(this, e.getX(), e.getY());
	}
}
