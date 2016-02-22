package eu.allowensembles.privacyandsecurity.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.dfki.layer.Coordinate;
import de.dfki.layer.Layer;
import de.dfki.layer.SafetyArea;
import eu.allowensembles.controller.MainController;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.UserData;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

public class PrivacyAndSecurity extends AbstractExecutableActivityInterface
	implements IPrivacyAndSecurity {

    UserData userData;
    private MainController controller;
    private List<Alternative> alt;

    /**
     * Return privacy and security parameters for a given journey alternative.
     * 
     * @return USP (Unsatisfied security Preferences) and WSD (Willingness to
     *         Share Data) parameters
     */

    /*
     * @Override public double[] getPSParameters(Alternative alt) { double USP =
     * 0; double WSD = 0;
     * 
     * return new double[] { USP, WSD }; }
     */

    /**
     * This is the actual method. Currently, the values of sensitivity of
     * privacy attributes are the same for all providers and the request mask
     * vector has all the elements equals to 1.
     */

    @Override
    public double[] getPSParameters(String user) {
	double USP = 0;
	double WSD = 0;
	int numPrivacyPref = 4;

	userData = new UserData();
	userData = controller.getUserData(user);
	WSD = (userData.getPreferences().getNamesens()
		+ userData.getPreferences().getEmailsens()
		+ userData.getPreferences().getPhonesens() + userData
		.getPreferences().getGpssens()) / numPrivacyPref;
	return new double[] { USP, WSD };
    }

    public PrivacyAndSecurity(MainController mc) {
	this.controller = mc;
    }

    /* I would need to have user preferences as input here */
    public PrivacyAndSecurity() {
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	String input = controller.getTypeForProcess(proc, "User");

	/* Load safety partitioning layer */

	String fileName = "src/main/resources/map/safety.layer";
	List<String> lines = null;
	try {
	    lines = Files.readAllLines(Paths.get(fileName));
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	final Layer<SafetyArea> newLayer = new Layer<SafetyArea>("safety");
	for (String line : lines) {
	    String tokens[] = line.split(";;");

	    // Parse vertices.
	    String vertices[] = tokens[1].split(",");
	    List<Coordinate> polygon = new ArrayList<Coordinate>(
		    vertices.length);

	    for (String vertex : vertices) {
		String coord[] = vertex.split(" ");
		polygon.add(new Coordinate(Double.parseDouble(coord[0]), Double
			.parseDouble(coord[1])));
	    }
	    int safetyLevel = Integer.parseInt(tokens[2]);
	    SafetyArea newArea = new SafetyArea(tokens[0], polygon, safetyLevel);
	    // System.out.println("Adding new area " + newArea.getName());
	    newLayer.addArea(newArea);
	}
	userData = new UserData();
	userData = controller.getUserData(input);
	alt = new ArrayList<Alternative>();
	alt = userData.getAlternatives();
	
	/* For debugging  
	 * for (int i = 0; i < alt.size(); i++) {
	 * System.out.println(alt.get(i).getId()); }
	 */
	for (int i = 0; i < alt.size(); i++) {
	    int safLevel = alt.get(i).calculateSafetyLevel(newLayer);
	    if (safLevel > 3) {
	    	alt.remove(i);
	    	i--;
	    }
	}
	
	/* For debugging  
	 * for (int i = 0; i < alt.size(); i++) {
	 * System.out.println(alt.get(i).getId()); }
	 */
	proc.getCurrentActivity().setExecuted(true);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
	PrivacyAndSecurity c = new PrivacyAndSecurity(controller);
	c.alt = alt;
	c.userData = userData;
	return c;
    }

}
