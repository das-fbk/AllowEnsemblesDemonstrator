package eu.allowensembles.controller;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.viewer.GeoPosition;

import eu.allowensembles.presentation.main.map.Routes;
import eu.allowensembles.presentation.main.map.Routes.Route;
import eu.allowensembles.presentation.main.map.Routes.Route.Leg;
import eu.allowensembles.utility.controller.Preferences;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.DoiBean;
import eu.allowensembles.utils.ParserUtil;
import eu.allowensembles.utils.ResourceLoader;
import eu.allowensembles.utils.UserData;
import eu.allowensembles.utils.WaypointUtil;
import eu.fbk.das.adaptation.AdaptationManager;
import eu.fbk.das.adaptation.api.AdaptationManagerInterface;
import eu.fbk.das.composer.impl.Composer;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ServiceDiagram;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.Fragment;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance.DomainObjectInstanceKnowledge.Dp;
import eu.fbk.das.process.engine.api.util.DomainObjectInstanceWithVariable;
import eu.fbk.das.process.engine.impl.DomainObjectManager;
import eu.fbk.das.process.engine.impl.ProcessEngineImpl;
import eu.fbk.das.process.engine.impl.util.ScenarioLoader;

/**
 * A facade for Process Engine integration
 */
public class ProcessEngineFacade {

    private static final Logger logger = LogManager
	    .getLogger(ProcessEngineFacade.class);

    private DomainObjectManagerInterface manager;
    private AdaptationManagerInterface adaptationManager;
    private ProcessEngine processEngine;
    private String workingDir;
    private Map<String, ProcessDiagram> doiNameToProcessModel = new HashMap<String, ProcessDiagram>();

    // temporary map current processes's activity names
    private Map<String, ProcessActivity> activityName = new HashMap<String, ProcessActivity>();

    private Map<String, ProcessDiagram> refinements = new HashMap<String, ProcessDiagram>();

    private List<DomainObjectInstanceWithVariable> laterExecutionDoiList = new ArrayList<DomainObjectInstanceWithVariable>();

    /**
     * Create process Engine using
     * 
     * @param dir
     *            - main directory for storyboard (must contain a scenario file)
     */
    public ProcessEngineFacade(String dir) {
	this.workingDir = dir;
	manager = new DomainObjectManager(Arrays.asList(dir));
	adaptationManager = new AdaptationManager(new Composer(dir));
	processEngine = new ProcessEngineImpl(manager, adaptationManager, dir);
	adaptationManager.setProcessEngine(processEngine);
    }

    public void loadScenario(String scenarioFileName, MainController controller) {
	logger.debug("Load scenario: " + scenarioFileName);
	// load
	ScenarioLoader loader = new ScenarioLoader(workingDir);
	Scenario scenario = loader.load(scenarioFileName);
	for (DomainObject domainObject : scenario.getDomainObject()) {
	    DomainObjectDefinition ed;
	    try {
		ed = manager.add(manager.load(domainObject.getFile()));
		for (eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d : domainObject
			.getDomainObjectInstance()) {
		    DomainObjectInstance doi = manager.buildInstance(ed, d,
			    Arrays.asList(workingDir));
		    if (d.getDomainObjectInstanceKnowledge() != null) {
			for (Dp knownledge : d
				.getDomainObjectInstanceKnowledge().getDp()) {
			    doi.updateKnowledge(knownledge);
			}
		    }
		    if (d.getPreferenceFile() != null) {
			Preferences pref = ParserUtil.parse(workingDir,
				d.getPreferenceFile());
			if (pref != null) {
			    controller.getUserData(doi.getId()).setPreferences(
				    pref);
			}
		    }
		    if (d.getEnsemble() != null) {
			doi.setEnsemble(d.getEnsemble());
		    }
		    if (d.getCorrelations() != null) {
			doi.setCorrelations(d.getCorrelations());
		    }
		    if (d.getSelectedRoute() != null) {
			doi.setSelectedRoute(d.getSelectedRoute());
		    }
		    if (d.getOnTurn() != null && d.getOnTurn() > 0) {
			DomainObjectInstanceWithVariable doiwv = new DomainObjectInstanceWithVariable();
			doiwv.setDoi(doi);
			doiwv.setDoiFromScenario(d);
			laterExecutionDoiList.add(doiwv);
		    } else {
			int pid = processEngine.start(doi);
			processEngine.assignVariableFromIstance(d, pid);

		    }

		    // load user data inside controller
		    UserData ud = updateUserData(
			    controller.getUserData(doi.getId()), doi.getId());
		    // build poi for map
		    if (ud.getMapIcon() == null && d != null
			    && d.getLat() != null && d.getLong() != null) {
			GeoPosition position = new GeoPosition(Double.valueOf(d
				.getLat()), Double.valueOf(d.getLong()));
			URL resource = getClass().getResource(
				"/images/waypoint_white.png");
			if (ud.getName().contains("User")) {
			    resource = getClass().getResource(
				    "/images/waypoint_white_walk.png");
			} else if (ud.getName().contains("Employee")) {
			    resource = getClass().getResource(
				    "/images/waypoint_white_flexibus.png");
			}
			ud.setMapIcon(WaypointUtil.buildMapIcon(d.getId(),
				Color.red, position, resource));
		    }

		    controller.setUserData(doi.getId(), ud);
		}
	    } catch (ProcessEngineRuntimeException | NullPointerException
		    | InvalidObjectCurrentStateException
		    | InvalidFlowInitialStateException
		    | InvalidFlowActivityException
		    | FlowDuplicateActivityException e) {
		logger.error(e.getMessage(), e);
	    }
	}
    }

    /**
     * Update {@link UserData} reading journey alternatives from file using
     * {@link ResourceLoader#getRouteFile()}
     * 
     * @param data
     * @param name
     * @return updated data
     */
    public UserData updateUserData(UserData data, String name) {
	try {
	    File routesFile = ResourceLoader.getRouteFile();
	    JAXBContext context = JAXBContext.newInstance(Routes.class);
	    Routes r = new Routes();
	    r = (Routes) context.createUnmarshaller().unmarshal(routesFile);

	    ArrayList<Alternative> alt = new ArrayList<Alternative>();
	    for (int i = 0; i < r.getRoute().size(); i++) {
		double walkingDistance = 0;
		long travelTime = 0;
		double cost = 0;
		String modes = "";
		Route route = r.getRoute().get(i);
		if (route.isFlexibusRoute() == null
			|| (route.isFlexibusRoute() != null
				&& route.isFlexibusRoute() && name
				    .contains("Employee"))) {
		    List<Leg> leg = route.getLeg();
		    for (int j = 0; j < leg.size(); j++) {
			walkingDistance += leg.get(j).getWalkingDistance();
			travelTime += leg.get(j).getDuration();
			cost += leg.get(j).getCost();
			modes += leg.get(j).getTransportType().getType() + " ";

		    }
		    alt.add(new Alternative(Integer.valueOf(r.getRoute().get(i)
			    .getId()), r.getRoute().get(i).getNoOfChanges(),
			    walkingDistance, travelTime, cost, modes, leg, 0));
		}
	    }

	    data.setAlternatives(alt);
	    data.setName(name);

	    return data;
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
	return null;
    }

    /**
     * Force {@link ProcessEngine} to make a {@link ProcessEngine#stepAll()},
     * updating all processes
     */
    public void step() {
	// if there is any domainobjectinstance for later execution, update it,
	// if counter is zero, start it
	if (laterExecutionDoiList != null && !laterExecutionDoiList.isEmpty()) {
	    updateLaterExecutionDoiList();
	}
	processEngine.stepAll();
    }

    private void updateLaterExecutionDoiList() {
	Iterator<DomainObjectInstanceWithVariable> iter = laterExecutionDoiList
		.iterator();
	while (iter.hasNext()) {
	    DomainObjectInstanceWithVariable doi = iter.next();
	    doi.getDoi().setOnTurn(doi.getDoi().getOnTurn() - 1);
	    if (doi.getDoi().getOnTurn() <= 0) {
		try {
		    int pid = processEngine.start(doi.getDoi());
		    if (doi.getDoiFromScenario() != null) {
			processEngine.assignVariableFromIstance(
				doi.getDoiFromScenario(), pid);
		    }
		    iter.remove();
		} catch (ProcessEngineRuntimeException
			| InvalidFlowInitialStateException
			| InvalidFlowActivityException
			| FlowDuplicateActivityException e) {
		}
	    }
	}
    }

    /**
     * @return an updated list of {@link DoiBean} from current
     *         {@link DomainObjectInstance} list inside
     *         {@link DomainObjectManager}
     */
    public List<DoiBean> getDomainObjectInstances() {
	List<DoiBean> doiBeans = new ArrayList<DoiBean>();
	for (DomainObjectInstance doi : manager.getInstances()) {
	    DoiBean db = new DoiBean();
	    db.setModel(doi.getProcess());
	    db.setId("" + doi.getProcess().getpid());
	    db.setName(doi.getId());
	    db.setStatus(getStatus(doi.getProcess()));
	    db.setLat(doi.getLat());
	    db.setLon(doi.getLon());
	    doiBeans.add(db);
	}
	return doiBeans;
    }

    /**
     * @param process
     * @return status of given process, displayed on demonstrator gui
     */
    private String getStatus(ProcessDiagram process) {
	if (!process.isRunning() && !process.isTerminated()
		&& !process.isEnded()) {
	    return "waiting";
	}
	if (process.isRunning() && process.isTerminated() && process.isEnded()
		&& getRefinement(process) == null) {
	    return "ended";
	}
	if (process.isRunning() && process.isTerminated() && process.isEnded()) {
	    return "waiting";
	}
	if (!process.isRunning() && getRefinement(process) != null) {
	    return "waiting";
	}
	if (!process.isRunning() && process.isTerminated()) {
	    return "terminated";
	}
	if (!process.isRunning() && process.isEnded()) {
	    return "endend";
	}
	if (process.isRunning()) {
	    return "running";
	}
	return "";
    }

    /**
     * @param db
     * @return {@link ProcessDiagram} from {@link DoiBean} using
     *         {@link DomainObjectInstance}
     */
    public ProcessDiagram getProcessDiagram(DoiBean db) {
	if (db == null) {
	    return null;
	}
	DomainObjectInstance doi = manager.findInstanceById(db.getName());
	if (doi != null) {
	    return doi.getProcess();
	}
	return null;
    }

    /**
     * @param pid
     *            - unique process id
     * @return {@link ProcessDiagram} for given process pid
     */
    public ProcessDiagram getProcessDiagram(Integer pid) {
	DomainObjectInstance doi = manager.findInstance(pid);
	if (doi != null && doi.getProcess() != null) {
	    return doi.getProcess();
	}
	if (processEngine.isRefinement(pid)) {
	    return processEngine.getRefinement(pid);
	}
	if (processEngine.getRefinement(pid) != null) {
	    return processEngine.getRefinement(pid);
	}
	return null;
    }

    /**
     * @param pd
     * @return true if passed {@link ProcessDiagram} have refinements
     */
    public boolean hasRefinements(ProcessDiagram pd) {
	return processEngine.hasRefinements(pd.getpid());
    }

    /**
     * @param pd
     * @return refinement for {@link ProcessDiagram}
     */
    public ProcessDiagram getRefinement(ProcessDiagram pd) {
	return processEngine.getRefinement(pd.getpid());
    }

    /**
     * @param name
     * @return {@link ProcessDiagram} model for {@link DomainObjectInstance}
     *         with given name
     */
    public ProcessDiagram getModel(String name) {
	ProcessDiagram pd = null;

	DomainObjectInstance doi = manager.findInstanceById(name);
	if (doi != null && doiNameToProcessModel.containsKey(doi.getId())) {
	    pd = doiNameToProcessModel.get(doi.getId());
	} else {
	    ProcessDiagram model = new ProcessDiagram();
	    model.setName(doi.getProcess().getName());
	    List<ProcessActivity> activities = new ArrayList<ProcessActivity>();
	    for (ProcessActivity act : doi.getProcess().getActivities()) {
		ProcessActivity a = new ProcessActivity(act.getName());
		a.setType(act.getType());
		a.setAbstract(act.isAbstract());
		a.setConcrete(act.isConcrete());
		a.setId(act.getId());
		activities.add(a);
	    }

	    model.setActivities(activities);
	    model.setPid(doi.getProcess().getpid());
	    doiNameToProcessModel.put(doi.getId(), model);
	    pd = model;
	}

	return pd;
    }

    /**
     * @param type
     * @return {@link ProcessDiagram} model for given type
     */
    public ProcessDiagram getModelByType(String type) {
	ProcessDiagram pd = null;
	DomainObjectInstance doi = manager.findInstanceByType(type);
	if (doi != null && doiNameToProcessModel.containsKey(doi.getId())) {
	    pd = doiNameToProcessModel.get(doi.getId());
	} else if (doi != null) {
	    ProcessDiagram model = new ProcessDiagram();
	    model.setName(doi.getProcess().getName());
	    List<ProcessActivity> activities = new ArrayList<ProcessActivity>();
	    for (ProcessActivity act : doi.getProcess().getActivities()) {
		ProcessActivity a = new ProcessActivity(act.getName());
		a.setType(act.getType());
		a.setAbstract(act.isAbstract());
		a.setConcrete(act.isConcrete());
		a.setId(act.getId());
		activities.add(a);
	    }

	    model.setActivities(activities);
	    model.setPid(doi.getProcess().getpid());
	    doiNameToProcessModel.put(doi.getId(), model);
	    pd = model;
	}
	if (pd == null) {
	    // if not found, search into domainObjectdefinitions
	    DomainObjectDefinition dod = manager.findDefinition(type);
	    if (dod != null) {
		DomainObjectInstance d = manager
			.convertToDomainObjectInstance(dod);
		if (d != null) {
		    return d.getProcess();
		}
	    }
	}
	return pd;
    }

    public String fromProcessModelToDoi(ProcessDiagram model) {
	if (doiNameToProcessModel.containsValue(model)) {
	    Iterator<String> iter = doiNameToProcessModel.keySet().iterator();
	    while (iter.hasNext()) {
		String key = iter.next();
		if (doiNameToProcessModel.get(key).equals(model)) {
		    return key;
		}
	    }
	}
	return "";
    }

    /**
     * @param pid
     * @return list of correlated process id (pid) for input
     */
    public List<DomainObjectInstance> getCorrelations(DomainObjectInstance doi) {
	return processEngine.getCorrelated(doi);
    }

    /**
     * 
     * @param db
     * @return
     */
    public List<String> getFragmentsFromDoiBean(String name) {
	ArrayList<String> response = new ArrayList<String>();
	DomainObjectInstance doi = manager.findInstanceById(name);
	if (doi == null) {
	    return response;
	}
	if (doi.getFragments() == null || doi.getFragments().isEmpty()) {
	    return response;
	}
	for (ServiceDiagram f : doi.getFragments()) {
	    response.add(f.getSid());
	}
	return response;
    }

    /**
     * @param process
     *            id
     * @return true if input pid is refinement for other process
     */
    public boolean isRefinement(Integer pid) {
	return processEngine.isRefinement(pid);
    }

    /**
     * 
     * @param process
     *            id
     * @return "master" process for current pid (that must be a refinement),
     *         null otherwise
     */
    public DomainObjectInstance getRefinementOrigin(Integer pid) {
	return processEngine.getRefinementOrigin(pid);
    }

    public DomainObjectInstance getDomainObjectInstanceForProcess(
	    ProcessDiagram p) {
	return processEngine.getDomainObjectInstance(p);
    }

    public List<DomainObjectDefinition> getDomainObjectDefinitions() {
	return manager.getDefinitions();
    }

    /**
     * @param name
     *            , domainObjectDefinition name
     * @return name of fragment for given domainObject name
     */
    public List<String> getFragmentNames(String name) {
	List<String> result = new ArrayList<String>();
	for (DomainObjectDefinition dod : manager.getDefinitions()) {
	    if (dod.getDomainObject().getName().equals(name)) {
		for (Fragment fr : dod.getFragments()) {
		    result.add(fr.getId());
		}
	    }
	}
	return result;
    }

    public void addExecutableHandler(String activityName,
	    AbstractExecutableActivityInterface handler) {
	processEngine.addExecutableHandler(activityName, handler);
    }

    /**
     * @param processDiagram
     *            to complete
     * @return a new ProcessDiagram with all activities refined
     * 
     */
    public ProcessDiagram getCompleteProcessDiagram(ProcessDiagram pd) {
	if (pd == null) {
	    logger.debug("getCompleteProcessDiagram - input null");
	    return new ProcessDiagram();
	}
	ProcessDiagram response = new ProcessDiagram();
	try {
	    for (int i = 0; i < pd.getActivities().size(); i++) {
		ProcessActivity pa = pd.getActivities().get(i);
		activityName.put(pa.getName(), pa.clone());
		if (pa.isAbstract()) {
		    // handle abstract activity case
		    response.addAllActivity(getActivitiesFromAbstractActivity(
			    pd, pa));
		} else {
		    response.addActivity(pa);
		}

	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
	return response;
    }

    private List<ProcessActivity> getActivitiesFromAbstractActivity(
	    ProcessDiagram pd, ProcessActivity pa) {
	ProcessDiagram response = new ProcessDiagram();
	if (pd != null && pa != null && pa.isAbstract() && hasRefinements(pd)
		&& pd.getCurrentActivity() != null
		&& pd.getCurrentActivity().getName() != null
		&& pa.getName() != null
		&& pd.getCurrentActivity().getName().equals(pa.getName())) {

	    logger.debug(pa.getName());
	    ProcessDiagram refinement = getRefinement(pd);
	    refinements.put(pd.getpid() + "_" + pa.getName(), refinement);
	    ProcessDiagram completeProcess = getCompleteProcessDiagram(refinement);
	    response.addAllActivity(completeProcess.getActivities());
	} else if (pa.isAbstract() && !pa.isRunning() && pa.isExecuted()) {
	    ProcessDiagram refinement = refinements.get(pd.getpid() + "_"
		    + pa.getName());
	    ProcessDiagram completeProcess = getCompleteProcessDiagram(refinement);
	    response.addAllActivity(completeProcess.getActivities());
	} else {
	    response.addActivity(pa);
	}
	return response.getActivities();
    }

    public Map<String, ProcessDiagram> getRefinements(ProcessDiagram process) {
	return refinements;
    }

    public DomainObjectInstance buildUserDoi(String userName) {
	// build a user doi
	DomainObjectInstance doi = new DomainObjectInstance();
	for (DomainObjectDefinition dod : manager.getDefinitions()) {
	    if (dod.getDomainObject().getName().equals("User")) {
		doi = manager.buildInstance(dod);
		break;
	    }
	}
	// set user infos
	doi.setId(userName);
	try {
	    processEngine.start(doi);
	} catch (ProcessEngineRuntimeException
		| InvalidFlowInitialStateException
		| InvalidFlowActivityException | FlowDuplicateActivityException e) {
	    logger.error(e.getMessage(), e);
	}
	return doi;
    }

    public ProcessEngine getProcessEngine() {
	return processEngine;
    }

    public ProcessDiagram getRunningBranch(ProcessDiagram pd) {
	return processEngine.getRunningBranch(pd);
    }

    /**
     * @return true if first and second are correlated using
     *         {@link DomainObjectInstance#getId()}.
     */
    public boolean isCorrelated(DomainObjectInstance first,
	    DomainObjectInstance second) {
	if (first == null || second == null) {
	    return false;
	}
	// check if there is direct correlation between first and second
	List<DomainObjectInstance> corr = processEngine.getCorrelated(first);
	for (DomainObjectInstance c : corr) {
	    if (c.getId().equals(second.getId())) {
		return true;
	    }
	}
	return false;
    }
}
