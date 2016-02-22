package eu.allowensembles.presentation.main.map.listener;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jxmapviewer.JXMapViewer;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.presentation.main.map.viewer.MyWaypoint;

public class MapMouseListener extends MouseAdapter {

    private JXMapViewer mapViewer;
    private MainController controller;
    private JPopupMenu popup;

    public MapMouseListener(MainController controller, JXMapViewer mapViewer,
	    JPopupMenu popup) {
	this.controller = controller;
	this.mapViewer = mapViewer;
	this.popup = popup;
    }

    @Override
    public void mousePressed(MouseEvent e) {
	handleMapClick(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	handleMapClick(e);
    }

    private void handleMapClick(MouseEvent e) {
	Point2D gp_pt = null;

	List<MyWaypoint> waypoints = controller.getWayPoints();
	for (MyWaypoint waypoint : waypoints) {
	    // convert to world bitmap
	    gp_pt = mapViewer.getTileFactory().geoToPixel(
		    waypoint.getPosition(), mapViewer.getZoom());

	    // convert to screen
	    Rectangle rect = mapViewer.getViewportBounds();
	    Point converted_gp_pt = new Point((int) gp_pt.getX() - rect.x,
		    (int) gp_pt.getY() - rect.y);
	    // check if near the mouse
	    if (converted_gp_pt.distance(e.getPoint()) < 50) {
		if (SwingUtilities.isRightMouseButton(e)) {
		    showMenu(e, waypoint);
		}
	    }
	}

    }

    private void showMenu(MouseEvent e, MyWaypoint waypoint) {
	Component[] components = popup.getComponents();
	for (Component component : components) {
	    component.setVisible(false);
	}
	if (waypoint.getLabel().contains("Employee")) {
	    // enable only employee related
	    for (Component component : components) {
		if (component instanceof JMenuItem) {
		    JMenuItem item = (JMenuItem) component;
		    if (item.getText().contains("delay")) {
			item.setVisible(true);
		    }
		}
	    }
	    popup.show(e.getComponent(), e.getX(), e.getY());
	} else {
	    // disable employee related
	    // enable only employee related
	    for (Component component : components) {
		if (component instanceof JMenuItem) {
		    JMenuItem item = (JMenuItem) component;
		    if (!item.getText().contains("delay")) {
			item.setVisible(true);
		    }
		}
	    }
	    popup.show(e.getComponent(), e.getX(), e.getY());
	}
    }
}
