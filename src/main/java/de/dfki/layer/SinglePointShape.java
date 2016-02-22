package de.dfki.layer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represented a shape which is given by a single point only.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class SinglePointShape extends Shape {
	// Point of the shape.
	private Coordinate point; 
	
	/**
	 * Constructor.
	 * Creates a new instance of a single point area given the point of interest.
	 * 
	 * @param point Point that representes the single point shape.
	 */
	public SinglePointShape(Coordinate point) {
		this.point = point;
	}
	
	@Override
	public boolean contains(Coordinate other) {
		return point.equals(other);
	}

	@Override
	public List<Coordinate> getBoundary() {
		ArrayList<Coordinate> ret = new ArrayList<Coordinate>();
		ret.add(point);
		return ret;
	}

}
