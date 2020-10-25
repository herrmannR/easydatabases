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
import de.herrmannR.easydatabase.Main;
import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.GUI.components.table.ContentTableClickListener;
import de.herrmannR.easydatabase.GUI.components.table.SingleTable;
import de.herrmannR.easydatabase.structure.Filter;

public class TableDialog extends JDialog implements ContentTableClickListener, ActionListener {

	private static final long serialVersionUID = 306039785577770539L;

	private static final String CLOSE = "close action";
	private static final String ADD_NEW = "add_new_row action";

	private final Dimension minSize = new Dimension(1000, 300);
	private final String table;

	private SingleTable content;

	public TableDialog(DatabaseView parent, String table) {
		super(parent);
		this.table = table;

		this.setMinimumSize(minSize);
		this.getContentPane().setLayout(new BorderLayout());

		this.content = new SingleTable(this.table);
		this.content.addContentTableClickListener(this);
		JScrollPane tablePanel = new JScrollPane(content);

		JLabel header = new JLabel(this.table);
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
				Main.adminFrame.onCloseDialog(TableDialog.this.table);
				Main.adminFrame.updateTable();
				super.windowClosed(e);
			}
		});

		this.setVisible(true);
	}

	private Filter getFilterForRow(int row) throws SQLException {
		Set<String> primCols = DatabaseManager.getInstance().getPrimaryCols(this.table);
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
			new EditRowDialog((DatabaseView) this.getParent(), this.table, primaryKeys);
			this.reloadTable();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(getParent(), "Can't open EditRowDialog!\n" + e1.getMessage(), "SQL-Exception",
					JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	private void reloadTable() {
		this.content.refresh();
	}

	@Override
	public void mouseDoubleClicked(MouseEvent e) {
		int row = content.rowAtPoint(e.getPoint());
		this.openRowEditDialog(row);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CLOSE)) {
			((DatabaseView) this.getParent()).onCloseDialog(this.table);
			this.dispose();
			Main.adminFrame.updateTable();
		} else if (e.getActionCommand().equals(ADD_NEW)) {
			try {
				new AddRowDialog((DatabaseView) this.getParent(), this.table);
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
					String result = DatabaseManager.getInstance().deleteRow(TableDialog.this.table,
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
