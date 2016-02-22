package eu.allowensembles.controller.executables;

import eu.allowensembles.controller.MainController;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

public class RpEnterInFlexibusOnMap extends AbstractExecutableActivityInterface {

    private MainController controller;

    public RpEnterInFlexibusOnMap(MainController controller) {
	this.controller = controller;
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	// because passenger is entered in the flexibus, hide icon on map
	DomainObjectInstance currentDoi = controller.getProcessEngineFacade()
		.getDomainObjectInstanceForProcess(proc);
	DomainObjectInstance doi = controller.getProcessEngineFacade()
		.getCorrelations(currentDoi).stream()
		.filter(d -> d.getId().contains("User")).findAny().get();

	controller.hidePassenger(doi.getId());
	pa.setExecuted(true);

    }

}
