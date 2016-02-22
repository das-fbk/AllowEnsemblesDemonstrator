package eu.allowensembles.presentation.main.map;

public class MapExtent {

    private double north;
    private double west;
    private double east;
    private double south;

    public MapExtent(double n, double w, double e, double s) {
	this.north = n;
	this.west = w;
	this.east = e;
	this.south = s;
    }

    public double getNorth() {
	return north;
    }

    public double getWest() {
	return west;
    }

    public double getEast() {
	return east;
    }

    public double getSouth() {
	return south;
    }

    public boolean contains(double x, double y) {
	return x > west && x < east && y > north && y < south;
    }

    @Override
    public String toString() {
	return "n:" + north + ",w:" + west + ",e:" + east + ",s:" + south;
    }
}
