package de.herrmannR.easydatabase.GUI.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface DoubleClickListener extends MouseListener {

	public void mouseDoubleClicked(MouseEvent e);

	@Override
	default void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			mouseDoubleClicked(e);
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
