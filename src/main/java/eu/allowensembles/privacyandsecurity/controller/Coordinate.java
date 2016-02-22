package eu.allowensembles.privacyandsecurity.controller;

public class Coordinate {
	
	/*x coordinate.*/
	public double x;
	
	/*x coordinate.*/
	public double y;
	
	/**
	 * Constructor.
	 * Creates a new 2D coordinate with given x and y entries.
	 * 
	 * @param x x coordinate.
	 * @param y y coordinate.
	 */
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Constructor.
	 * Creates a new 2D coordinate with x = 0 and y = 0.
	 */
	public Coordinate() { }
	
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}