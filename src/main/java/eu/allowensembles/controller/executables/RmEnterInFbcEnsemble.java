package eu.allowensembles.controller.executables;

import eu.allowensembles.controller.MainController;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.impl.context.StoryboardOneContext;

public class RmEnterInFbcEnsemble extends AbstractExecutableActivityInterface {

    private MainController controller;

    public RmEnterInFbcEnsemble(MainController controller) {
	this.controller = controller;
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	DomainObjectInstance target = controller.getProcessEngineFacade()
		.getDomainObjectInstanceForProcess(proc);
	StoryboardOneContext.getInstance().addEnsemble(target.getId(),
		target.getEnsemble());
	controller.updateEnsembles(StoryboardOneContext.getInstance()
		.getEnsembles());
	pa.setExecuted(true);
    }

}
