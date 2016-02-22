package de.dfki.layer;

import java.util.List;

public class SafetyArea extends Area {
	// Safety level associated with the area.
	private int safetyLevel;
	
	/**
	 * Constructor
	 * Creates a new instance of a SafetyArea given its name, boundary, and
	 * safety level.
	 * 
	 * @param name A string identifier i.e. a name of the area
	 * @param boundary List of points describing the boundary of the area
	 * @param safetyLevel Safety level associated with the area
	 */
	public SafetyArea(String name, List<Coordinate> boundary, int safetyLevel) {
		super(name, boundary);
		this.safetyLevel = safetyLevel;
	}
	
	/**
	 * Returns the safety level of this area.
	 * 
	 * @return Safety level of this area.
	 */
	public int getSafetyLevel() {
		return safetyLevel;
	}

}
