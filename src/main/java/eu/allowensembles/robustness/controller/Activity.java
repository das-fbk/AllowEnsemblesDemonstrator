package eu.allowensembles.robustness.controller;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "activity")
public class Activity {

	private boolean compensable;
	private boolean write;
	private boolean deterministic;
	private String name;
	private int robustness;

	public void setName(String name) {
		this.name = name;
	}

	public void setCompensable(boolean compensable) {
		this.compensable = compensable;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	public void setDeterministic(boolean deterministic) {
		this.deterministic = deterministic;
	}
	
	public void setRobustness(int robustness) {
		this.robustness = robustness;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	@XmlAttribute(name = "compensable")
	public boolean getCompensable() {
		return compensable;
	}

	@XmlAttribute(name = "write")
	public boolean getWrite() {
		return write;
	}

	@XmlAttribute(name = "deterministic")
	public boolean getDeterministic() {
		return deterministic;
	}
	
	@XmlAttribute(name = "robustness")
	public int getRobustness() {
		return robustness;
	}

	public String toString() {
		return name + " c: " + compensable + " w: " + write + " d: " + deterministic + " r: " + robustness;
	}
	
	public String details() {
		return "c: " + compensable + "\nw: " + write + "\nd: " + deterministic;
	}
	
	public boolean isActivelyReplicated() {
		return deterministic && !write;
	}
	
	
	public static Activity generateNewActivity(String name) {
		Activity activity = new Activity();
		activity.setCompensable(true);
		activity.setDeterministic(false);
		activity.setWrite(true);
		activity.setName(name);
		activity.setRobustness((int)(Math.random() * 10) + 90);
		return activity;
	}

}