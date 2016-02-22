package eu.allowensembles.robustness.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.fbk.das.process.engine.api.domain.ProcessActivity;

public class RobustnessAnnotationHandler {

	private ArrayList<RobustnessAnnotation> annotations;
	private HashMap<String, Activity> activities;

	public RobustnessAnnotationHandler() {
		annotations = new ArrayList<>();
		activities = new HashMap<>();
		readAll();
	}

	public Activity getActivity(String name) {
		if (activities.containsKey(name)) {
			return activities.get(name);
		} else {
			return Activity.generateNewActivity(name);
		}
	}
	
	public Activity getActivity(ProcessActivity activity) {
		return getActivity(activity.getName());
	}

	private void readAll() {
		annotations.clear();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(RobustnessAnnotation.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Path robustnessPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "storyboard1",
					"robustness");
			File robustnessDir = robustnessPath.toFile();
			File[] files = robustnessDir.listFiles();
			for (File f : files) {
				RobustnessAnnotation annotation = (RobustnessAnnotation) jaxbUnmarshaller.unmarshal(f);
				annotations.add(annotation);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buildHashmap();
	}

	private void buildHashmap() {
		ArrayList<Activity> activities = new ArrayList<>();
		for (RobustnessAnnotation annotation : annotations) {
			if (annotation.getProcesses() != null) {
				for (Process p : annotation.getProcesses()) {
					activities.addAll(p.getActivities());
				}
			}
			if (annotation.getFragments() != null) {
				for (Fragment f : annotation.getFragments()) {
					activities.addAll(f.getActivities());
				}
			}
		}
		for (Activity activity : activities) {
			this.activities.put(activity.getName(), activity);
		}
	}
}
