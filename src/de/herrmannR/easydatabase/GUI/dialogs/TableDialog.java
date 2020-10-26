package de.herrmannR.easydatabase.GUI.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.Main;
import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.GUI.components.table.SingleTable;
import de.herrmannR.easydatabase.structure.Filter;

public class TableDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 306039785577770539L;

	private static final String CLOSE = "close action";
	private static final String ADD_NEW = "add_new_row action";

	private final Dimension minSize = new Dimension(1000, 300);
	private final String table;

	private SingleTable content;

	public TableDialog(DatabaseView owner, String table) {
		super(owner);
		this.table = table;

		this.setMinimumSize(minSize);
		this.getContentPane().setLayout(new BorderLayout());

		this.content = new SingleTable(this.getTable(), this, !Main.isMetaTable(table));
		JScrollPane tablePanel = new JScrollPane(content);

		JLabel header = new JLabel(this.getTable());
		header.setFont(new Font("Arial", Font.PLAIN, 18));

		JPanel buttons = new JPanel();

		if (!Main.isMetaTable(table)) {
			JButton add = new JButton("Add row");
			add.setActionCommand(ADD_NEW);
			add.addActionListener(this);
			buttons.add(add);
		}

		JButton close = new JButton("Close");
		close.setActionCommand(CLOSE);
		close.addActionListener(this);
		buttons.add(close);

		this.getContentPane().add(tablePanel, BorderLayout.CENTER);
		this.getContentPane().add(header, BorderLayout.NORTH);
		this.getContentPane().add(buttons, BorderLayout.SOUTH);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Main.adminFrame.onCloseDialog(TableDialog.this.getTable());
				Main.adminFrame.updateTable();
				super.windowClosed(e);
			}
		});

		this.setVisible(true);
	}

	public Filter getFilterForRow(int row) throws SQLException {
		Set<String> primCols = DatabaseManager.getInstance().getPrimaryCols(this.getTable());
		Filter filter = new Filter();
		for (String primCol : primCols) {
			int column = content.getColumn(primCol).getModelIndex();
			filter.addAttribute(primCol, content.getValueAt(row, column));
		}
		return filter;
	}

	public void openRowEditDialog(int row) {
		try {
			Filter primaryKeys = this.getFilterForRow(row);
			new EditRowDialog((DatabaseView) this.getParent(), this.getTable(), primaryKeys);
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
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CLOSE)) {
			((DatabaseView) this.getParent()).onCloseDialog(this.getTable());
			this.dispose();
			Main.adminFrame.updateTable();
		} else if (e.getActionCommand().equals(ADD_NEW)) {
			try {
				new AddRowDialog((DatabaseView) this.getParent(), this.getTable());
				this.reloadTable();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(getParent(), "Can't open AddRowDialog!\n" + e1.getMessage(),
						"SQL-Exception", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}

	public String getTable() {
		return table;
	}
}
