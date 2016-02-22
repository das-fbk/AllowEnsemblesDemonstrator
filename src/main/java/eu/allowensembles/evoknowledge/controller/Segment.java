package eu.allowensembles.evoknowledge.controller;

public class Segment {

	private String label;
	private String mode;
	private double length;
	private double travelTime;
	private double costs;
	
	public Segment(String label,
			String mode,
			double length) {
		this.label = label;
		this.mode = mode;
		this.length = length;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getMode() {
		return mode;
	}
	
	public double getLength() {
		return length;
	}
	
	public double getTravelTime() {
		return travelTime;
	}
	
	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}
	
	public double getCosts() {
		return costs;
	}
	
	public void setCosts(double costs) {
		this.costs = costs;
	}
}
