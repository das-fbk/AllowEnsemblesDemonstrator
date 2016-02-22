package eu.allowensembles.privacyandsecurity.controller;

public class Leg {
	/*Leg unique identifier*/
	private int id;
	
	/*2D Coordinate of the start point of the leg*/
	private Coordinate start_point;
	
	/*2D Coordinate of the end point of the leg */
	private Coordinate end_point;
	
	/*mode of transportation used in this leg*/
	private String mode;
	
	public Leg(int id, Coordinate start_point, Coordinate end_point, String mode) {
		this.setId(id);
		this.setStart_point(start_point);
		this.setEnd_point(end_point);
		this.mode = mode;
	}
	
	public String getMode() {
		return mode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Coordinate getStart_point() {
		return start_point;
	}

	public void setStart_point(Coordinate start_point) {
		this.start_point = start_point;
	}

	public Coordinate getEnd_point() {
		return end_point;
	}

	public void setEnd_point(Coordinate end_point) {
		this.end_point = end_point;
	}
}
