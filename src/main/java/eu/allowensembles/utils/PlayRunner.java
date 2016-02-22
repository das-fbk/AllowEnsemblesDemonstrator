package eu.allowensembles.utils;

import eu.allowensembles.DemonstratorConstant;
import eu.allowensembles.controller.MainController;
import eu.allowensembles.controller.events.StepEvent;

public class PlayRunner implements Runnable {

    private static final PlayRunner DEFAULT_THREAD = new PlayRunner();

    protected Thread thread = null;

    private MainController controller;

    public static PlayRunner getDefault() {
	return DEFAULT_THREAD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
	while (true) {
	    if (controller == null) {
		stop();
		break;
	    }
	    MainController.post(new StepEvent());
	    try {
		Thread.sleep(DemonstratorConstant.getStepTime());
	    } catch (InterruptedException e) {
		return;
	    }
	}
    }

    public boolean isRunning() {
	return thread != null && thread.isAlive();
    }

    public void start() {
	stop();
	thread = new Thread(this);
	thread.setDaemon(true);
	thread.start();
    }

    public void stop() {
	if (thread != null && thread.isAlive()) {
	    thread.interrupt();
	}
	thread = null;
    }

    public void setController(MainController controller) {
	this.controller = controller;
    }

}