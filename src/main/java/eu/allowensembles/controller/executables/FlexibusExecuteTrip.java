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
import eu.allowensembles.presentation.main.map.viewer.MyWaypoint;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.ExecutableLeg;
import eu.allowensembles.utils.PlayRunner;
import eu.allowensembles.utils.UserData;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.impl.context.StoryboardOneContext;

/**
 * Execute selected journey alternative from {@link UserData} and display it on
 * {@link JXMapViewer} in {@link MainWindow} using
 * {@link DefaultAnimationRunner}
 * 
 * @see DefaultAnimationAdapter
 */
public class FlexibusExecuteTrip extends AbstractExecutableActivityInterface
	implements AnimationCompleteInterface {

    private static final Logger logger = LogManager
	    .getLogger(FlexibusExecuteTrip.class);

    // private static final double DISTANCE_TOLERANCE = 0.001;

    private MainController controller;
    private JXMapViewer map;

    private enum ANIMATION_STATUS {
	READY, IN_PROGRESS, COMPLETED
    }

    private ANIMATION_STATUS state = ANIMATION_STATUS.READY;

    private DefaultAnimationRunner defaultAnimationRunner = new DefaultAnimationRunner();

    public FlexibusExecuteTrip(MainController controller, JXMapViewer map) {
	this.controller = controller;
	this.map = map;
    }

    @Override
    public void execute(ProcessDiagram proc, ProcessActivity pa) {
	logger.debug("Execute " + pa.getName());
	DomainObjectInstance currentDoi = controller.getProcessEngineFacade()
		.getDomainObjectInstanceForProcess(proc);
	DomainObjectInstance doi = controller.getProcessEngineFacade()
		.getCorrelations(currentDoi).stream()
		.filter(d -> d.getId().contains("Employee")).findAny().get();
	String name = doi.getId();

	switch (state) {
	case READY:
	    // hide on map passengers now on flexibus
	    controller.hidePassengers(StoryboardOneContext.getInstance()
		    .getPassengersAtCurrentPP(currentDoi.getId()));
	    startAnimation(name, currentDoi.getId(), doi);
	    break;
	case IN_PROGRESS:
	    // do nothing
	    break;
	case COMPLETED:
	    logger.debug("Animation ended");
	    proc.getCurrentActivity().setExecuted(true);
	    GeoPosition currentPosition = controller.getPosition(doi.getId());
	    controller.movePositionOnMapForPassenger(
		    StoryboardOneContext.getInstance()
			    .getPassengersAtCurrentPP(currentDoi.getId()),
		    currentPosition);
	    break;

	default:
	    break;
	}
    }

    private void startAnimation(String employeeName, String driverName,
	    DomainObjectInstance doi) {
	UserData ud = controller.getUserData(employeeName);
	// assign route to flexibus
	if (ud.getSelectedAlternative() == null) {
	    String route = doi.getSelectedRoute();
	    if (route != null) {
		Alternative a = ud.getAlternatives().stream()
			.filter(alt -> alt.getId() == Integer.valueOf(route))
			.findFirst().get();
		ud.setSelectedAlternative(a);
	    }
	}

	// from context get current pickupoint index to reach in this route
	int i = StoryboardOneContext
		.getInstance()
		.getDriverCurrentDestinationIndex(driverName, doi.getEnsemble());

	Leg leg = ud.getSelectedAlternative().getLegs().get(i);
	animateLeg(employeeName, leg, ud.getMapIcon());
	logger.debug("Animation started");
	state = ANIMATION_STATUS.IN_PROGRESS;
    }

    private void animateLeg(String name, Leg leg, MyWaypoint waypoint) {
	// stop if active
	PlayRunner.getDefault().stop();

	List<ExecutableLeg> executableLegs = new ArrayList<ExecutableLeg>();
	List<GeoPosition> points = GoogleMapsDecoder.decode(leg.getGeometry());
	ExecutableLeg el = new ExecutableLeg();
	el.setPoints(points);
	el.setTransportType(leg.getTransportType());
	executableLegs.add(el);

	animate(waypoint, executableLegs);
    }

    private void animate(Moveable target, List<ExecutableLeg> legs) {
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
		defaultAnimationRunner.addAnimation(locationAnimation);
		startOffset += duration;
	    }
	}
	if (!defaultAnimationRunner.isRunning()) {
	    defaultAnimationRunner.start();
	}
    }

    @Override
    public void setAnimationCompleted() {
	state = ANIMATION_STATUS.COMPLETED;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
	return new FlexibusExecuteTrip(controller, map);
    }

}
