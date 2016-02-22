package eu.allowensembles.presentation.main.map.listener;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.PanMouseInputListener;

/**
 * A mouse listener for map pan actions, see {@link PanMouseInputListener}
 */
public class PanMouseMaxExtentListener extends MouseInputAdapter {

    private Point prev;
    private JXMapViewer viewer;
    private Cursor priorCursor;

    /**
     * @param viewer
     *            the jxmapviewer
     */
    public PanMouseMaxExtentListener(JXMapViewer viewer) {
	this.viewer = viewer;
    }

    @Override
    public void mousePressed(MouseEvent evt) {
	prev = evt.getPoint();
	priorCursor = viewer.getCursor();
	viewer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
	if (!SwingUtilities.isLeftMouseButton(evt))
	    return;

	Point current = evt.getPoint();
	double x = viewer.getCenter().getX();
	double y = viewer.getCenter().getY();

	if (prev != null) {
	    x += prev.x - current.x;
	    y += prev.y - current.y;
	}

	if (!viewer.isNegativeYAllowed()) {
	    if (y < 0) {
		y = 0;
	    }
	}

	int maxHeight = (int) (viewer.getTileFactory()
		.getMapSize(viewer.getZoom()).getHeight() * viewer
		.getTileFactory().getTileSize(viewer.getZoom()));
	if (y > maxHeight) {
	    y = maxHeight;
	}

	prev = current;
	viewer.setCenter(new Point2D.Double(x, y));
	viewer.repaint();

    }

    @Override
    public void mouseReleased(MouseEvent evt) {
	if (!SwingUtilities.isLeftMouseButton(evt))
	    return;

	prev = null;
	viewer.setCursor(priorCursor);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		viewer.requestFocusInWindow();
	    }
	});
    }
}
