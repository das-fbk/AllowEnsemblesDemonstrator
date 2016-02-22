package eu.allowensembles.robustness.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "robustnessAnnotation")
public class RobustnessAnnotation {
	private ArrayList<Process> proccesses;
	private ArrayList<Fragment> fragments;

	public void setProcesses(ArrayList<Process> proccesses) {
		this.proccesses = proccesses;
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		this.fragments = fragments;
	}

	@XmlElement(name = "process")
	public ArrayList<Process> getProcesses() {
		return proccesses;
	}

	@XmlElement(name = "fragment")
	public ArrayList<Fragment> getFragments() {
		return fragments;
	}

	public String toString() {
		StringBuilder bldr = new StringBuilder();
		for (Process p : proccesses) {
			for (Activity a : p.getActivities()) {
				bldr.append(a.getName() + ";");
			}
		}
		return bldr.toString();
	}

	public static void main(String[] args) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(RobustnessAnnotation.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Path robustnessPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "storyboard1",
					"robustness");
			File robustnessDir = robustnessPath.toFile();
			File[] files = robustnessDir.listFiles();
			for (File f : files) {
				System.out.println(f.getName());
				RobustnessAnnotation annotation = (RobustnessAnnotation) jaxbUnmarshaller.unmarshal(f);
				System.out.println(annotation);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

@XmlRootElement(name = "process")
class Process {
	private ArrayList<Activity> activities;
	private String name;

	public void setActivities(ArrayList<Activity> activities) {
		this.activities = activities;
	}

	@XmlElement(name = "activity")
	public ArrayList<Activity> getActivities() {
		return activities;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
}

@XmlRootElement(name = "fragment")
class Fragment {

	private ArrayList<Activity> activities;
	private String name;

	public void setActivities(ArrayList<Activity> activities) {
		this.activities = activities;
	}

	@XmlElement(name = "activity")
	public ArrayList<Activity> getActivities() {
		return activities;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

}
