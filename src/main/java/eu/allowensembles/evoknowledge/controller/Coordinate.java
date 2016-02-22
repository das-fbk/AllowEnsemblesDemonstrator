package eu.allowensembles.evoknowledge.controller;

/**
 * Represents a 2D pair of coordinates.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Coordinate {
	/**
	 * x coordinate.
	 */
	public double x;
	
	/**
	 * y coordinate.
	 */
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
	 * Returns the Euclidean distance to another 2D coordinate.
	 * 
	 * @param other Other coordinate.
	 * @return Euclidean distance to another coordinate.
	 */
	public double distance(Coordinate other) {
		double diffX = x - other.x;
		double diffY = y - other.y;
		return Math.sqrt(diffX * diffX + diffY * diffY);
	}
	
	private static final double FACTOR = (Math.PI / 360.0);
	private static final double DEG2RAD = (Math.PI / 180.0);
	private static final double EARTH_FACTOR = 12742000.0; // 2 * 6371 * 1000.
	
	/**
	 * Returns the Haversine distance to another coordinate in m.
	 * 
	 * @param destination Other coordinate.
	 * @return Haversine distance in m.
	 */
	public double haversine(Coordinate destination) {
		double sinDLon = Math.sin(FACTOR * (destination.x - x));
		double sinDLat = Math.sin(FACTOR * (destination.y - y));
		double a = sinDLat * sinDLat + sinDLon * sinDLon * Math.cos(DEG2RAD * destination.y) * Math.cos(DEG2RAD * y);
		return EARTH_FACTOR * Math.atan2(Math.sqrt(a), Math.sqrt((1.0 - a)));
	}
	
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		
		if (other == this) {
			return true;
		}
		
		if (!(other instanceof Coordinate)) {
			return false;
			
		}
		Coordinate otherC = (Coordinate) other;
		return (otherC.x == x) && (otherC.y == y);
	}
	
	public int hashCode() {
		return (int) (37 + 31 * (((x == 0.0) ? 0L : Double.doubleToLongBits(x)) + ((y == 0.0) ? 0L : Double.doubleToLongBits(y))));
	}
}
