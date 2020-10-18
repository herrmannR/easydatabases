package de.herrmannR.easydatabase.GUI.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.herrmannR.easydatabase.DatabaseManager;

public class CloseHandling extends WindowAdapter {

	@Override
	public void windowClosing(WindowEvent e) {
		DatabaseManager.closeInstance();
		System.exit(0);
	}
}
