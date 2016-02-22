package eu.allowensembles.controller.executables;

import java.util.List;

import eu.allowensembles.controller.MainController;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.impl.context.StoryboardOneContext;

public class EmployeeEnterEnsembleExecutable extends
	AbstractExecutableActivityInterface {

    private MainController controller;

    public EmployeeEnterEnsembleExecutable(MainController controller) {
	this.controller = controller;
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	DomainObjectInstance doi = controller.getProcessEngineFacade()
		.getDomainObjectInstanceForProcess(proc);
	List<DomainObjectInstance> employees = controller
		.getProcessEngineFacade().getProcessEngine()
		.findAllDomainObjectByType("FlexibusEmployee");
	DomainObjectInstance target = null;
	for (DomainObjectInstance u : employees) {
	    if (controller.getProcessEngineFacade().isCorrelated(doi, u)) {
		target = u;
		break;
	    }

	}
	// Assign driver to ensemble defined in storyboard xml
	StoryboardOneContext.getInstance().addEnsemble(target.getId(),
		target.getEnsemble());
	controller.updateEnsembles(StoryboardOneContext.getInstance()
		.getEnsembles());
	pa.setExecuted(true);
    }

}
