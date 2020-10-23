package de.herrmannR.easydatabase.GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import de.herrmannR.easydatabase.GUI.components.ContentTableOld;
import de.herrmannR.easydatabase.GUI.components.ContentTableOld.ContentTableCellRenderer;
import de.herrmannR.easydatabase.GUI.dialogs.TableDialog;
import de.herrmannR.easydatabase.GUI.util.CloseHandling;
import de.herrmannR.easydatabase.GUI.util.ContentTableClickListener;

public class DatabaseView extends JFrame implements ContentTableClickListener {

	private static final long serialVersionUID = 5319220277392624846L;

	private static final int TABLE_COLUMN = 0;
	private static final int ROW_COUNT_COLUMN = 1;
	private static final int DESCRIPTION_COLUMN = 2;
	private static final int REFERENCES_COLUMN = 3;

	public final HashMap<String, TableDialog> tableViews = new HashMap<String, TableDialog>();

	private final Dimension minSize = new Dimension(1000, 600);

	private ContentTableOld content;

	public DatabaseView() {
		this.setMinimumSize(minSize);
		Dimension window = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((window.width - minSize.width) / 2, (window.height - minSize.height) / 2);
		this.init();
		this.addWindowListener(new CloseHandling());
	}

	private void init() {
		this.content = new ContentTableOld(this);
		this.content.addContentTableClickListener(this);

		TableColumnModel columnModel = this.content.getColumnModel();
		columnModel.getColumn(TABLE_COLUMN).setPreferredWidth(150);
		columnModel.getColumn(ROW_COUNT_COLUMN).setPreferredWidth(50);
		columnModel.getColumn(DESCRIPTION_COLUMN).setPreferredWidth(500);
		((ContentTableCellRenderer) columnModel.getColumn(DESCRIPTION_COLUMN).getCellRenderer()).setShowTooltips(true);
		columnModel.getColumn(REFERENCES_COLUMN).setPreferredWidth(300);

		JScrollPane tablePanel = new JScrollPane(this.content);
		this.getContentPane().add(tablePanel);
	}

	private void showTable(String tableName) {
		if (!tableViews.containsKey(tableName) || tableViews.get(tableName) == null) {
			tableViews.put(tableName, new TableDialog(this, tableName));
		} else {
			tableViews.get(tableName).toFront();
		}
	}

	public void onCloseDialog(String table) {
		tableViews.put(table, null);
	}

	public void updateTable() {
		this.content.refresh();
	}

	@Override
	public void mouseDoubleClicked(MouseEvent e) {
		int row = this.content.rowAtPoint(e.getPoint());
		this.showTable((String) this.content.getValueAt(row, 0));
	}

	@Override
	public void showPopUpMenu(MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();
		int row = this.content.rowAtPoint(e.getPoint());
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DatabaseView.this.showTable((String) DatabaseView.this.content.getValueAt(row, 0));
			}
		});
		popupMenu.add(open);
		popupMenu.show(this.content, e.getX(), e.getY());
	}
}
