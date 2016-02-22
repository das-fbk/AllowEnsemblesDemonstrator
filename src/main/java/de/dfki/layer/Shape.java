package de.dfki.layer;

import java.util.List;

/**
 * Representes an abstract shpe which is given by a boundary (can be a single
 * point, too) and allows to identify whether a given point lies within its
 * boundaries.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class Shape {

	/**
	 * Tests whether the given point lies within the boundaries of the shape.
	 * For points lying exactly ON the boundary, the function may return either
	 * true or false.
	 * 
	 * @param point Point to test.
	 * 
	 * @return True, if point lies within the boundaries of the shape, false
	 *         otherwise.
	 */
	public abstract boolean contains(Coordinate point);
	
	/**
	 * Returns the boundary of the shape.
	 * 
	 * @return Boundary of the shape.
	 */
	public abstract List<Coordinate> getBoundary();
	
}
