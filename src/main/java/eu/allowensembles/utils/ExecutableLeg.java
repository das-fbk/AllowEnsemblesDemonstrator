package eu.allowensembles.utils;

import java.util.List;

import org.jxmapviewer.viewer.GeoPosition;

import eu.allowensembles.presentation.main.map.Routes.Route.Leg.TransportType;

/**
 * Executable pojo for a Leg
 */
public class ExecutableLeg {

    private List<GeoPosition> points;
    private TransportType transportType;

    public void setTransportType(TransportType transportType) {
	this.transportType = transportType;
    }

    public void setPoints(List<GeoPosition> points) {
	this.points = points;
    }

    public List<GeoPosition> getPoints() {
	return points;
    }

    public TransportType getTransportType() {
	return transportType;
    }

}
