package eu.allowensembles.presentation.main;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.controller.events.StepEvent;
import eu.allowensembles.presentation.main.events.SelectedAbstractActivityEvent;
import eu.allowensembles.presentation.main.process.ProcessModelPanel;
import eu.allowensembles.utils.ResourceLoader;
import eu.fbk.das.process.engine.api.AdaptationProblem;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.domain.AbstractActivity;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ServiceDiagram;
import eu.fbk.das.process.engine.api.jaxb.ClauseType.Point;
import eu.fbk.das.process.engine.api.jaxb.ClauseType.Point.DomainProperty;
import eu.fbk.das.process.engine.api.jaxb.GoalType;

public class ActivityWindow extends JPanel {

    private static final long serialVersionUID = -3065754932068313302L;

    private static final Logger logger = LogManager
	    .getLogger(ActivityWindow.class);

    // private static JFrame frame;
    private MainController controller;
    private JFrame frame;

    ProcessDiagram current;

    private JPanel activityPanel;
    private JPanel problemPanel;
    private JPanel domainPanel;
    private JPanel resultPanel;
    private JPanel processPanel;
    private ProcessModelPanel graphActivityPanel;
    private ProcessModelPanel graphProcessPanel;

    private JLabel goalLabel;
    private Label goalProblemLabel;
    private Label activityLabel;
    private Label smvLabel;
    private Label dotLabel;
    private JTextArea logTextArea;

    /**
     * Launch the application.
     */
    public ActivityWindow(JFrame frame) throws JAXBException,
	    URISyntaxException, IOException {
	setOpaque(true);
	setLayout(null);

	this.frame = frame;

	JPanel mainPanel = new JPanel();
	mainPanel.setSize(1300, 1300);
	mainPanel.setLayout(null);
	add(mainPanel);

	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	// an example how to get current process
	tabbedPane.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent e) {

	    }

	});
	tabbedPane.setBounds(0, 0, 841, 510);

	mainPanel.add(tabbedPane);

	activityPanel = new JPanel();
	tabbedPane.addTab("Cell Activity", null, activityPanel, null);

	// create panel to show the activity to refine or adapt

	problemPanel = new JPanel();
	problemPanel.setLayout(null);

	problemPanel.setPreferredSize(new Dimension(800, 800));
	JScrollPane scrollFrame = new JScrollPane(problemPanel);
	problemPanel.setAutoscrolls(true);
	scrollFrame.setPreferredSize(new Dimension(600, 200));

	tabbedPane.addTab("Cell Specialization Problem", null, scrollFrame,
		null);

	domainPanel = new JPanel();
	tabbedPane.addTab("Planning Domain", null, domainPanel, null);

	resultPanel = new JPanel();
	tabbedPane.addTab("Planning Result", null, resultPanel, null);

	processPanel = new JPanel();
	processPanel.setLayout(null);
	tabbedPane.addTab("Cell Specialization Result", null, processPanel,
		null);

	Button cancelButton = new Button("Cancel");
	cancelButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	    }
	});

	JLabel lblLog = new JLabel("Execution Log");
	lblLog.setBounds(10, 505, 223, 22);
	lblLog.setFont(new Font("Serif", Font.BOLD, 14));
	mainPanel.add(lblLog);

	// log inside a scrollpane
	logTextArea = new JTextArea("");
	logTextArea.setBounds(10, 530, 820, 100);
	logTextArea.setEditable(false);
	mainPanel.add(logTextArea);

    }

    @Subscribe
    public void onStep(StepEvent e) {
	try {
	    ProcessDiagram pd = controller.getCurrentProcess();
	    controller.getProcessEngineFacade().getCompleteProcessDiagram(pd);
	    this.addLog("ProcessEngine step completed");
	} catch (Exception ex) {
	    logger.error(ex.getMessage(), ex);
	}
    }

    public void setController(MainController controller) {
	this.controller = controller;
	controller.register(this);

	// ACTIVITY PANEL
	activityPanel.setLayout(null);
	activityLabel = new Label("Goal");
	activityLabel.setBounds(24, 24, 550, 22);
	activityPanel.add(activityLabel);

	goalLabel = new JLabel("Goal");
	goalLabel.setBounds(24, 45, 500, 22);
	goalLabel.setFont(new Font("Serif", Font.PLAIN, 12));
	activityPanel.add(goalLabel);

	graphActivityPanel = new ProcessModelPanel(
		controller.getProcessEngineFacade());
	graphActivityPanel.setBounds(24, 75, 200, 90);

	// PROBLEM PANEL

	goalProblemLabel = new Label("Goal");
	goalProblemLabel.setBounds(24, 45, 500, 22);
	goalProblemLabel.setFont(new Font("Serif", Font.PLAIN, 12));

	// PLANNING PANEL
	smvLabel = new Label("SMV File");
	smvLabel.setBounds(24, 65, 50, 50);
	smvLabel.setFont(new Font("Serif", Font.BOLD, 15));

	// PLANNING RESULT PANEL
	dotLabel = new Label("DOT File");
	dotLabel.setBounds(24, 65, 50, 50);
	dotLabel.setFont(new Font("Serif", Font.BOLD, 15));

	// panel to show the adaptation (process) result in the ResultView
	graphProcessPanel = new ProcessModelPanel(
		controller.getProcessEngineFacade());
	graphProcessPanel.setBounds(60, 60, 800, 150);

    }

    @Subscribe
    public void HandleSelectedAbstractActivity(SelectedAbstractActivityEvent sa) {
	System.out.println("Activity Window");
	String label = sa.getLabel();

	ProcessDiagram process = controller.getCurrentProcess();

	// reference to the do instance
	DomainObjectInstance doi = controller.getProcessEngineFacade()
		.getDomainObjectInstanceForProcess(process);

	// add content the activity view
	showActivity(label, process, doi);
	// add content in the adaptation problem view
	showAdaptationProblem(label, process, doi);
	// add the smv file at the DomainView
	showSMVFile(label, process);
	// add dot file at the ResultView
	showDOTFile(label, process);

	// add adaptation process to ProcessView
	showProcessResult(label, process);

	frame.setVisible(true);
	refreshWindow();

    }

    public void refreshWindow() {
	frame.getContentPane().validate();
	frame.getContentPane().repaint();
    }

    public String retrieveGoalString(GoalType goal) {
	String goalString = "<html>";
	if (goal.getPoint().size() > 1) {
	    // MULTIPLE POINT (OR of AND)
	    for (int j = 0; j < goal.getPoint().size(); j++) {
		Point currentPoint = goal.getPoint().get(j);
		if (currentPoint.getDomainProperty().size() > 1) {
		    // AND among different DP
		    for (int k = 0; k < currentPoint.getDomainProperty().size(); k++) {
			DomainProperty currentDP = currentPoint
				.getDomainProperty().get(k);
			String dpName = currentDP.getDpName();
			String dpState = currentDP.getState().get(0);
			String first = dpName.concat(" = ");
			String second = first.concat(dpState);
			if (k == currentPoint.getDomainProperty().size() - 1) {
			    // last DP
			    goalString = goalString.concat(second);
			} else {
			    // not last add AND
			    String third = second.concat(" <b>AND</b> ");
			    goalString = goalString.concat(third);
			    goalString = goalString.concat("\n");

			}

		    }
		    goalString = goalString.concat("</html>");

		} else {
		    // single clause
		    DomainProperty dp = currentPoint.getDomainProperty().get(0);
		    String dpName = dp.getDpName();
		    String dpState = dp.getState().get(0);
		    String first = dpName.concat(" = ");
		    String second = first.concat(dpState);
		    goalString = goalString.concat(second);
		    goalString = goalString.concat("</html>");

		}
		if (j != goal.getPoint().size() - 1) {
		    goalString = goalString.concat("\n\n");
		    goalString = goalString.concat("OR");
		    goalString = goalString.concat("\n\n");
		}

	    }

	} else {
	    // single POINT
	    Point currentPoint = goal.getPoint().get(0);
	    if (currentPoint.getDomainProperty().size() > 1) {
		// AND among different DP
		for (int j = 0; j < currentPoint.getDomainProperty().size(); j++) {
		    DomainProperty currentDP = currentPoint.getDomainProperty()
			    .get(j);
		    String dpName = currentDP.getDpName();
		    String dpState = currentDP.getState().get(0);
		    String first = dpName.concat(" = ");
		    String second = first.concat(dpState);
		    if (j == currentPoint.getDomainProperty().size() - 1) {
			// last DP
			goalString = goalString.concat(second);
		    } else {
			// not last add AND
			String third = second.concat("AND");
			goalString = goalString.concat(third);
			goalString = goalString.concat("\n");

		    }

		}
		goalString = goalString.concat("</html>");
	    } else {
		// single clause
		DomainProperty dp = currentPoint.getDomainProperty().get(0);
		String dpName = dp.getDpName();
		String dpState = dp.getState().get(0);
		String first = dpName.concat(" = ");
		String second = first.concat(dpState);

		goalString = goalString.concat(second);
		goalString = goalString.concat("</html>");
	    }

	}
	return goalString;

    }

    // method to visualize the activity to refine or to adapt
    public void showActivity(String label, ProcessDiagram process,
	    DomainObjectInstance doi) {

	// create and show the activity
	ProcessDiagram newProc = new ProcessDiagram();
	ProcessActivity activity = new ProcessActivity();
	activity.setAbstract(true);
	activity.setName(label);
	newProc.addActivity(activity);
	graphActivityPanel.clear();
	graphActivityPanel.updateProcess(newProc);
	activityPanel.add(graphActivityPanel);

	// retrieve the goal of the specific activity
	boolean exist = false;
	String goalString = "";
	for (int i = 0; i < process.getActivities().size(); i++) {
	    ProcessActivity current = process.getActivities().get(i);
	    if (current.getName() == label) {
		exist = true;
		GoalType goal = ((AbstractActivity) current).getGoal();

		goalString = retrieveGoalString(goal);
		break;

	    }

	}

	if (!exist) {
	    // activity not in the main process, should be in a refinement
	    Map<String, ProcessDiagram> currentRefinements = controller
		    .getProcessEngineFacade().getRefinements(process);
	    for (Map.Entry<String, ProcessDiagram> entry : currentRefinements
		    .entrySet()) {
		// String key = entry.getKey();
		ProcessDiagram proc = entry.getValue();
		for (int i = 0; i < proc.getActivities().size(); i++) {
		    ProcessActivity currentActivity = proc.getActivities().get(
			    i);
		    if (currentActivity.getName() == label) {
			GoalType goal = ((AbstractActivity) currentActivity)
				.getGoal();
			goalString = retrieveGoalString(goal);
			break;
		    }
		}

	    }

	}

	// update goal string
	goalLabel.setText(convertToMultiline(goalString));

    }

    public void addFragmentsAndProperties(AdaptationProblem problem) {

	HashMap fragmentsOfCells = new HashMap<String, ArrayList<String>>();
	HashMap propertiesOfCells = new HashMap<String, ArrayList<String>>();
	// HashMap statesOfProperties = new HashMap<String, String>();

	Map<String, List<String>> fragments = problem.getRelevantServices();

	Iterator<Entry<String, List<String>>> itf = fragments.entrySet()
		.iterator();
	while (itf.hasNext()) {
	    String currentFragment = itf.next().getKey();
	    for (int j = 0; j < problem.getDomainObjectInstances().size(); j++) {
		DomainObjectInstance currentDoi = problem
			.getDomainObjectInstances().get(j);
		ArrayList<String> finalFragments = new ArrayList<String>();
		for (int k = 0; k < currentDoi.getFragments().size(); k++) {
		    ServiceDiagram fr = currentDoi.getFragments().get(k);
		    if (fr.getSid().equals(currentFragment)) {
			// add fragment to the respective cell
			finalFragments.add(currentFragment);
			fragmentsOfCells
				.put(currentDoi.getId(), finalFragments);
		    }
		}
	    }
	}

	// add domain properties
	Map<String, List<ObjectDiagram>> domainProperties = problem
		.getRelevantProperties();
	Iterator<Entry<String, List<ObjectDiagram>>> itp = domainProperties
		.entrySet().iterator();
	while (itp.hasNext()) {
	    List<ObjectDiagram> properties = itp.next().getValue();
	    for (int k = 0; k < properties.size(); k++) {
		ObjectDiagram currentObject = properties.get(k);
		propertiesOfCells.put(currentObject.getOid(),
			currentObject.getCurrentState());
	    }

	}
	JTable tableFragments = new JTable(
		toTableModelFragments(fragmentsOfCells));

	JPanel FragmentListPanel = new JPanel();
	FragmentListPanel.setBounds(24, 155, 550, 500);

	FragmentListPanel.setLayout(new BorderLayout());
	FragmentListPanel.add(tableFragments.getTableHeader(),
		BorderLayout.PAGE_START);
	FragmentListPanel.add(tableFragments, BorderLayout.CENTER);

	tableFragments.getColumnModel().getColumn(0).setPreferredWidth(80);
	tableFragments.getColumnModel().getColumn(1).setPreferredWidth(80);

	JTable tableContext = new JTable(
		toTableModelProperties(propertiesOfCells));

	JPanel ContextListPanel = new JPanel();
	ContextListPanel.setBounds(24, 400, 550, 200);

	ContextListPanel.setLayout(new BorderLayout());
	ContextListPanel.add(tableContext.getTableHeader(),
		BorderLayout.PAGE_START);
	ContextListPanel.add(tableContext, BorderLayout.CENTER);

	tableContext.getColumnModel().getColumn(0).setPreferredWidth(80);
	tableContext.getColumnModel().getColumn(1).setPreferredWidth(80);
	problemPanel.add(ContextListPanel);
	problemPanel.add(FragmentListPanel);

    }

    public void showAdaptationProblem(String label, ProcessDiagram process,
	    DomainObjectInstance doi) {

	problemPanel.removeAll();
	Label goalLabel = new Label("Goal");
	goalLabel.setBounds(24, 24, 550, 22);
	problemPanel.add(goalLabel);

	Label fragmentsLabel = new Label("Fragments");
	fragmentsLabel.setBounds(24, 125, 550, 22);
	problemPanel.add(fragmentsLabel);

	Label contextLabel = new Label("Context Properties");
	contextLabel.setBounds(24, 370, 550, 22);
	problemPanel.add(contextLabel);

	// add Grounded goal

	String goalString = "";
	for (int i = 0; i < process.getActivities().size(); i++) {
	    ProcessActivity current = process.getActivities().get(i);
	    if (current.getName() == label) {
		GoalType goal = ((AbstractActivity) current).getGoal();
		goalString = retrieveGoalString(goal);
		break;

	    }

	}
	// show goal string

	goalProblemLabel.setText(convertToMultiline(goalString));
	problemPanel.add(goalProblemLabel);

	// add fragments and domain properties to the problem viewer
	boolean exist = false;
	for (int i = 0; i < process.getActivities().size(); i++) {
	    ProcessActivity current = process.getActivities().get(i);
	    if (current.getName() == label) {

		exist = true;
		AdaptationProblem problem = ((AbstractActivity) current)
			.getProblem();
		addFragmentsAndProperties(problem);
		break;
	    }

	}
	if (!exist) {
	    // activity not in the main process, should be in a refinement
	    Map<String, ProcessDiagram> currentRefinements = controller
		    .getProcessEngineFacade().getRefinements(process);
	    for (Map.Entry<String, ProcessDiagram> entry : currentRefinements
		    .entrySet()) {
		// String key = entry.getKey();
		ProcessDiagram proc = entry.getValue();
		for (int i = 0; i < proc.getActivities().size(); i++) {
		    ProcessActivity currentActivity = proc.getActivities().get(
			    i);
		    if (currentActivity.getName() == label) {
			AdaptationProblem problem = ((AbstractActivity) currentActivity)
				.getProblem();
			addFragmentsAndProperties(problem);
			break;

		    }
		}

	    }

	}

    }

    public void showSMVFile(String label, ProcessDiagram process) {

	domainPanel.removeAll();
	String apid = null;
	for (int i = 0; i < process.getActivities().size(); i++) {
	    ProcessActivity current = process.getActivities().get(i);
	    if (current.getName() == label) {
		AdaptationProblem problem = ((AbstractActivity) current)
			.getProblem();
		apid = problem.getProblemId();
		break;
	    }
	}
	if (apid == null) {
	    logger.warn("Adaptation problem id is null");
	    return;
	}

	// read the smv file content

	String srcPath = ResourceLoader.getStoryboardFile().getParentFile()
		.getPath();
	String path = srcPath.concat("/Compositions/" + apid + "/" + apid
		+ ".smv");

	String output = null;
	try {
	    output = new Scanner(new File(path)).useDelimiter("\\Z").next();
	    System.out.println("" + output);
	} catch (Exception e) {
	    logger.error("Error on readind SMV file ", e);
	}

	JTextArea codeArea = new JTextArea(50, 150);

	codeArea.setText(output);
	codeArea.setEditable(false);
	codeArea.setFont(Font.getFont(Font.SANS_SERIF));

	JScrollPane sp = new JScrollPane(codeArea);
	sp.setPreferredSize(new Dimension(600, 400));
	sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	domainPanel.add(sp);
	domainPanel.add(smvLabel);

    }

    public void showDOTFile(String label, ProcessDiagram process) {
	resultPanel.removeAll();
	String apid = null;
	for (int i = 0; i < process.getActivities().size(); i++) {
	    ProcessActivity current = process.getActivities().get(i);
	    if (current.getName() == label) {
		AdaptationProblem problem = ((AbstractActivity) current)
			.getProblem();
		apid = problem.getProblemId();
		break;

	    }
	}
	if (apid == null) {
	    Map<String, ProcessDiagram> currentRefinements = controller
		    .getProcessEngineFacade().getRefinements(process);
	    for (Map.Entry<String, ProcessDiagram> entry : currentRefinements
		    .entrySet()) {
		String key = entry.getKey();
		ProcessDiagram proc = entry.getValue();
		for (int i = 0; i < proc.getActivities().size(); i++) {
		    ProcessActivity currentActivity = proc.getActivities().get(
			    i);
		    if (currentActivity.getName() == label) {
			AdaptationProblem problem = ((AbstractActivity) currentActivity)
				.getProblem();
			apid = problem.getProblemId();
			break;
		    }
		}

	    }
	}

	String srcPath = ResourceLoader.getStoryboardFile().getParentFile()
		.getPath();
	String path = srcPath.concat("/Compositions/" + apid + "/" + apid
		+ ".dot");
	String output = null;

	try {
	    output = new Scanner(new File(path)).useDelimiter("\\Z").next();

	} catch (Exception e) {
	    logger.error("Error on readind SMV file ", e);
	}

	JTextArea codeArea = new JTextArea(50, 150);
	codeArea.setText(output);
	codeArea.setEditable(false);
	// codeArea.setWrapStyleWord(true);
	codeArea.setFont(Font.getFont(Font.SANS_SERIF));

	JScrollPane sp = new JScrollPane(codeArea);
	sp.setPreferredSize(new Dimension(600, 400));
	sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	resultPanel.add(sp);
	resultPanel.add(dotLabel);

    }

    public void showProcessResult(String label, ProcessDiagram process) {

	// retrieve adaptation result process

	boolean exist = false;
	for (int i = 0; i < process.getActivities().size(); i++) {
	    ProcessActivity current = process.getActivities().get(i);
	    if (current.getName() == label) {
		exist = true;
		try {
		    Map<String, ProcessDiagram> currentRefinements = controller
			    .getProcessEngineFacade().getRefinements(process);
		    for (Map.Entry<String, ProcessDiagram> entry : currentRefinements
			    .entrySet()) {
			String key = entry.getKey();
			ProcessDiagram proc = entry.getValue();
			if (key.contains(current.getName())) {

			    // here I can retried the Adaptation Result as
			    // process diagram and
			    // show it in the viewer
			    graphProcessPanel.clear();
			    graphProcessPanel.updateProcess(proc);
			    processPanel.add(graphProcessPanel);
			}

		    }

		} catch (Exception e) {
		    logger.error("Error on reading adaptation result ", e);
		}
		break;
	    }
	}
	if (!exist) {
	    try {
		Map<String, ProcessDiagram> currentRefinements = controller
			.getProcessEngineFacade().getRefinements(process);
		for (Map.Entry<String, ProcessDiagram> entry : currentRefinements
			.entrySet()) {
		    ProcessDiagram currentProcess = entry.getValue();
		    for (int i = 0; i < currentProcess.getActivities().size(); i++) {
			ProcessActivity current = currentProcess
				.getActivities().get(i);
			if (current.getName() == label) {
			    exist = true;
			    try {
				Map<String, ProcessDiagram> refinements = controller
					.getProcessEngineFacade()
					.getRefinements(process);
				for (Map.Entry<String, ProcessDiagram> ent : refinements
					.entrySet()) {
				    String key = ent.getKey();
				    ProcessDiagram proc = ent.getValue();
				    if (key.contains(current.getName())) {

					// here I can retried the Adaptation
					// Result as
					// process diagram and
					// show it in the viewer
					graphProcessPanel.clear();
					graphProcessPanel.updateProcess(proc);
					processPanel.add(graphProcessPanel);
				    }

				}

			    } catch (Exception e) {
				logger.error(
					"Error on reading adaptation result ",
					e);
			    }

			    break;
			}
		    }
		}
	    } catch (Exception e) {
		logger.error("Error on reading adaptation result ", e);
	    }

	}

    }

    public String convertToMultiline(String orig) {
	return orig.replaceAll("\n", "<br/>");
    }

    public TableModel toTableModelFragments(Map map) {
	DefaultTableModel model = new DefaultTableModel(new Object[] { "Cells",
		"Fragments" }, 0);
	for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
	    Map.Entry entry = (Map.Entry) it.next();
	    model.addRow(new Object[] { entry.getKey(), entry.getValue() });
	}
	return model;
    }

    public TableModel toTableModelProperties(Map map) {
	DefaultTableModel model = new DefaultTableModel(new Object[] {
		"Context Property", "Current Status" }, 0);
	for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
	    Map.Entry entry = (Map.Entry) it.next();
	    model.addRow(new Object[] { entry.getKey(), entry.getValue() });
	}
	return model;
    }

    /**
     * Add a line at the bottom of the log
     * 
     * @param line
     */
    public void addLog(String line) {
	if (line == null) {
	    logger.warn("addLog: line must be not null");
	    return;
	}
	logTextArea.append(line + System.lineSeparator());
    }

}
