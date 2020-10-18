package de.herrmannR.easydatabase.GUI.admin.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.DatabaseView;
import de.herrmannR.easydatabase.GUI.components.ContentTable;
import de.herrmannR.easydatabase.GUI.util.DoubleClickListener;
import de.herrmannR.easydatabase.structure.Filter;

public class TableDialog extends JDialog implements DoubleClickListener, ActionListener {

	private static final long serialVersionUID = 306039785577770539L;

	private static final String CLOSE = "close action";
	private static final String ADD_NEW = "add_new_row action";

	private final Dimension minSize = new Dimension(1000, 300);
	private final String table;

	private JTable content;

	public TableDialog(DatabaseView parent, String table) {
		super(parent);
		this.table = table;

		this.setMinimumSize(minSize);
		this.getContentPane().setLayout(new BorderLayout());

		this.content = new ContentTable(table, this);
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
				super.windowClosed(e);
			}
		});

		this.setVisible(true);
	}

	@Override
	public void mouseDoubleClicked(MouseEvent e) {
		try {
			Set<String> primCols = DatabaseManager.getInstance().getPrimaryCols(table);
			Filter primaryKeys = new Filter();
			int row = content.rowAtPoint(e.getPoint());
			for (String primCol : primCols) {
				int column = content.getColumn(primCol).getModelIndex();
				primaryKeys.addAttribute(primCol, content.getValueAt(row, column));
			}
			new EditRowDialog((Frame) this.getParent(), table, primaryKeys);
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(getParent(), "Can't open EditRowDialog!\n" + e1.getMessage(), "SQL-Exception",
					JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CLOSE)) {
			((DatabaseView) this.getParent()).onCloseDialog(table);
			this.dispose();
		} else if (e.getActionCommand().equals(ADD_NEW)) {
			try {
				new AddRowDialog((Frame) this.getParent(), table);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(getParent(), "Can't open AddRowDialog!\n" + e1.getMessage(),
						"SQL-Exception", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}
}
