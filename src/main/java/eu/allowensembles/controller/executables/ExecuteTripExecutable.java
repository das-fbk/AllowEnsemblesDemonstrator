package eu.allowensembles.controller.executables;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import de.anormalmedia.vividswinganimations.DefaultAnimationRunner;
import de.anormalmedia.vividswinganimations.LocationAnimation;
import de.anormalmedia.vividswinganimations.Moveable;
import eu.allowensembles.controller.MainController;
import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.presentation.main.action.listener.DefaultAnimationAdapter;
import eu.allowensembles.presentation.main.map.Routes.Route.Leg;
import eu.allowensembles.presentation.main.map.util.GoogleMapsDecoder;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.ExecutableLeg;
import eu.allowensembles.utils.PlayRunner;
import eu.allowensembles.utils.UserData;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * Execute selected journey alternative from {@link UserData} and display it on
 * {@link JXMapViewer} in {@link MainWindow} using
 * {@link DefaultAnimationRunner}
 * 
 * @see DefaultAnimationAdapter
 */
public class ExecuteTripExecutable extends AbstractExecutableActivityInterface
	implements AnimationCompleteInterface {

    private static final Logger logger = LogManager
	    .getLogger(ExecuteTripExecutable.class);

    private static final double DISTANCE_TOLERANCE = 0.001;

    private MainController controller;
    private JXMapViewer map;
    private boolean animate = false;
    private boolean animationComplete = false;

    private DefaultAnimationRunner defaultAnimationRunner = new DefaultAnimationRunner();

    public ExecuteTripExecutable(MainController controller, JXMapViewer map) {
	this.controller = controller;
	this.map = map;
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	logger.debug("Execute " + pa.getName());
	String name = controller.getTypeForProcess(proc, "User");
	if (name.isEmpty()) {
	    logger.error("Impossible find type for process with pid "
		    + proc.getpid());
	    return;
	}

	if (!animate && controller.getUserData(name) != null) {
	    UserData ud = controller.getUserData(name);
	    if (ud.getSelectedAlternative() != null && ud.getMapIcon() != null
		    && ud.getMapIcon().isVisible()) {
		// stop play if active
		PlayRunner.getDefault().stop();

		Alternative sa = ud.getSelectedAlternative();

		if (sa.getLegs() == null || sa.getLegs().isEmpty()) {
		    animationComplete = true;
		}

		List<ExecutableLeg> executableLegs = new ArrayList<ExecutableLeg>();
		for (Leg leg : sa.getLegs()) {

		    List<GeoPosition> points = GoogleMapsDecoder.decode(leg
			    .getGeometry());
		    ExecutableLeg el = new ExecutableLeg();
		    // simplify list of points
		    // points = GeometryUtils.simplifyPoints(points,
		    // DISTANCE_TOLERANCE);
		    el.setPoints(points);
		    el.setTransportType(leg.getTransportType());
		    executableLegs.add(el);
		}
		logger.debug("Animation started");
		animate(ud.getMapIcon(), executableLegs, name);
		animate = true;
	    } else {
		animationComplete = true;
	    }
	}
	if (animationComplete) {
	    proc.getCurrentActivity().setExecuted(true);
	    logger.debug("Animation ended");
	}
    }

    private void animate(Moveable target, List<ExecutableLeg> legs, String name) {
	if (target == null || legs == null || legs.isEmpty() || name == null) {
	    return;
	}
	// build sequentialAnimator
	int startOffset = 0;
	int duration = 16; // 60fps, 16 for ms

	boolean last = false;
	for (ExecutableLeg el : legs) {
	    List<GeoPosition> points = el.getPoints();
	    for (int i = 0; i < points.size(); i++) {
		GeoPosition s = points.get(i);
		LocationAnimation locationAnimation = new LocationAnimation(
			target, s.getLatitude(), s.getLongitude());
		locationAnimation.setDuration(duration);
		locationAnimation.setStartOffset(startOffset);
		last = (i == points.size() - 1) ? true : false;
		locationAnimation
			.addAnimationListener(new DefaultAnimationAdapter(map,
				last, this, target, el.getTransportType()
					.getType()));
		getDefaultAnimator().addAnimation(locationAnimation);
		startOffset += duration;
	    }
	}
	if (!getDefaultAnimator().isRunning()) {
	    getDefaultAnimator().start();
	}
    }

    private DefaultAnimationRunner getDefaultAnimator() {
	return defaultAnimationRunner;
    }

    @Override
    public void setAnimationCompleted() {
	animationComplete = true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
	return new ExecuteTripExecutable(controller, map);
    }

}
