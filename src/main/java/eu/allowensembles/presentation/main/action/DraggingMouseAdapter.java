package eu.allowensembles.presentation.main.action;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import eu.allowensembles.presentation.main.process.ProcessModelPanel;

public class DraggingMouseAdapter extends MouseAdapter {

    private Point origin;
    private ProcessModelPanel processModelPanel;

    public DraggingMouseAdapter(ProcessModelPanel processModelPanel) {
	this.processModelPanel = processModelPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
	origin = new Point(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
	if (origin != null) {
	    JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(
		    JViewport.class, processModelPanel);
	    if (viewPort != null) {
		int deltaX = origin.x - e.getX();
		int deltaY = origin.y - e.getY();

		Rectangle view = viewPort.getViewRect();
		view.x += deltaX;
		view.y += deltaY;

		processModelPanel.scrollRectToVisible(view);
	    }
	}
    }

}
