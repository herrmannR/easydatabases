package de.herrmannR.easydatabase.GUI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import de.herrmannR.easydatabase.GUI.components.ContentTable;
import de.herrmannR.easydatabase.GUI.dialogs.TableDialog;
import de.herrmannR.easydatabase.GUI.util.CloseHandling;
import de.herrmannR.easydatabase.GUI.util.DoubleClickListener;
import de.herrmannR.easydatabase.util.Database;

public class DatabaseView extends JFrame implements DoubleClickListener {

	private static final long serialVersionUID = 5319220277392624846L;

	private static final int TABLE_COLUMN = 0;
	private static final int ROW_COUNT_COLUMN = 1;
	private static final int DESCRIPTION_COLUMN = 2;
	private static final int REFERENCES_COLUMN = 3;

	public final Database database;
	public final HashMap<String, TableDialog> tableViews = new HashMap<String, TableDialog>();

	private final Dimension minSize = new Dimension(1000, 600);

	public DatabaseView() {
		database = (Database) JOptionPane.showInputDialog(this, "Select the database you want to access.",
				"Database-Selection", JOptionPane.OK_CANCEL_OPTION, null, Database.values(), 0);

		if (database == null) {
			System.exit(0);
		}

		this.setMinimumSize(minSize);
		this.init();
		this.addWindowListener(new CloseHandling());
	}

	private void init() {
		ContentTable table = new ContentTable(this, this);

		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(TABLE_COLUMN).setPreferredWidth(150);
		columnModel.getColumn(ROW_COUNT_COLUMN).setPreferredWidth(50);
		columnModel.getColumn(DESCRIPTION_COLUMN).setPreferredWidth(500);
		columnModel.getColumn(DESCRIPTION_COLUMN).setCellRenderer(new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 2638355105114045117L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				cell.setToolTipText(cell.getText());
				return cell;
			}
		});
		columnModel.getColumn(REFERENCES_COLUMN).setPreferredWidth(300);

		JScrollPane tablePanel = new JScrollPane(table);
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

	@Override
	public void mouseDoubleClicked(MouseEvent e) {
		ContentTable table = (ContentTable) e.getSource();
		int row = table.rowAtPoint(e.getPoint());
		this.showTable((String) table.getValueAt(row, 0));
	}

	public static void main(String[] args) {
		DatabaseView adminFrame = new DatabaseView();
		adminFrame.setVisible(true);
	}
}
