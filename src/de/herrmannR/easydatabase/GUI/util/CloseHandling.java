package de.herrmannR.easydatabase.GUI.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.herrmannR.easydatabase.DatabaseManager;
import de.herrmannR.easydatabase.GUI.DatabaseView;

public class CloseHandling extends WindowAdapter {

	@Override
	public void windowClosing(WindowEvent e) {
		DatabaseManager.closeInstance(((DatabaseView) e.getWindow()).database);
		System.exit(0);
	}
}
