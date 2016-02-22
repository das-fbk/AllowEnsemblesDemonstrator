package eu.allowensembles.evoknowledge.presentation;

import java.util.ArrayList;
import java.util.List;

import eu.allowensembles.evoknowledge.controller.Coordinate;

public class Util {

	public static List<String> polyLineToString(String encoded) {
		List<String> points = new ArrayList<String>();
		
		if (encoded.equals("")) {
			return points;
		}
		
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
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			points.add(String.valueOf((double) lng / 1E5) + "," + String.valueOf((double) lat / 1E5));
		}
		return points;
	}
	
	public static List<Coordinate> polylineToPoints(String encoded) {
		List<Coordinate> points = new ArrayList<Coordinate>();
		
		if (encoded.equals("")) {
			return points;
		}
		
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
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			points.add(new Coordinate((double) lng / 1E5, (double) lat / 1E5));
		}
		return points;
	}
}
