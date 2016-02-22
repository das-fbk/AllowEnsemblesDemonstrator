package eu.allowensembles.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.allowensembles.DemonstratorConstant;
import eu.allowensembles.controller.MainController;
import eu.allowensembles.presentation.main.events.StoryboardLoadedEvent;
import eu.allowensembles.presentation.main.map.MapInfo;
import eu.allowensembles.presentation.main.map.MapInfoLoader;
import eu.allowensembles.presentation.main.map.Routes.Route;

public class ResourceLoader {

    private static final Logger logger = LogManager
	    .getLogger(ResourceLoader.class);

    private static MapInfo mapInfo = null;

    private static Storyboard storyboard;

    private static File storyboardFile;

    private static File routeFile;

    private static File scenarioFile;

    private static File coordinateFile;

    private static final String FILE_SEPARATOR = System
	    .getProperty("file.separator");

    private static final String COORDINATE_FILENAME = "coordinates.properties";

    private static Properties coordinates;

    public static MapInfo load(String string) {
	if (mapInfo != null) {
	    return mapInfo;
	}
	try {
	    MapInfoLoader mil = new MapInfoLoader();
	    mapInfo = mil.loadInfo("map/mapinfo.properties");
	} catch (IOException e) {
	    logger.error("Error on loading map/mapinfo.properties", e);
	    return null;
	}
	return mapInfo;
    }

    public static MapInfo getMapInfo() {
	return load("mapinfo.properties");
    }

    public static void loadStoryboard(File f) throws JAXBException {
	try {
	    // read storyboard
	    storyboard = null;
	    JAXBContext context = JAXBContext.newInstance(Storyboard.class);
	    storyboard = (Storyboard) context.createUnmarshaller().unmarshal(f);
	    storyboardFile = f;
	    // read routeFile
	    routeFile = new File(storyboardFile.getParent() + FILE_SEPARATOR
		    + storyboard.getRoutes());
	    if (!routeFile.exists()) {
		logger.warn("File " + routeFile.getAbsolutePath()
			+ " does not exist");
		return;
	    }
	    // read scenarioFile
	    scenarioFile = new File(storyboardFile.getParent() + FILE_SEPARATOR
		    + storyboard.getScenario());
	    if (!scenarioFile.exists()) {
		logger.warn("File " + routeFile.getAbsolutePath()
			+ " does not exist");
		return;
	    }

	    logger.info("Storyboard loaded");
	    // notify eventBus
	    MainController.post(new StoryboardLoadedEvent());
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(null,
		    "Error on loading storyboard definition " + e.getMessage());
	    logger.error(e.getMessage(), e);
	}
    }

    public static Storyboard getStoryboard() {
	return storyboard;
    }

    public static File getStoryboardFile() {
	return storyboardFile;
    }

    public static File getRouteFile() {
	return routeFile;
    }

    public static File getScenarioFile() {
	return scenarioFile;
    }

    private static File loadCoordinateFile() {
	if (coordinateFile == null) {
	    coordinateFile = new File(storyboardFile.getParent()
		    + FILE_SEPARATOR + COORDINATE_FILENAME);
	}
	return coordinateFile;
    }

    public static Properties loadCoordinates() {
	if (coordinates == null) {
	    coordinates = new Properties();
	    try {
		coordinates.load(new FileInputStream(loadCoordinateFile()));
	    } catch (IOException e) {
		logger.error(e.getMessage(), e);
		return new Properties();
	    }
	}
	return coordinates;
    }

    public static BufferedImage getImageForTransportType(String transportType) {
	try {
	    URL resource = ResourceLoader.class
		    .getResource("/images/waypoint_white_" + transportType
			    + ".png");
	    return ImageIO.read(resource);
	} catch (Exception ex) {
	    try {
		URL resource = ResourceLoader.class
			.getResource("/images/waypoint_white_" + transportType
				+ ".png");
		return ImageIO.read(resource);
	    } catch (IOException e) {
		logger.error("Impossible to load /images/waypoint_white_"
			+ transportType + ".png");
	    }
	}
	return null;
    }

    private static int getPickPointNumbersForFlexibusRoute(Route r) {
	int pn = 0;
	return r.getLeg()
		.stream()
		.filter(rn -> rn.getTransportType().equals(
			DemonstratorConstant.FLEXIBUS))
		.collect(Collectors.toList()).size();
    }
}
