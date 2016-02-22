package eu.allowensembles.controller.executables;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;

import eu.allowensembles.DemonstratorConstant;
import eu.allowensembles.controller.MainController;
import eu.allowensembles.controller.events.SelectJourneyEvent;
import eu.allowensembles.presentation.main.map.Routes.Route.Leg;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.UserData;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * Handler for processEngine in order to show window for alternative selection
 */
public class UserChooseAlternativeExecutable extends
	AbstractExecutableActivityInterface {

    private static final Logger logger = LogManager
	    .getLogger(UserChooseAlternativeExecutable.class);

    private MainController controller;
    private ProcessDiagram current;
    private String name;
    private boolean windowDisplay = false;

    public UserChooseAlternativeExecutable(MainController controller) {
	this.controller = controller;
	MainController.register(this);
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	if (pa.isExecuted()) {
	    // if activity is already executed, do nothing
	    return;
	}
	// display window to the user, note: if user select an alternative,
	// process can continue, otherwise is waiting for user, because this is
	// an human activity

	// note: this control have to be re-enabled if we show alternative
	// selection window to user
	// stop play if running
	// PlayRunner.getDefault().stop();

	name = controller.getTypeForProcess(proc, "User");
	UserData ud = controller.getUserData(name);
	current = proc;

	// Disabled after introducing more than one instance, best journey
	// alternative (first one, because they are ranked) are selected
	if (windowDisplay) {
	    if (!pa.isExecuted()) {
		controller.displayAlternativesFor(name);
	    }
	} else {
	    logger.debug("Select alternative for user " + name);
	    DomainObjectInstance doi = controller.getProcessEngineFacade()
		    .getDomainObjectInstanceForProcess(proc);
	    ud.setSelectedAlternative(toggleFromAlternativeFlexibus(findAlternative(
		    ud.getAlternatives(), doi.getSelectedRoute())));
	    //
	    // if (name.equals("User_1")) {
	    // ud.setSelectedAlternative(toggleFromAlternativeFlexibus(findAlternative(
	    // ud.getAlternatives(), "2")));
	    // } else if (name.equals("User_2")) {
	    // ud.setSelectedAlternative(toggleFromAlternativeFlexibus(findAlternative(
	    // ud.getAlternatives(), "3")));
	    // } else if (name.equals("User_3")) {
	    // ud.setSelectedAlternative(toggleFromAlternativeFlexibus(findAlternative(
	    // ud.getAlternatives(), "6")));
	    // }
	    current.getCurrentActivity().setExecuted(true);
	}

    }

    private Alternative findAlternative(List<Alternative> alternatives,
	    String id) {
	return alternatives.stream()
		.filter(a -> a.getId() == Integer.valueOf(id)).findFirst()
		.get();
    }

    private Alternative toggleFromAlternativeFlexibus(Alternative alternative) {
	List<Leg> toRemove = new ArrayList<Leg>();
	for (Leg leg : alternative.getLegs()) {
	    if (leg.getTransportType().getType()
		    .equals(DemonstratorConstant.FLEXIBUS)) {
		toRemove.add(leg);
	    }
	}
	alternative.getLegs().removeAll(toRemove);
	return alternative;
    }

    @Subscribe
    public void onSelectionOfAlternative(SelectJourneyEvent event) {
	if (current != null) {
	    if (controller.getUserData(name) != null) {
		UserData ud = controller.getUserData(name);
		if (event.getAlternativeIndex() >= 0
			&& event.getAlternativeIndex() < ud.getAlternatives()
				.size()) {
		    Alternative sa = ud.getAlternatives().get(
			    event.getAlternativeIndex());
		    ud.setSelectedAlternative(sa);
		    controller.setUserData(name, ud);

		    // current activity is executed and selected journey
		    // alternative saved into user data
		    current.getCurrentActivity().setExecuted(true);

		    controller.addLog("Selected journey alternative with id "
			    + sa.getId());
		    logger.debug("Selected alternative from user choose completed");
		    return;
		}
	    }
	}
	logger.warn("Selected alternative actions not completed");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
	return new UserChooseAlternativeExecutable(controller);
    }
}
