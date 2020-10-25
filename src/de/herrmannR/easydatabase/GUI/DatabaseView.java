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

import de.herrmannR.easydatabase.GUI.components.table.ContentTableClickListener;
import de.herrmannR.easydatabase.GUI.components.table.DatabaseTable;
import de.herrmannR.easydatabase.GUI.dialogs.TableDialog;
import de.herrmannR.easydatabase.GUI.util.CloseHandling;

public class DatabaseView extends JFrame implements ContentTableClickListener {

	private static final long serialVersionUID = 5319220277392624846L;

	public final HashMap<String, TableDialog> tableViews = new HashMap<String, TableDialog>();

	private final Dimension minSize = new Dimension(1000, 600);

	private DatabaseTable content;

	public DatabaseView() {
		this.setMinimumSize(minSize);
		Dimension window = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((window.width - minSize.width) / 2, (window.height - minSize.height) / 2);
		this.init();
		this.addWindowListener(new CloseHandling());
	}

	private void init() {
		this.content = new DatabaseTable();
		this.content.addContentTableClickListener(this);
		JScrollPane tablePanel = new JScrollPane(this.content);
		this.getContentPane().add(tablePanel);
	}

	public void onCloseDialog(String table) {
		tableViews.put(table, null);
	}

	public void updateTable() {
		this.content.refresh();
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
