package eu.allowensembles.utils;

import java.util.ArrayList;
import java.util.List;

import org.jxmapviewer.viewer.GeoPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

/**
 * A set of geometry-related utils
 */
public final class GeometryUtils {

    private static final double FACTOR = (Math.PI / 360.0);
    private static final double DEG2RAD = (Math.PI / 180.0);
    private static final double EARTH_FACTOR = 12742000.0; // 2 * 6371 * 1000.

    private GeometryUtils() {
    }

    /**
     * Simplify list of points using DouglasPeucker Algorithm, ensuring geometry
     * is consistent
     * 
     * @param points
     * @param distanceTolerance
     * 
     * @return list of simplified points
     * 
     * @see DouglasPeuckerSimplifier
     */
    public static List<GeoPosition> simplifyPoints(List<GeoPosition> points,
	    double distanceTolerance) {
	GeometryFactory gf = new GeometryFactory();
	LineString ls = gf.createLineString(convertToCoordinates(points));
	Geometry tempGeom = TopologyPreservingSimplifier.simplify(ls,
		distanceTolerance);

	Coordinate[] coordinates = tempGeom.getCoordinates();
	points = convertToPoints(coordinates);
	return points;
    }

    public static List<GeoPosition> convertToPoints(Coordinate[] coordinates) {
	List<GeoPosition> response = new ArrayList<GeoPosition>();
	for (int i = 0; i < coordinates.length; i++) {
	    response.add(new GeoPosition(coordinates[i].x, coordinates[i].y));
	}
	return response;
    }

    public static Coordinate[] convertToCoordinates(List<GeoPosition> points) {
	Coordinate[] response = new Coordinate[points.size()];
	int index = 0;
	for (GeoPosition p : points) {
	    response[index] = new Coordinate(p.getLatitude(), p.getLongitude());
	    index++;
	}
	return response;
    }

    public static List<de.dfki.layer.Coordinate> decodePolyline(String encoded) {
	List<de.dfki.layer.Coordinate> points = new ArrayList<de.dfki.layer.Coordinate>();

	if (encoded.equals("")) {
	    return points;
	}

	int index = 0, len = encoded.length();
	/* Workaround to avoid StringIndexOutOfBoundsException */
	len = len - 4;
	int lat = 0, lng = 0;

	while (index < len) {
	    int b, shift = 0, result = 0;
	    do {
		b = encoded.charAt(index++) - 63;
		result |= (b & 0x1f) << shift;
		shift += 5;
	    } while (b >= 0x20);
	    int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	    lat += dlat;

	    shift = 0;
	    result = 0;
	    do {
		b = encoded.charAt(index++) - 63;
		result |= (b & 0x1f) << shift;
		shift += 5;
	    } while (b >= 0x20);
	    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	    lng += dlng;

	    points.add(new de.dfki.layer.Coordinate(lng / 1E5, lat / 1E5));
	}
	return points;
    }

    public static double haversine(Coordinate start, Coordinate destination) {
	double sinDLon = Math.sin(FACTOR * (destination.x - start.x));
	double sinDLat = Math.sin(FACTOR * (destination.y - start.y));
	double a = sinDLat * sinDLat + sinDLon * sinDLon
		* Math.cos(DEG2RAD * destination.y)
		* Math.cos(DEG2RAD * start.y);
	return EARTH_FACTOR * Math.atan2(Math.sqrt(a), Math.sqrt((1.0 - a)));
    }
}
