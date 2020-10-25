package de.herrmannR.easydatabase.GUI;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import de.herrmannR.easydatabase.GUI.components.table.DatabaseTable;
import de.herrmannR.easydatabase.GUI.dialogs.TableDialog;
import de.herrmannR.easydatabase.GUI.util.CloseHandling;

public class DatabaseView extends JFrame {

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
		this.content = new DatabaseTable(this);
		JScrollPane tablePanel = new JScrollPane(this.content);
		this.getContentPane().add(tablePanel);
	}

	public void onCloseDialog(String table) {
		tableViews.put(table, null);
	}

	public void updateTable() {
		this.content.refresh();
	}

	public void showTable(String tableName) {
		if (!tableViews.containsKey(tableName) || tableViews.get(tableName) == null) {
			tableViews.put(tableName, new TableDialog(this, tableName));
		} else {
			tableViews.get(tableName).toFront();
		}
	}
}
