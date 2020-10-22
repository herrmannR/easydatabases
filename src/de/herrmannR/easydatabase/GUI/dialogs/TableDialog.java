package de.herrmannR.easydatabase.GUI.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.GUI.components.ContentTable;
import de.herrmannR.easydatabase.GUI.util.ContentTableClickListener;
import de.herrmannR.easydatabase.structure.Filter;
import de.herrmannR.easydatabase.util.Database;

public class TableDialog extends JDialog implements ContentTableClickListener, ActionListener {

	private static final long serialVersionUID = 306039785577770539L;

	private static final String CLOSE = "close action";
	private static final String ADD_NEW = "add_new_row action";

	private final Dimension minSize = new Dimension(1000, 300);
	private final String table;
	private final Database database;

	private ContentTable content;

	public TableDialog(DatabaseView parent, String table) {
		super(parent);
		this.table = table;
		this.database = parent.database;

		this.setMinimumSize(minSize);
		this.getContentPane().setLayout(new BorderLayout());

		this.content = new ContentTable(table, parent.database);
		this.content.addContentTableClickListener(this);
		JScrollPane tablePanel = new JScrollPane(content);

		JLabel header = new JLabel(table);
		header.setFont(new Font("Arial", Font.PLAIN, 18));

		JButton close = new JButton("Close");
		JButton add = new JButton("Add row");
		JPanel buttons = new JPanel();

		add.setActionCommand(ADD_NEW);
		add.addActionListener(this);
		close.setActionCommand(CLOSE);
		close.addActionListener(this);
		buttons.add(add);
		buttons.add(close);

		this.getContentPane().add(tablePanel, BorderLayout.CENTER);
		this.getContentPane().add(header, BorderLayout.NORTH);
		this.getContentPane().add(buttons, BorderLayout.SOUTH);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				parent.onCloseDialog(table);
				parent.updateTable();
				super.windowClosed(e);
			}
		});

		this.setVisible(true);
	}

	private Filter getFilterForRow(int row) throws SQLException {
		Set<String> primCols = DatabaseManager.getInstance(this.database).getPrimaryCols(table);
		Filter filter = new Filter();
		for (String primCol : primCols) {
			int column = content.getColumn(primCol).getModelIndex();
			filter.addAttribute(primCol, content.getValueAt(row, column));
		}
		return filter;
	}

	private void openRowEditDialog(int row) {
		try {
			Filter primaryKeys = this.getFilterForRow(row);
			new EditRowDialog((DatabaseView) this.getParent(), table, primaryKeys);
			this.reloadTable();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(getParent(), "Can't open EditRowDialog!\n" + e1.getMessage(), "SQL-Exception",
					JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	private void reloadTable() {
		this.content.refresh(this.database, this.table);
	}

	@Override
	public void mouseDoubleClicked(MouseEvent e) {
		int row = content.rowAtPoint(e.getPoint());
		this.openRowEditDialog(row);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CLOSE)) {
			((DatabaseView) this.getParent()).onCloseDialog(table);
			this.dispose();
		} else if (e.getActionCommand().equals(ADD_NEW)) {
			try {
				new AddRowDialog((DatabaseView) this.getParent(), table);
				this.reloadTable();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(getParent(), "Can't open AddRowDialog!\n" + e1.getMessage(),
						"SQL-Exception", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void showPopUpMenu(MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();
		int row = this.content.rowAtPoint(e.getPoint());

		JMenuItem edit = new JMenuItem("Edit");
		edit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TableDialog.this.openRowEditDialog(row);
			}
		});
		popupMenu.add(edit);

		JMenuItem delete = new JMenuItem("Delete");
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String result = DatabaseManager.getInstance(TableDialog.this.database).deleteRow(table,
							TableDialog.this.getFilterForRow(row));
					JOptionPane.showMessageDialog(TableDialog.this, result);
				} catch (Exception ex) {
					if (JOptionPane.showOptionDialog(TableDialog.this, ex.toString() + "\nView details in console?",
							"Insert failed", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, null,
							null) == JOptionPane.YES_OPTION) {
						ex.printStackTrace();
					}
				}
				TableDialog.this.reloadTable();
			}
		});
		popupMenu.add(delete);

		popupMenu.show(this.content, e.getX(), e.getY());
	}
}
