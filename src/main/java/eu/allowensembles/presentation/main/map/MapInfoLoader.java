package eu.allowensembles.presentation.main.map;

import static eu.allowensembles.presentation.main.map.MapInfo.BIKESHARING_POINTS;
import static eu.allowensembles.presentation.main.map.MapInfo.EXTENT;
import static eu.allowensembles.presentation.main.map.MapInfo.MAX_ZOOM;
import static eu.allowensembles.presentation.main.map.MapInfo.PARKING_POINTS;
import static eu.allowensembles.presentation.main.map.MapInfo.START_LAT;
import static eu.allowensembles.presentation.main.map.MapInfo.START_LON;
import static eu.allowensembles.presentation.main.map.MapInfo.START_ZOOM;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Utility class to load map info from file
 */
public final class MapInfoLoader {

    private static final Logger logger = LogManager
	    .getLogger(MapInfoLoader.class);

    public MapInfoLoader() {
    }

    public final MapInfo loadInfo(String fileName) throws IOException {
	logger.debug("loadInfo starting");
	Properties p = new Properties();
	try {
	    p.load(getClass().getClassLoader().getResourceAsStream(fileName));
	} catch (FileNotFoundException e) {
	    logger.error("Error loading file ", e);
	    return null;
	} catch (IOException e) {
	    logger.error("Error loading file ", e);
	    return null;
	}
	// setting info
	MapInfo mapInfo = new MapInfo();
	mapInfo.setZoom(Integer.valueOf(p.getProperty(START_ZOOM, "8")));
	mapInfo.setLat(Double.valueOf(p.getProperty(START_LAT, "46.0636")));
	mapInfo.setLon(Double.valueOf(p.getProperty(START_LON, "11.1260")));
	mapInfo.setMaxZoom(Integer.valueOf(p.getProperty(MAX_ZOOM, "8")));
	String extent = p.getProperty(EXTENT,
		"46.0775, 11.0911, 11.1770, 46.0352");
	String[] s = extent.split(",");
	mapInfo.setExtent(new MapExtent(Double.valueOf(s[0]), Double
		.valueOf(s[1]), Double.valueOf(s[2]), Double.valueOf(s[3])));
	mapInfo.setBikeSharingPoints(parseStringPoints(p.getProperty(
		BIKESHARING_POINTS, "")));
	mapInfo.setParkingPoints(parseStringPoints(p.getProperty(
		PARKING_POINTS, "")));
	logger.debug("loadInfo end, loaded " + mapInfo.toString());
	return mapInfo;
    }

    private List<GeoPosition> parseStringPoints(String value) {
	List<GeoPosition> result = new ArrayList<GeoPosition>();
	String[] points = value.split(";");
	for (int i = 0; i < points.length; i++) {
	    String[] v = points[i].split(",");
	    result.add(new GeoPosition(Double.valueOf(v[0]), Double
		    .valueOf(v[1])));
	}
	return result;
    }
}
