package eu.allowensembles.presentation.main.map.util;

import java.util.ArrayList;
import java.util.List;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * Google map geometry decoder
 * 
 * @see https://github.com/googlemaps/android-maps-utils
 */
public class GoogleMapsDecoder {

    private GoogleMapsDecoder() {
    }

    /**
     * Decode a google-maps geometry in a list of lat/lon points
     * 
     * @param encodedPath
     *            as String
     * @return list of {@link GeoPosition} or null if error
     */

    public static List<GeoPosition> decode(String encoded) {
	List<GeoPosition> polyline = new ArrayList<GeoPosition>();
	int index = 0, len = encoded.length();
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
	    if (index >= len) {
		break;
	    }
	    do {
		b = encoded.charAt(index++) - 63;
		result |= (b & 0x1f) << shift;
		shift += 5;
	    } while (b >= 0x20);
	    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	    lng += dlng;

	    GeoPosition p = new GeoPosition(lat / 1E5, lng / 1E5);
	    polyline.add(p);
	}

	return polyline;
    }

}
