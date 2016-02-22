package eu.allowensembles.utils;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jxmapviewer.viewer.GeoPosition;

import eu.allowensembles.presentation.main.map.viewer.MyWaypoint;

public final class WaypointUtil {

    /**
     * @return build {@link MyWaypoint} using provided informations
     */
    public static MyWaypoint buildMapIcon(String label, Color color,
	    GeoPosition position, URL resource) {
	MyWaypoint wp = new MyWaypoint(label, color, position);
	if (resource != null) {
	    try {
		wp.setImage(ImageIO.read(resource));
	    } catch (IOException e) {
	    }
	}
	return wp;
    }

}
