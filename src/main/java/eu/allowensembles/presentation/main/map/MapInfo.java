package eu.allowensembles.presentation.main.map;

import java.util.List;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Init info for a {@link JXMapViewer} map
 */
public class MapInfo {

    public static final String START_ZOOM = "startZoom";
    public static final String START_LAT = "startLat";
    public static final String START_LON = "startLon";
    public static final String MAX_ZOOM = "maxZoom";
    public static final String EXTENT = "extent";
    public static final String BIKESHARING_POINTS = "bikeSharingPoints";
    public static final String PARKING_POINTS = "parkingPoints";

    private int zoom;
    private double lat;
    private double lon;
    private int maxZoom;
    private MapExtent extent;
    private List<GeoPosition> bikeSharingPoints;
    private List<GeoPosition> parkingPoints;

    public int getMaxZoom() {
	return maxZoom;
    }

    public void setMaxZoom(int maxZoom) {
	this.maxZoom = maxZoom;
    }

    public int getZoom() {
	return zoom;
    }

    public void setZoom(int zoom) {
	this.zoom = zoom;
    }

    public void setLat(double d) {
	this.lat = d;
    }

    public double getLat() {
	return lat;
    }

    public void setLon(double d) {
	this.lon = d;
    }

    public double getLon() {
	return lon;
    }

    public MapExtent getExtent() {
	return extent;
    }

    public void setExtent(MapExtent extent) {
	this.extent = extent;
    }

    @Override
    public String toString() {
	return "MaxZoom:" + maxZoom + ",Zoom:" + zoom + ",Lat:" + lat + ",Lon:"
		+ lon + ",Extent:" + extent;
    }

    public List<GeoPosition> getBikeSharingPoints() {
	return bikeSharingPoints;
    }

    public void setBikeSharingPoints(List<GeoPosition> bikeSharingPoints) {
	this.bikeSharingPoints = bikeSharingPoints;
    }

    public List<GeoPosition> getParkingPoints() {
	return parkingPoints;
    }

    public void setParkingPoints(List<GeoPosition> parkingPoints) {
	this.parkingPoints = parkingPoints;
    }

}
