package de.herrmannR.easydatabase.GUI.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

public interface ContentTableClickListener extends MouseListener {

	public void mouseDoubleClicked(MouseEvent e);

	public void showPopUpMenu(MouseEvent e);

	@Override
	default void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (e.getClickCount() == 2) {
				mouseDoubleClicked(e);
			}
		} else if (SwingUtilities.isRightMouseButton(e)) {
			showPopUpMenu(e);
		}
	}

	@Override
	default void mousePressed(MouseEvent e) {

	}

	@Override
	default void mouseReleased(MouseEvent e) {

	}

	@Override
	default void mouseEntered(MouseEvent e) {

	}

	@Override
	default void mouseExited(MouseEvent e) {

	}
}
