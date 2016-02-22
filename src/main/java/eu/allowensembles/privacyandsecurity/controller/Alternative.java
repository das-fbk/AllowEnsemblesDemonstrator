package eu.allowensembles.privacyandsecurity.controller;

import java.util.List;

public class Alternative{
	
	/*Alternative unique identifier*/
	private int id;
	/*The number of changes that an alternative has*/
	private int NoOfChanges;
	/*The walking distance that an alternative has*/
	private long WalkingDistance;
	/*The total estimated travel time for an alternative*/
	private long TravelTime;
	/*The total estimated cost for an alternative*/
	private double Cost;
	/*The estimated utility for an alternative*/
	private double Utility;
	/*List of legs in the alternative*/
	private List<Leg> legs;
	
	public Alternative(int id, int noc, long wd, long tt, double c, double u, List<Leg> l){
		this.id=id;
		this.NoOfChanges = noc;
		this.WalkingDistance = wd;
		this.TravelTime = tt;
		this.Cost = c;
		this.Utility = u;
		this.legs = l;
	}
	
	public Alternative(){
	}
	
	public int getId() {
		return id;
	}
	
	public int getNoOfChanges() {
		return NoOfChanges;
	}
	
	public long getWalkingDistance() {
		return WalkingDistance;
	}
	
	public long getTravelTime() {
		return TravelTime;
	}
	
	public double getCost() {
		return Cost;
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
	
	public void setWalkingDistance(long wd) {
		WalkingDistance = wd;
	}
	
	public void setTravelTime(long tt) {
		TravelTime = tt;
	}
	
	public void setCost(double c) {
		Cost = c;
	}
	
	
	public void setUtility(double u){
		this.Utility = u;
	}

	public List<Leg> getLegs() {
		return legs;
	}

	public void setLegs(List<Leg> legs) {
		this.legs = legs;
	}
	
	public int getSafetyCriticalityLevel() {
		int ret = 0;
		if (this.id == 1) {
			ret = 4;
		}
		else if (this.id == 2) {
			ret = 3;
		}
		else if (this.id == 3) {
			ret = 4;
		}
		else if (this.id == 4) {
			ret = 1;
		}
		return ret;
	}
	
}