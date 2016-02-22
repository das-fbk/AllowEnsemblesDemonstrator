package eu.allowensembles.controller.executables;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * DummyExecutable, do nothing, useful for testing
 */
public class DummyExecutable extends AbstractExecutableActivityInterface {

    private static final Logger logger = LogManager
	    .getLogger(DummyExecutable.class);

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	logger.debug("do nothing");
	proc.getCurrentActivity().setExecuted(true);
    }
}
