package eu.allowensembles.controller.executables;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.utility.controller.Preferences;
import eu.allowensembles.utility.controller.Utility;
import eu.allowensembles.utils.UserData;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * Rank journey alternative
 */
public class RankingExecutable extends AbstractExecutableActivityInterface {

    private MainController controller;
    private Utility utility;
    private Preferences pref = new Preferences();

    public RankingExecutable(MainController controller) {
	this.controller = controller;
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	String input = controller.getTypeForProcess(proc, "User");

	UserData userData = new UserData();
	userData = controller.getUserData(input);
	utility = new Utility();
	pref = userData.getPreferences();

	userData.setAlternatives(utility.rankAlternatives(userData, controller));

	System.out.println("High utility - alternative id: "
		+ userData.getAlternatives().get(0).getId() + " utility: "
		+ userData.getAlternatives().get(0).getUtility());
	proc.getCurrentActivity().setExecuted(true);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
	RankingExecutable c = new RankingExecutable(controller);
	c.pref = pref;
	c.utility = utility;
	return c;
    }

}
