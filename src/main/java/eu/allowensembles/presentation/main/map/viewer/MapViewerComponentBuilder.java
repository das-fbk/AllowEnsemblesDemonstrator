package eu.allowensembles.presentation.main.map.viewer;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.MouseInputListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.LocalResponseCache;
import org.jxmapviewer.viewer.TileFactoryInfo;

import eu.allowensembles.DemonstratorConstant;
import eu.allowensembles.presentation.main.map.MapInfo;
import eu.allowensembles.presentation.main.map.Routes;
import eu.allowensembles.presentation.main.map.Routes.Route;
import eu.allowensembles.presentation.main.map.Routes.Route.Leg;
import eu.allowensembles.presentation.main.map.listener.PanMouseMaxExtentListener;
import eu.allowensembles.presentation.main.map.listener.ZoomMaxMouseWheelListenerCenter;
import eu.allowensembles.presentation.main.map.util.GoogleMapsDecoder;
import eu.allowensembles.utils.ResourceLoader;

/**
 * Build map viewer component and init it on start location
 */
public class MapViewerComponentBuilder {

    private static final Logger logger = LogManager
	    .getLogger(MapViewerComponentBuilder.class);

    public MapViewerComponentBuilder() {
    }

    public JXMapViewer buildViewer() {
	logger.debug("buildViewer without loading routes");
	return buildViewer(null);
    }

    public JXMapViewer buildViewer(File routeFile) {
	logger.debug("buildViewer start");
	// define map info
	MapInfo mapInfo = ResourceLoader.getMapInfo();
	logger.debug("Loaded mapinfo.properties");

	// Setup JXMapViewer
	JXMapViewer mapViewer = new JXMapViewer();
	// Create a TileFactoryInfo for OSM
	TileFactoryInfo info = new OSMTileFactoryInfo();
	DefaultTileFactory tileFactory = new DefaultTileFactory(info);
	tileFactory.setThreadPoolSize(8);

	// Setup local file cache
	File cacheDir = new File(System.getProperty("user.home")
		+ File.separator + ".jxmapviewer2");
	LocalResponseCache.installResponseCache(info.getBaseURL(), cacheDir,
		false);

	mapViewer.setTileFactory(tileFactory);

	// Add interactions
	MouseInputListener mia = new PanMouseMaxExtentListener(mapViewer);
	mapViewer.addMouseListener(mia);
	mapViewer.addMouseMotionListener(mia);
	mapViewer.addMouseListener(new CenterMapListener(mapViewer));
	mapViewer.addMouseWheelListener(new ZoomMaxMouseWheelListenerCenter(
		mapViewer, mapInfo.getMaxZoom()));
	mapViewer.addKeyListener(new PanKeyListener(mapViewer));

	// load routes definitions
	try {
	    mapViewer = buildMap(mapViewer, mapInfo, routeFile);
	} catch (JAXBException e) {
	    logger.error("Error on loading routes ", e);
	}
	logger.debug("buildViewer end");
	return mapViewer;
    }

    private JXMapViewer buildMap(JXMapViewer mapViewer, MapInfo info,
	    File routeFile) throws JAXBException, IllegalArgumentException {
	logger.debug("buildMap start");
	// Set the focus
	mapViewer.setZoom(info.getZoom());
	mapViewer.setAddressLocation(new GeoPosition(info.getLat(), info
		.getLon()));
	mapViewer.setRestrictOutsidePanning(true);

	List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();

	JAXBContext context = JAXBContext.newInstance(Routes.class);

	if (routeFile != null && routeFile.exists()) {
	    logger.debug("Route file definition exist: "
		    + routeFile.getAbsolutePath());
	    Routes r = (Routes) context.createUnmarshaller().unmarshal(
		    routeFile);

	    int rn = r.getRoute().size();
	    logger.info(+rn + " routes found");
	    for (Route route : r.getRoute()) {
		for (Leg leg : route.getLeg()) {
		    List<GeoPosition> points = GoogleMapsDecoder.decode(leg
			    .getGeometry());
		    Painter<JXMapViewer> routePainter = buildPainter(points,
			    getColor(route.getColor()));

		    painters.add(routePainter);
		}
	    }

	    CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(
		    painters);
	    mapViewer.setOverlayPainter(painter);
	}
	logger.debug("buildMap end");
	return mapViewer;
    }

    // convert color from hex format to Java format
    private Color getColor(String color) {
	return Color.decode(color);
    }

    private Painter<JXMapViewer> buildPainter(List<GeoPosition> points,
	    Color color) {
	List<GeoPosition> track = new ArrayList<GeoPosition>();

	for (GeoPosition p : points) {
	    boolean found = false;
	    for (GeoPosition g : track) {
		if (g.getLatitude() == p.getLatitude()
			&& g.getLongitude() == p.getLongitude()) {
		    found = true;
		}
	    }
	    if (!found) {
		track.add(p);
	    }
	}
	ColorRoutePainter routePainter = new ColorRoutePainter(track, color);

	return routePainter;
    }

    public JXMapViewer addRoute(JXMapViewer mapViewer, List<Leg> legs,
	    Color color) {
	List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();

	List<GeoPosition> points = new ArrayList<GeoPosition>();

	for (Leg leg : legs) {
	    points.addAll(GoogleMapsDecoder.decode(leg.getGeometry()));
	}

	Painter<JXMapViewer> routePainter = buildPainter(points, color);
	painters.add(routePainter);

	// aggiungo layer
	CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(
		painters);
	((CompoundPainter<JXMapViewer>) mapViewer.getOverlayPainter())
		.addPainter(painter);
	// mapViewer.setOverlayPainter(painter);
	return mapViewer;
    }

    /**
     * Add legs on map using legs colors defined as Constants
     * 
     * @param mapPanel
     * @param legs
     */
    public JXMapViewer addRoute(JXMapViewer mapViewer, List<Leg> legs) {
	List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();

	for (Leg leg : legs) {
	    Color color = DemonstratorConstant.getColor(leg.getTransportType()
		    .getType());
	    List<GeoPosition> points = new ArrayList<GeoPosition>();
	    points.addAll(GoogleMapsDecoder.decode(leg.getGeometry()));
	    Painter<JXMapViewer> routePainter = buildPainter(points, color);
	    painters.add(routePainter);
	}

	// aggiungo layer
	CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(
		painters);
	mapViewer.setOverlayPainter(painter);
	return mapViewer;

    }
}
