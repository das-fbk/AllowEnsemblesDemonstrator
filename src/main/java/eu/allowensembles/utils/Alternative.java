package eu.allowensembles.utils;

import java.util.ArrayList;
import java.util.List;

import de.dfki.layer.Area;
import de.dfki.layer.Coordinate;
import de.dfki.layer.Layer;
import de.dfki.layer.SafetyArea;
import eu.allowensembles.presentation.main.map.Routes.Route.Leg;

public class Alternative {

    /* Alternative unique identifier */
    private int id;
    /* The number of changes that an alternative has */
    private int NoOfChanges;
    /* The walking distance that an alternative has */
    private double WalkingDistance;
    /* The total estimated travel time for an alternative */
    private long TravelTime;
    /* The total estimated cost for an alternative */
    private double Cost;
    /* The modes of transportation used for an alternative */
    private String Modes;
    /* The list of the legs of the alternative */
    private List<Leg> Legs;
    /* The estimated utility for an alternative */
    private double Utility;

    public Alternative(int id, int noc, double wd, long tt, double c,
	    String modes, List<Leg> legs, double u) {
	this.id = id;
	this.NoOfChanges = noc;
	this.WalkingDistance = wd;
	this.TravelTime = tt;
	this.Cost = c;
	this.Modes = modes;
	this.Legs = legs;
	this.Utility = u;
    }

    public int getId() {
	return id;
    }

    public int getNoOfChanges() {
	return NoOfChanges;
    }

    public double getWalkingDistance() {
	return WalkingDistance;
    }

    public long getTravelTime() {
	return TravelTime;
    }

    public double getCost() {
	return Cost;
    }

    public String getModes() {
	return Modes;
    }

    public List<Leg> getLegs() {
	return this.Legs;
    }

    public double getUtility() {
	return Utility;
    }

    public void setId(int id) {
	this.id = id;
    }

    public void setNoOfChanges(int noc) {
	NoOfChanges = noc;
    }

    public void setWalkingDistance(double wd) {
	WalkingDistance = wd;
    }

    public void setTravelTime(long tt) {
	TravelTime = tt;
    }

    public void setCost(double c) {
	Cost = c;
    }

    public void setModes(String modes) {
	Modes = modes;
    }

    public void setLegs(List<Leg> legs) {
	this.Legs = legs;
    }

    public void setUtility(double u) {
	this.Utility = u;
    }

    /*** Dummy method ***/
    /*
     * public int getSafetyLevel() { int ret = 0; if (this.id == 1) { ret = 4; }
     * else if (this.id == 2) { ret = 3; } else if (this.id == 3) { ret = 4; }
     * else if (this.id == 4) { ret = 1; } return ret; }
     */
    /**
     * Return the max safety level of a journey by considering walking and
     * biking legs. Return 0 if there are no walking or biking legs in a journey
     * 
     * @param layer
     *            - an object that contains the map partition of Trento
     *            according to safety.
     */
    public int calculateSafetyLevel(Layer<SafetyArea> layer) {
	List<Area> safAreas = new ArrayList<Area>();
	List<Coordinate> itLegCoordinate = new ArrayList<Coordinate>();
	String itLegTransType;
	int safLevel = 0;

	for (int i = 0; i < this.Legs.size(); i++) {
	    itLegCoordinate = GeometryUtils.decodePolyline(this.Legs.get(i)
		    .getGeometry());
	    itLegTransType = this.Legs.get(i).getTransportType().getType();
	    for (int j = 0; j < itLegCoordinate.size(); j++) {
		safAreas = layer
			.getAreasContainingPoint(itLegCoordinate.get(j));
		for (Area currentArea : safAreas) {
		    if (((SafetyArea) currentArea).getSafetyLevel() > safLevel
			    && (itLegTransType.equals("bicycle") || itLegTransType
				    .equals("walk"))) {
			safLevel = ((SafetyArea) currentArea).getSafetyLevel();
		    }
		}
	    }
	}
	return safLevel;
    }

}