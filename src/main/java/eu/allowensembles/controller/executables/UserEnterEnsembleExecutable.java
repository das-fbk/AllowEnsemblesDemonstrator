package eu.allowensembles.controller.executables;

import java.util.List;

import eu.allowensembles.controller.MainController;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.impl.context.StoryboardOneContext;

/**
 * An example how an user enter in an ensemble
 */
public class UserEnterEnsembleExecutable extends
	AbstractExecutableActivityInterface {

    private MainController controller;

    public UserEnterEnsembleExecutable(MainController controller) {
	this.controller = controller;
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	DomainObjectInstance doi = controller.getProcessEngineFacade()
		.getDomainObjectInstanceForProcess(proc);
	List<DomainObjectInstance> users = controller.getProcessEngineFacade()
		.getProcessEngine().findAllDomainObjectByType("User");
	DomainObjectInstance target = null;
	for (DomainObjectInstance u : users) {
	    if (controller.getProcessEngineFacade().isCorrelated(doi, u)) {
		target = u;
		break;
	    }

	}
	StoryboardOneContext.getInstance().addEnsemble(target.getId(),
		target.getEnsemble());
	controller.updateEnsembles(StoryboardOneContext.getInstance()
		.getEnsembles());
	pa.setExecuted(true);
    }
}
