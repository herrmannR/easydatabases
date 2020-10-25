package de.herrmannR.easydatabase.GUI.components.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.TableCellRenderer;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.dialogs.TableDialog;
import de.herrmannR.easydatabase.structure.DataPackage;

public class SingleTable extends ContentTable {

	private static final long serialVersionUID = -4477745770731280306L;

	private final String table;
	private final TableDialog owner;

	public SingleTable(String table, TableDialog owner) {
		super(false);
		this.table = table;
		this.owner = owner;
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

	@Override
	public void mouseDoubleClicked(MouseEvent e) {
		int row = this.rowAtPoint(e.getPoint());
		this.owner.openRowEditDialog(row);
	}

	@Override
	protected JPopupMenu getPopupMenu(MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();
		int row = this.rowAtPoint(e.getPoint());

		JMenuItem edit = new JMenuItem("Edit");
		edit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SingleTable.this.owner.openRowEditDialog(row);
			}
		});
		popupMenu.add(edit);

		JMenuItem delete = new JMenuItem("Delete");
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String result = DatabaseManager.getInstance().deleteRow(SingleTable.this.table,
							SingleTable.this.owner.getFilterForRow(row));
					JOptionPane.showMessageDialog(SingleTable.this.owner, result);
				} catch (Exception ex) {
					if (JOptionPane.showOptionDialog(SingleTable.this.owner,
							ex.toString() + "\nView details in console?", "Insert failed", JOptionPane.YES_NO_OPTION,
							JOptionPane.ERROR_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
						ex.printStackTrace();
					}
				}
				SingleTable.this.refresh();
			}
		});
		popupMenu.add(delete);
		return popupMenu;
	}

}
