package de.dfki.layer;

import java.util.List;

/**
 * Represents an area defined by a boundary shape and a name.
 * Allows to query whether a certain point lies within the area.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class Area {
	// Name/identifier of the area, e.g. "city center".
	protected String name;
	
	// Shape of the area.
	protected Shape shape;
	
	/**
	 * Constructor.
	 * Creates a new instance of an area given its name and its boundary. 
	 * 
	 * @param name Name of the area to create.
	 * @param boundary Boundary of the area. In case there is only one point
	 *        a single point area is created.
	 */
	public Area(String name, List<Coordinate> boundary) {
		this.name = name;
		
		if (boundary.size() == 1) {
			shape = new SinglePointShape(boundary.get(0));
			
		} else if (boundary.size() > 1) {
			shape = new PolygonShape(boundary);
		}
	}

	/**
	 * Constructor.
	 * Creates a new instance if a single point area
	 * 
	 * @param name
	 * @param point
	 */
	public Area(String name, Coordinate point) {
		this.name = name;
		shape = new SinglePointShape(point);
	}
	
	/**
	 * Returns the name/identifier of the area, e.g. "city center".
	 * 
	 * @return Name/identifier of the area.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Determines whether a given point lies inside the area or not.
	 * 
	 * @param point Point to test.
	 * 
	 * @return True, if point lies within the area, false otherwise.
	 */
	public boolean contains(Coordinate point) {
		return shape.contains(point);
	}
	
	/**
	 * Returns the boundary of the area given by a set of points/vertices.
	 * 
	 * @return Boundary of the area.
	 */
	public List<Coordinate> getBoundary() {
		return shape.getBoundary();
	}
}
