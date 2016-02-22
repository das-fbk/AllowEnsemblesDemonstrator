package eu.allowensembles.presentation.main.map.listener;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.jxmapviewer.JXMapViewer;

/**
 * zooms using the mouse wheel on the view center
 */
public class ZoomMaxMouseWheelListenerCenter implements MouseWheelListener {
    private JXMapViewer viewer;
    private int maxZoom;

    /**
     * @param viewer
     *            the jxmapviewer
     */
    public ZoomMaxMouseWheelListenerCenter(JXMapViewer viewer, int maxZoom) {
	this.viewer = viewer;
	this.maxZoom = maxZoom;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
	int zoom = viewer.getZoom() + e.getWheelRotation() < maxZoom ? viewer
		.getZoom() + e.getWheelRotation() : maxZoom;
	viewer.setZoom(zoom);

    }

}
