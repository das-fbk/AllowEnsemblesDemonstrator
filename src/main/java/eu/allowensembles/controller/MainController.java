package eu.allowensembles.controller;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import eu.allowensembles.controller.events.DomainObjectInstanceSelection;
import eu.allowensembles.controller.events.StepEvent;
import eu.allowensembles.controller.executables.EmployeeEnterEnsembleExecutable;
import eu.allowensembles.controller.executables.ExecuteTripExecutable;
import eu.allowensembles.controller.executables.FlexibusExecuteTrip;
import eu.allowensembles.controller.executables.RankingExecutable;
import eu.allowensembles.controller.executables.RmEnterInFbcEnsemble;
import eu.allowensembles.controller.executables.RpEnterInFlexibusOnMap;
import eu.allowensembles.controller.executables.UserChooseAlternativeExecutable;
import eu.allowensembles.controller.executables.UserEnterEnsembleExecutable;
import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.presentation.main.action.listener.DomainObjectDefinitionSelectionByName;
import eu.allowensembles.presentation.main.events.DomainObjectInstanceSelectionByName;
import eu.allowensembles.presentation.main.events.StoryboardLoadedEvent;
import eu.allowensembles.presentation.main.map.viewer.FancyWaypointRenderer;
import eu.allowensembles.presentation.main.map.viewer.MyWaypoint;
import eu.allowensembles.privacyandsecurity.controller.PrivacyAndSecurity;
import eu.allowensembles.utility.controller.Preferences;
import eu.allowensembles.utils.DoiBean;
import eu.allowensembles.utils.PlayRunner;
import eu.allowensembles.utils.ResourceLoader;
import eu.allowensembles.utils.UserData;
import eu.allowensembles.utils.WaypointUtil;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.OnMessageActivity;
import eu.fbk.das.process.engine.api.domain.PickActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ScopeActivity;
import eu.fbk.das.process.engine.api.domain.WhileActivity;
import eu.fbk.das.process.engine.api.jaxb.EventHandlerType;
import eu.fbk.das.process.engine.api.jaxb.OnDPchangeType.DomainProperty;
import eu.fbk.das.process.engine.impl.ProcessEngineImpl;
import eu.fbk.das.process.engine.impl.context.api.Ensemble;
import eu.fbk.das.process.engine.impl.executable.DriverEnterInRouteEnsembleExecutable;
import eu.fbk.das.process.engine.impl.executable.FcAssignPassengerToRouteExecutable;
import eu.fbk.das.process.engine.impl.executable.FcRouteStarted;
import eu.fbk.das.process.engine.impl.executable.FcWaitRouteCreatedExecutable;
import eu.fbk.das.process.engine.impl.executable.FdAllPassengersOnBoardExecutable;
import eu.fbk.das.process.engine.impl.executable.FdAllPickupPointReachedExecutable;
import eu.fbk.das.process.engine.impl.executable.FdGoToNextPickupPointExecutable;
import eu.fbk.das.process.engine.impl.executable.FdRouteClosedNotice;
import eu.fbk.das.process.engine.impl.executable.FdWaitPassengersCheckOutExecutable;
import eu.fbk.das.process.engine.impl.executable.PassengerEnterInRouteEnsembleExecutable;
import eu.fbk.das.process.engine.impl.executable.RmAssignDriverExecutable;
import eu.fbk.das.process.engine.impl.executable.RmAssignPickupPointExecutable;
import eu.fbk.das.process.engine.impl.executable.RmCheckInEndedExecutable;
import eu.fbk.das.process.engine.impl.executable.RmCheckInExecuted;
import eu.fbk.das.process.engine.impl.executable.RmRouteSoldOutExecutable;
import eu.fbk.das.process.engine.impl.executable.RmUnlockWaitingPassengerExecutable;
import eu.fbk.das.process.engine.impl.executable.RouteManagerCreateRouteEnsembleExecutable;
import eu.fbk.das.process.engine.impl.executable.RpPassengerCheckedInExecutable;
import eu.fbk.das.process.engine.impl.executable.RpPassengerChekedOutExecutable;
import eu.fbk.das.process.engine.impl.executable.RpWaitFlexibusExecutable;

/**
 * Allow Ensembles Demonstrator's Main controller, use it to post/subscrive
 * events using google's EventBus
 * 
 * @see EventBus
 */
public class MainController {

    private static final Logger logger = LogManager
	    .getLogger(ProcessEngineFacade.class);

    private static EventBus eventBus;
    private MainWindow window;
    private ProcessEngineFacade processEngineFacade;

    private Map<String, UserData> userData = new HashMap<String, UserData>();

    private DoiBean current;

    private boolean displayPoints = true;

    /**
     * Construct controller for Demonstrator. Initialize {@link EventBus} with
     * {@link MainController} instance
     * 
     * @param window
     *            instance
     */
    public MainController(MainWindow window) {
	eventBus = new EventBus();
	register(this);
	this.window = window;
    }

    /**
     * Post an event on eventBus
     * 
     * @param event
     *            - a generic object that represent an event, subscribed by a
     *            method
     * @see EventBus#post(Object)
     * 
     */
    public static void post(Object event) {
	if (eventBus == null) {
	    logger.warn("EventBus is not initialized correctly, not possible to post event");
	    return;
	}
	eventBus.post(event);
    }

    /**
     * Register a subscriber to be notified by events
     * 
     * @param subscriber
     *            object
     * @see EventBus#register(Object)
     */
    public static void register(Object subscriber) {
	if (eventBus == null) {
	    logger.warn("EventBus is not initialized correctly, not possible to register subscriber");
	    return;
	}
	eventBus.register(subscriber);
    }

    @Subscribe
    public void onStoryboardLoaded(StoryboardLoadedEvent sle) {
	try {
	    // init map: on startup, hide routes
	    window.initMap(ResourceLoader.getRouteFile(), false);

	    // init scenario: load domainObjectInstances
	    processEngineFacade = new ProcessEngineFacade(ResourceLoader
		    .getScenarioFile().getParent());
	    processEngineFacade.loadScenario(ResourceLoader.getScenarioFile()
		    .getName(), this);
	    logger.debug("ProcessEngineFacade init complete");
	    window.loadDomainObjectInstancesTable(processEngineFacade
		    .getDomainObjectInstances());
	    // load user information

	    // update utilityview
	    window.getUtilityView().init();

	    // update PSview
	    window.getPSView().init();

	    // update comboboxModels
	    updateComboboxEntities();

	    // show main window
	    window.showMainScrollPane(true);

	    // register handler for executable activities
	    registerHandlersForProcessEngine();

	    addLog("Storyboard loaded: "
		    + ResourceLoader.getScenarioFile().getAbsolutePath());
	} catch (Exception e) {
	    logger.debug("Problem on loading storyboard");
	    logger.error(e.getMessage(), e);
	}
    }

    /**
     * Register handlers for executable activity. This is a bridge with actual
     * implementation of a given activity, for example per for utility, when
     * processEngine process activity with given name, registered handler will
     * be called and executed
     */
    private void registerHandlersForProcessEngine() {
	processEngineFacade
		.addExecutableHandler("UMS_SecurityAndPrivacyFiltering",
			new PrivacyAndSecurity(this));
	processEngineFacade.addExecutableHandler("UMS_UtilityRanking",
		new RankingExecutable(this));
	processEngineFacade.addExecutableHandler("USER_ChooseAlternative",
		new UserChooseAlternativeExecutable(this));
	processEngineFacade.addExecutableHandler("USER_animateTrip",
		new ExecuteTripExecutable(this, window.getMapViewer()));
	processEngineFacade.addExecutableHandler("FC_EnterInFBCensemble_User",
		new UserEnterEnsembleExecutable(this));
	processEngineFacade.addExecutableHandler(
		"FC_EnterInFBCensemble_Employee",
		new EmployeeEnterEnsembleExecutable(this));
	processEngineFacade.addExecutableHandler(
		"FD_AnimateGoToNextPickupPoint", new FlexibusExecuteTrip(this,
			window.getMapViewer()));
	processEngineFacade.addExecutableHandler("RP_EnterInFlexibusOnMap",
		new RpEnterInFlexibusOnMap(this));
	processEngineFacade.addExecutableHandler("RM_EnterInFBCensemble",
		new RmEnterInFbcEnsemble(this));

	// executable handlers for first storyboard
	processEngineFacade.addExecutableHandler(
		"RP_WaitFlexibus",
		new RpWaitFlexibusExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler("RM_CreateRouteEnsemble",
		new RouteManagerCreateRouteEnsembleExecutable(
			processEngineFacade.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"RP_EnterInRouteEnsemble",
		new PassengerEnterInRouteEnsembleExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"FD_EnterInRouteEnsemble",
		new DriverEnterInRouteEnsembleExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"FC_AssignPassengerToRoute",
		new FcAssignPassengerToRouteExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"RM_AssignDriver",
		new RmAssignDriverExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"RM_AssignPickupPoint",
		new RmAssignPickupPointExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"RM_RouteSoldOut",
		new RmRouteSoldOutExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"RM_CheckinEnded",
		new RmCheckInEndedExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"FD_GoToNextPickupPoint",
		new FdGoToNextPickupPointExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"RP_PassengerCheckedIn",
		new RpPassengerCheckedInExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"FD_AllPassengersOnBoard",
		new FdAllPassengersOnBoardExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"FD_AllPickupPointReached",
		new FdAllPickupPointReachedExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"RP_PassengerCheckedOut",
		new RpPassengerChekedOutExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"FD_WaitForPassengersCheckOut",
		new FdWaitPassengersCheckOutExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"FC_WaitRouteCreated",
		new FcWaitRouteCreatedExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler(
		"RM_UnlockWaitingPassenger",
		new RmUnlockWaitingPassengerExecutable(processEngineFacade
			.getProcessEngine()));
	processEngineFacade.addExecutableHandler("RM_CheckInExecuted",
		new RmCheckInExecuted(processEngineFacade.getProcessEngine()));

	processEngineFacade.addExecutableHandler("FD_RouteClosedNotice",
		new FdRouteClosedNotice());
	processEngineFacade.addExecutableHandler("FC_RouteStarted",
		new FcRouteStarted());
    }

    @Subscribe
    public void onDomainObjectInstanceSelection(DomainObjectInstanceSelection e) {
	DoiBean instance = findDoiBeanByName(e.getName());
	if (instance != null) {
	    current = instance;
	    updateInterface(current);
	    if (current != null) {
		try {
		    UserData ud = getUserData(current.getName());
		    window.centerMapOn(new GeoPosition(Double.valueOf(ud
			    .getMapIcon().getLocationX()), Double.valueOf(ud
			    .getMapIcon().getLocationY())));
		    if (ud.getSelectedAlternative() != null) {
			window.displayRouteOnMap(window.getMapPanel(), ud
				.getSelectedAlternative().getLegs(), Color.red);
		    }
		    if (displayPoints) {
			window.displayPoints(
				ResourceLoader.getMapInfo()
					.getBikeSharingPoints(),
				Color.green,
				getClass().getResource(
					"/images/waypoint_white_bike.png"));
			window.displayPoints(
				ResourceLoader.getMapInfo().getParkingPoints(),
				Color.decode("#7798CD"),
				getClass().getResource(
					"/images/waypoint_white_parking.png"));
		    }

		} catch (NumberFormatException nfe) {
		    logger.debug("Error in parsing for coordinates : ("
			    + current.getLat() + "," + current.getLon() + ")");
		    logger.error(nfe.getMessage(), nfe);
		}
	    }
	}
    }

    @Subscribe
    public void onDomainObjectInstanceSelection(
	    DomainObjectInstanceSelectionByName e) {
	try {
	    if (e.name != null) {
		current = findDoiBeanByName(e.name);
		updateInterface(current);
	    }
	} catch (Exception ex) {
	    logger.error(ex.getMessage(), ex);
	}
    }

    @Subscribe
    public void onDomainObjectDefinition(DomainObjectDefinitionSelectionByName e) {
	// given model name, get first instance: if there is at least one,
	// update interface with it, otherwise search process model by type an
	// populate interface with info
	DoiBean instance = null;
	for (DoiBean db : processEngineFacade.getDomainObjectInstances()) {
	    if (db.getName().startsWith(e.name)) {
		instance = db;
		break;
	    }
	}
	if (instance != null) {
	    current = instance;
	    updateInterface(current);
	    // reset all information not related directly to cell model
	    window.resetCorrelatedCells();
	    window.resetContextDetails();
	    window.resetCellDetails();
	    window.resetProcessExecution();
	    window.resetProcessModel();
	} else {
	    ProcessDiagram p = processEngineFacade.getModelByType(e.name);
	    if (p != null) {
		// display model information
		resetInterface();
		window.displayProcess(p, true, false);
		// update fragments list
		List<String> f = processEngineFacade.getFragmentNames(e.name);
		window.updateSelectedEntityProvidedFragments(f);
	    }
	}
    }

    private void resetInterface() {
	window.resetCellInstances();
	window.resetCorrelatedCells();
	window.resetCellDetails();
	window.resetContextDetails();
	window.resetProcessExecution();
	window.resetProcessModel();
    }

    /**
     * Update window interface and refresh it
     */
    public void updateInterface() {
	updateInterface(getCurrentDoiBean());
    }

    /**
     * Update window interface and refresh it
     */
    public void updateInterface(DoiBean db) {
	try {
	    displayProcessExecution(db);
	    displayProcessModel(db);
	    updateSelectedEntityDetails(db);
	    updateSelectedEntityCorrelations(db);
	    updateSelectedEntityProvidedFragments(db);
	    updateDomainObjectInstanceOnMap();
	    updateCellInstances(db);
	    updateSelectedEntityKnowledge(db);
	    updateComboboxEntities();
	    updateMonitor(db);
	    window.refreshWindow();
	} catch (Exception e) {
	    logger.error("Error in interface update", e);
	}
    }

    private void updateMonitor(DoiBean db) {
	// update monitor list using current db
	DomainObjectInstance doi = processEngineFacade
		.getDomainObjectInstanceForProcess(processEngineFacade
			.getProcessDiagram(db));
	if (doi != null) {
	    ProcessDiagram process = doi.getProcess();
	    List<String> monitors = getMonitor(process);
	    window.updateMonitors(monitors);
	}
    }

    private List<String> getMonitor(ProcessDiagram process) {
	List<String> result = new ArrayList<String>();
	if (process == null) {
	    return result;
	}
	for (ProcessActivity act : process.getActivities()) {
	    if (act.isWhile()) {
		result.addAll(getMonitor(((WhileActivity) act)
			.getDefaultBranch()));
	    } else if (act.isScope()) {
		ScopeActivity scope = (ScopeActivity) act;
		for (EventHandlerType eh : scope.getEventHandler()) {
		    result.add(getEventHandlerAsString(eh));
		}
		result.addAll(getMonitor(((ScopeActivity) act).getBranch()));
	    } else if (act.isPick()) {
		PickActivity pick = (PickActivity) act;
		for (OnMessageActivity msg : pick.getOnMessages()) {
		    result.addAll(getMonitor(msg.getBranch()));
		}
	    }
	}
	return result;
    }

    private String getEventHandlerAsString(EventHandlerType eh) {
	StringBuilder sb = new StringBuilder();
	// conditions
	sb.append("on(");
	if (eh.getOnDPchange() != null) {
	    sb.append(eh.getDpChange().getDpName()).append(" on ")
		    .append(eh.getDpChange().getEventName());
	}
	if (eh.getOnExternalEvent() != null) {
	    sb.append(eh.getOnExternalEvent().getOnEventName());
	}
	sb.append(") => ");
	// actions
	if (eh.getDpChange() != null) {
	    for (DomainProperty dp : eh.getOnDPchange().getDomainProperty()) {
		sb.append(dp.getDpName() + " = " + dp.getState().toString());
	    }
	}
	if (eh.getTriggerEvent() != null) {
	    sb.append(eh.getTriggerEvent().getName());
	}

	return sb.toString();
    }

    private void updateComboboxEntities() {
	List<String> response = new ArrayList<String>();
	for (DomainObjectDefinition dod : processEngineFacade
		.getDomainObjectDefinitions()) {
	    if (!response.contains(dod.getDomainObject().getName())) {
		response.add(dod.getDomainObject().getName());
	    }
	}
	window.updateComboxEntities(response);

    }

    private void updateSelectedEntityKnowledge(DoiBean db) {
	if (db == null) {
	    return;
	}
	DomainObjectInstance doi = processEngineFacade
		.getDomainObjectInstanceForProcess(processEngineFacade
			.getProcessDiagram(db));
	if (doi != null) {
	    List<String> response = new ArrayList<String>();
	    response = getKnowledgeValues(response, doi.getInternalKnowledge());
	    response = getKnowledgeValues(response, doi.getExternalKnowledge());
	    window.updateEntityKnowledge(response);
	}
    }

    private List<String> getKnowledgeValues(List<String> response,
	    List<ObjectDiagram> internal) {
	String v = "";
	if (internal != null) {
	    for (ObjectDiagram in : internal) {
		v = in.getOid() + " = " + in.getCurrentState();
		if (!response.contains(v)) {
		    response.add(v);
		}
	    }
	}
	return response;
    }

    private void updateCellInstances(DoiBean db2) {
	if (db2 == null) {
	    return;
	}
	List<String> toDisplay = new ArrayList<String>();
	List<DoiBean> instances = getProcessEngineFacade()
		.getDomainObjectInstances();
	if (instances == null) {
	    logger.warn("domainObjectInstances null");
	    return;
	}
	// put ino cellInstances list a list of all cell instances of same type
	// of current
	String type = getCurrentType(processEngineFacade.getProcessDiagram(db2));
	if (type.isEmpty()) {
	    window.updateCellInstances(toDisplay);
	    logger.debug("type of current domainObjectInstance not found");
	    return;
	}
	// return all instances of same type (f.e. all UMS instances using
	// current type
	List<DoiBean> result = new ArrayList<DoiBean>();
	for (DoiBean db : instances) {
	    ProcessDiagram p = processEngineFacade.getProcessDiagram(db);
	    if (db.getName().startsWith(type) && p != null && !p.isEnded()) {
		result.add(db);
	    }
	}
	for (DoiBean db : result) {
	    toDisplay.add(db.getName());
	}

	window.updateCellInstances(toDisplay);
    }

    /**
     * @param processDiagram
     * @return type of current process or an empty string if not found
     * @see ProcessEngineImpl#buildRelevantServices(ProcessDiagram) for info how
     *      this Id is generated
     */
    private String getCurrentType(ProcessDiagram process) {
	if (process != null) {
	    DomainObjectInstance currentDoi = processEngineFacade
		    .getDomainObjectInstanceForProcess(process);
	    String name = "";
	    if (currentDoi == null) {
		name = process.getName();
	    } else {
		if (currentDoi.getId() != null) {
		    name = currentDoi.getId();
		}
	    }
	    int separatorIndex = name
		    .indexOf(DomainObjectManagerInterface.ID_SEPARATOR);
	    if (separatorIndex != -1) {
		return name.substring(0, separatorIndex);
	    }

	    return name;
	}
	return "";
    }

    private void updateDomainObjectInstanceOnMap() {
	List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
	// for (DoiBean db : processEngineFacade.getDomainObjectInstances()) {

	Set<MyWaypoint> points = new HashSet<MyWaypoint>();
	for (String userName : userData.keySet()) {
	    UserData ud = userData.get(userName);
	    try {

		if (ud != null && ud.getMapIcon() != null
			&& ud.getMapIcon().isVisible()) {
		    points.add(ud.getMapIcon());
		}
	    } catch (NumberFormatException e) {
		logger.error(e.getMessage(), e);
	    }

	}
	WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
	waypointPainter.setWaypoints(points);
	waypointPainter.setRenderer(new FancyWaypointRenderer());
	painters.add(waypointPainter);
	//
	// CompoundPainter<JXMapViewer> painter = new
	// CompoundPainter<JXMapViewer>(
	// painters);
	if (window.getMapViewer() != null
		&& window.getMapViewer().getOverlayPainter() == null) {
	    window.getMapViewer().setOverlayPainter(
		    new CompoundPainter<JXMapViewer>());
	}
	CompoundPainter<JXMapViewer> cpainter = ((CompoundPainter<JXMapViewer>) window
		.getMapViewer().getOverlayPainter());
	cpainter.setPainters(painters);

	if (displayPoints) {
	    window.displayPoints(ResourceLoader.getMapInfo()
		    .getBikeSharingPoints(), Color.green, getClass()
		    .getResource("/images/waypoint_white_bike.png"));
	    window.displayPoints(
		    ResourceLoader.getMapInfo().getParkingPoints(), Color.blue,
		    getClass()
			    .getResource("/images/waypoint_white_parking.png"));
	}

	window.getMapViewer().repaint();

    }

    private void displayProcessModel(DoiBean db) {
	if (db == null) {
	    return;
	}
	ProcessDiagram model = processEngineFacade.getModel(db.getName());
	window.displayProcess(model, true, false);
    }

    private void displayProcessExecution(DoiBean db) {
	if (db == null) {
	    return;
	}
	logger.debug("Display process execution model for db: " + db.getName());
	ProcessDiagram pd = processEngineFacade.getProcessDiagram(db);
	window.displayProcess(pd, false, true);
    }

    @Subscribe
    public void onStep(StepEvent se) {
	try {
	    // one step for process engine
	    processEngineFacade.step();
	    addLog("ProcessEngine step completed");
	    // update domainObjectInstances list
	    window.updateDomainObjectInstancesTable(processEngineFacade
		    .getDomainObjectInstances());
	    // update current selected domainObjectInstance
	    if (getCurrentDoiBean() == null) {
		window.selectFirstEntityInTable();
		current = getCurrentDoiBean();
	    } else if (getCurrentDoiBean().getId() == null
		    && window.getSelectedEntityInTable().isEmpty()
		    && !window.getSelectedCorrelatedEntity().isEmpty()) {
		window.selectFirstEntityInTable();
		current = getCurrentDoiBean();
	    } else if (!window.getSelectedCorrelatedEntity().isEmpty()) {
		updateInterface(findDoiBeanByName(window
			.getSelectedCorrelatedEntity()));
		return;
	    }
	    // update selected entitydetails
	    updateInterface(getCurrentDoiBean());
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
    }

    private void updateSelectedEntityProvidedFragments(DoiBean db) {
	if (db == null) {
	    return;
	}
	List<String> response = processEngineFacade.getFragmentsFromDoiBean(db
		.getName());
	window.updateSelectedEntityProvidedFragments(response);
    }

    private void updateSelectedEntityDetails(DoiBean db) {
	if (db == null) {
	    return;
	}
	List<String> toDisplay = new ArrayList<String>();
	ProcessDiagram process = processEngineFacade.getModel(db.getName());
	if (process != null) {
	    String doiName = processEngineFacade.fromProcessModelToDoi(process);
	    toDisplay.add("Name :" + doiName);
	    toDisplay.add("Process name: " + process.getName());

	    window.updateSelectedEntityDetails(toDisplay);
	}
    }

    /**
     * Update in window correlation of current process identified by index
     * 
     * Correlated DomainObjectInstances are :</br> - refinement process
     * correlated instances, for example User -> refinement1 -> UMS</br> - and
     * vice versa, so UMS -> refinement 1 -> User
     * 
     * @param db
     */
    private void updateSelectedEntityCorrelations(DoiBean db) {
	if (db == null) {
	    return;
	}
	List<String> response = new ArrayList<String>();
	ProcessDiagram process = processEngineFacade.getProcessDiagram(db);
	if (process != null) {
	    // using correlated process, get origin of this correlation. So for
	    // example if User -> refinement1 -> UMS, origin for UMS is User
	    DomainObjectInstance doi = processEngineFacade
		    .getDomainObjectInstanceForProcess(process);
	    List<DomainObjectInstance> corrs = processEngineFacade
		    .getCorrelations(doi);
	    if (corrs != null) {
		for (DomainObjectInstance corr : corrs) {
		    if (corr != null && !response.contains(corr.getId())
			    && !doi.getId().equals(corr.getId())) {
			response.add(corr.getId());
		    }
		}
	    }
	    // using current process, using refinements information, get in
	    // correlation processes, so for example if User-> refinement 1 ->
	    // Ums, result is Ums for user, in recursive way
	    window.updateSelectedEntityCorrelations(response);
	}
    }

    /**
     * @return instance of {@link ProcessEngineFacade}
     */
    public ProcessEngineFacade getProcessEngineFacade() {
	return processEngineFacade;
    }

    /**
     * @return process currently selected and executed, null if not found. Be
     *         aware: this is process get directly from processEngine
     */
    public ProcessDiagram getCurrentProcess() {
	return processEngineFacade.getProcessDiagram(getCurrentDoiBean());
    }

    /**
     * Using currently selected domain object, select into interface
     */
    public void selectDomainObject() {
	updateInterface(getCurrentDoiBean());
    }

    public void setUserData(String name, UserData data) {
	userData.put(name, data);
    }

    /**
     * @return {@link UserData} for given userName
     */
    public UserData getUserData(String name) {
	if (userData.get(name) == null) {
	    userData.put(name, new UserData());
	}
	return userData.get(name);
    }

    /**
     * Returns a process diagram refined up to the point of current execution
     * 
     * @return requested process diagram or void process diagram (no activities)
     *         if there is no current process
     */
    public ProcessDiagram getCurrentRefinedProcessDiagram() {
	List<ProcessActivity> activityList = new ArrayList<ProcessActivity>();
	ProcessDiagram pd = getCurrentProcess();
	if (pd == null) {
	    return new ProcessDiagram();
	}
	activityList.addAll(pd.getActivities());
	ProcessActivity currentActivity = pd.getCurrentActivity();
	while (processEngineFacade.getRefinement(pd) != null) {
	    pd = processEngineFacade.getRefinement(pd);
	    int index = activityList.indexOf(currentActivity);
	    activityList.addAll(index, pd.getActivities());
	    activityList.remove(index + pd.getActivities().size());
	    currentActivity = pd.getCurrentActivity();
	}
	// Create process from a list of activities
	ProcessDiagram retPd = new ProcessDiagram(activityList);
	return retPd;
    }

    /**
     * Returns the activity currently being executed at the bottom of the
     * refinement tree.
     * 
     * @return
     */
    public ProcessActivity getCurrentlyExecutingRefinedActivity() {
	ProcessDiagram pd = getCurrentProcess();
	while (processEngineFacade.getRefinement(pd) != null) {
	    pd = processEngineFacade.getRefinement(pd);
	}
	return pd.getCurrentActivity();
    }

    /**
     * Display journey alternative in window for given username
     * 
     * @param userName
     */
    public void displayAlternativesFor(String userName) {
	if (userName == null) {
	    logger.error("Not possible to show alternatives for a null username");
	    return;
	}
	UserData ud = userData.get(userName);
	if (ud == null) {
	    logger.warn("Not found any data for name " + userName);
	    return;
	}
	window.displayAlternativesWindow(ud);
    }

    /**
     * @return selected DoiBean using interface information
     */
    public DoiBean getCurrentDoiBean() {
	String id = window.getSelectedEntityInTable();
	if (id != null && !id.isEmpty()) {
	    for (DoiBean d : processEngineFacade.getDomainObjectInstances()) {
		if (d.getId().equals(id)) {
		    return d;
		}
	    }
	}
	return current;
    }

    private DoiBean findDoiBeanById(String id) {
	for (DoiBean d : processEngineFacade.getDomainObjectInstances()) {
	    if (d.getId().equals(id)) {
		return d;
	    }
	}
	return null;
    }

    public DoiBean findDoiBeanByName(String name) {
	for (DoiBean d : processEngineFacade.getDomainObjectInstances()) {
	    if (d.getName().equals(name)) {
		return d;
	    }
	}
	return null;
    }

    /**
     * Add a line of log into window
     * 
     * @param line
     */
    public void addLog(String line) {
	window.addLog(line);
    }

    public void play() {
	try {
	    if (!PlayRunner.getDefault().isRunning()) {
		PlayRunner.getDefault().stop();
		PlayRunner.getDefault().setController(this);
		PlayRunner.getDefault().start();
	    } else {
		PlayRunner.getDefault().stop();
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
    }

    /**
     * Build a new {@link DomainObjectInstance} for username and set
     * userPreference
     * 
     * @param userName
     * @param userPreference
     */
    public void buildUserDoiBean(String userName, Preferences userPreference) {
	DomainObjectInstance doi = processEngineFacade.buildUserDoi(userName);
	// build userdata
	UserData ud = new UserData();
	ud.setName(userName);
	ud.setPreferences(userPreference);
	// read coordinate from coordinates.properties, and remove it after use
	// (so ther is a fine number of coordinate for new entities)
	Properties coords = ResourceLoader.loadCoordinates();
	String key = (String) coords.keySet().stream().sorted().findFirst()
		.get();
	String value = (String) coords.get(key);
	StringTokenizer stk = new StringTokenizer(value, ",");
	String lat = stk.nextToken();
	String lng = stk.nextToken();
	GeoPosition position = new GeoPosition(Double.valueOf(lat),
		Double.valueOf(lng));
	URL resource = getClass().getResource("/images/waypoint_white.png");
	if (userName.contains("User")) {
	    resource = getClass()
		    .getResource("/images/waypoint_white_walk.png");
	} else if (userName.contains("Employee")) {
	    resource = getClass().getResource(
		    "/images/waypoint_white_flexibus.png");
	}
	ud.setMapIcon(WaypointUtil.buildMapIcon(userName, Color.red, position,
		resource));
	coords.remove(key);
	// set user data and update according
	doi.setLat(lat);
	doi.setLon(lng);
	setUserData(doi.getId(), ud);
	processEngineFacade.updateUserData(ud, doi.getId());
	window.updateDomainObjectInstancesTable(getProcessEngineFacade()
		.getDomainObjectInstances());

	updateInterface();
    }

    public void selectPreviousEntity() {
	if (current == null) {
	    window.selectFirstEntityInTable();
	    current = getCurrentDoiBean();
	    return;
	}
	// search current then select previous
	DoiBean previous = null;
	for (DoiBean d : processEngineFacade.getDomainObjectInstances()) {
	    if (d.getId().equals(current.getId())) {
		break;
	    }
	    previous = d;
	}
	if (previous == null) {
	    window.selectFirstEntityInTable();
	    current = getCurrentDoiBean();
	    return;
	}
	current = previous;
	window.selectEntityOnTable(previous.getId());
	updateInterface(current);
    }

    public void selectNextEntity() {
	if (current == null) {
	    window.selectFirstEntityInTable();
	    current = getCurrentDoiBean();
	    return;
	}
	// search current then select next
	DoiBean next = null;
	for (int i = 0; i < processEngineFacade.getDomainObjectInstances()
		.size(); i++) {
	    DoiBean d = processEngineFacade.getDomainObjectInstances().get(i);
	    if (d.getId().equals(current.getId())) {
		if (i + 1 < processEngineFacade.getDomainObjectInstances()
			.size()) {
		    next = processEngineFacade.getDomainObjectInstances().get(
			    i + 1);
		    break;
		}
	    }

	}
	if (next != null) {
	    current = next;
	    updateInterface(current);
	    window.selectEntityOnTable(next.getId());
	}

    }

    /**
     * Return current user name. <br>
     * From current displayed process, search into {@link DomainObjectInstance}
     * if is not of user Type, then search using correlation and refinements
     * 
     * @return current user
     */
    public String getCurrentUser() {
	if (getCurrentDoiBean() == null) {
	    return null;
	}
	if (getCurrentDoiBean().getModel() != null) {
	    DomainObjectInstance doi = processEngineFacade
		    .getDomainObjectInstanceForProcess(getCurrentDoiBean()
			    .getModel());
	    if (doi != null) {
		if (doi.getType().startsWith("User")) {
		    return doi.getId();
		} else {
		    // search into correlated
		    List<DomainObjectInstance> corr = processEngineFacade
			    .getCorrelations(doi);
		    for (DomainObjectInstance c : corr) {
			if (c != null) {
			    if (c.getType().startsWith("User")) {
				return c.getId();
			    }
			}
		    }
		}
	    }
	}
	return current.getName();
    }

    /**
     * @param proc
     *            - {@link ProcessDiagram} to analyze
     * @param type
     *            - type of domainObjectInstance to find
     * @return correlated instance type from a process. Note: all process in
     *         demonstrator are related to at least one type (user,flexibus,
     *         etc..). Return empty string if error or not found
     */
    public String getTypeForProcess(ProcessDiagram proc, String type) {
	if (proc == null) {
	    logger.warn("Impossible to find user for null process");
	    return "";
	}
	DomainObjectInstance doi = processEngineFacade
		.getDomainObjectInstanceForProcess(proc);
	if (doi == null) {
	    return "";
	}
	if (doi.getType().startsWith(type)) {
	    return doi.getId();
	} else {
	    // search into correlated
	    List<DomainObjectInstance> corr = processEngineFacade
		    .getCorrelations(doi);
	    for (DomainObjectInstance c : corr) {
		if (c != null) {
		    if (c.getType().startsWith(type)) {
			return c.getId();
		    }
		}
	    }

	}

	return "";
    }

    public void updateEnsembles(List<Ensemble> list) {
	window.updateEnsembles(list);
    }

    /**
     * @return list of current {@link MyWaypoint} current on map
     */
    public List<MyWaypoint> getWayPoints() {
	List<MyWaypoint> points = new ArrayList<MyWaypoint>();
	for (DoiBean db : processEngineFacade.getDomainObjectInstances()) {
	    try {
		UserData ud = userData.get(db.getName());
		if (ud != null && ud.getMapIcon() != null
			&& ud.getMapIcon().isVisible()) {
		    points.add(ud.getMapIcon());
		}
	    } catch (NumberFormatException e) {
		logger.error(e.getMessage(), e);
	    }
	}
	return points;
    }

    public void hidePassenger(String id) {
	for (String userName : userData.keySet()) {
	    if (userName != null && userName.equals(id)) {
		userData.get(userName).getMapIcon().setVisible(false);
	    }
	}
	updateDomainObjectInstanceOnMap();
    }

    public void hidePassengers(List<String> names) {
	for (String userName : names) {
	    if (userName != null && userName.startsWith("User")) {
		userData.get(userName).getMapIcon().setVisible(false);
	    }
	}

    }

    public void movePositionOnMapForPassenger(List<String> passengers,
	    GeoPosition to) {
	for (String p : passengers) {
	    userData.get(p).getMapIcon().setPosition(to);
	}

    }

    public GeoPosition getPosition(String userName) {
	MyWaypoint waypoint = userData.get(userName).getMapIcon();
	return waypoint.getPosition();

    }
}
