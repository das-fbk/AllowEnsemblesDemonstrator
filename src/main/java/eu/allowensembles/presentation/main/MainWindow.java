package eu.allowensembles.presentation.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import eu.allowensembles.DemonstratorConstant;
import eu.allowensembles.controller.MainController;
import eu.allowensembles.presentation.main.action.SelectedEntitiesButtonListener;
import eu.allowensembles.presentation.main.action.listener.CorrelateEntitiesListener;
import eu.allowensembles.presentation.main.action.listener.EnsembleListListener;
import eu.allowensembles.presentation.main.action.listener.EntityDetailActionListener;
import eu.allowensembles.presentation.main.action.listener.EntityTableSelectionListener;
import eu.allowensembles.presentation.main.action.listener.MenuExitListener;
import eu.allowensembles.presentation.main.action.listener.OpenScenarioListener;
import eu.allowensembles.presentation.main.action.listener.RobustnessButtonActionListener;
import eu.allowensembles.presentation.main.action.listener.SelectInstanceListener;
import eu.allowensembles.presentation.main.action.listener.SelectedComboEntityListener;
import eu.allowensembles.presentation.main.action.listener.StepButtonActionListener;
import eu.allowensembles.presentation.main.map.Routes.Route.Leg;
import eu.allowensembles.presentation.main.map.listener.MapMouseListener;
import eu.allowensembles.presentation.main.map.viewer.FancyWaypointRenderer;
import eu.allowensembles.presentation.main.map.viewer.MapViewerComponentBuilder;
import eu.allowensembles.presentation.main.map.viewer.MyWaypoint;
import eu.allowensembles.presentation.main.process.ProcessModelPanel;
import eu.allowensembles.privacyandsecurity.presentation.PSView;
import eu.allowensembles.robustness.controller.RobustnessController;
import eu.allowensembles.utility.presentation.UtilityView;
import eu.allowensembles.utils.DoiBean;
import eu.allowensembles.utils.UserData;
import eu.allowensembles.utils.WaypointUtil;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.impl.context.api.Ensemble;

public class MainWindow {

    private static final Logger logger = LogManager.getLogger(MainWindow.class);

    // main frame
    public JFrame frame;

    // other windows
    private JFrame newEntityFrame;
    private AddNewEntityWindow addNewEntityWindow;

    // main window components
    private JTable generalTable;
    private JTable ensemblesTable;
    private JScrollPane entitiesScrollPane;
    private JScrollPane ensemblesScrollPane;
    private Label label;
    private JPanel mainPanel;
    private Label label_1;
    private Label label_2;
    private JList<String> providedFragmentsList;
    private JScrollPane mainScrollPane;
    private Label label_3;
    private JList<String> cellInstancesList;
    private Label label_4;
    private JList ensembleMembersList;
    private ProcessModelPanel processModelPanel;
    private JLabel lblNewLabel;
    private JLabel lblProcessExecution;
    private ProcessModelPanel processExecutionPanel;
    private JLabel lblCorrelatedEntities;
    private JList<String> correlatedEntitiesList;
    private JButton robustnessButton;
    private JMenu mnHelp;
    private JMenuItem mntmAbout;
    private JXMapViewer mapPanel;
    private final RobustnessController robustnessController;

    // window builder
    private MapViewerComponentBuilder mvcb = new MapViewerComponentBuilder();

    private JScrollPane modelScrollPane;

    private MainController controller;

    private JToolBar toolbar;

    private JButton btnNextEntity;

    private JButton btnPreviousEntity;

    private JButton btnStep;

    private JButton btnPlaypause;

    private JPopupMenu popupMenu;

    private JList<String> entityDetailsList;

    private UtilityView utilityView;
    private PSView PSView;
    private ActivityWindow refinementView;

    private JFrame utilityFrame;
    private JFrame refinementFrame;

    private JFrame PSFrame;
    private JScrollPane entityDetailScrollPane;

    private JList<String> entityKnowledgeList;

    private JScrollPane entityKnowledgeScrollPane;

    private JComboBox<String> comboEntities;

    private JLabel lblComboEntities;

    private SelectJourneyAlternativeWindow selectAlternativeWindow;

    private JFrame selectAlternativeFrame;

    private JTextArea logTextArea;

    private AboutDialog abtDialog;

    private JMenu mnEdit;

    private PreferencesDialog preferencesDialog;

    private JPanel storyboardOnePanel;

    private JButton btnDisplayAdaptationPanel;

    private AdaptationPanel adaptationPanel;

    private JScrollPane monitorScrollPane;

    private JList<String> monitorList;

    private JPopupMenu generalTablePopup;

    /**
     * Create the application.
     */
    public MainWindow() {
	try {
	    initialize();
	} catch (IOException e) {
	    logger.error("Error in initialization", e);
	}
	robustnessController = new RobustnessController(this);
    }

    /**
     * Initialize the contents of the frame.
     * 
     * @throws IOException
     */
    private void initialize() throws IOException {
	frame = new JFrame("Allow Ensembles Demonstrator");

	mainPanel = new JPanel();
	mainPanel.setVisible(true);
	mainPanel.setLayout(null);
	// mettere solo preferredSize per far comparire le barre di scorrimento
	// verticali
	mainPanel.setPreferredSize(new Dimension(1024, 1300));

	storyboardOnePanel = new JPanel();
	storyboardOnePanel.setLayout(null);
	storyboardOnePanel.setBounds(0, 350, 1024, 800);
	// storyboardOnePanel.setBorder(new LineBorder(Color.red, 2));
	mainPanel.add(storyboardOnePanel, -1);

	adaptationPanel = new AdaptationPanel();
	adaptationPanel.setBounds(0, 350, 1024, 850);
	adaptationPanel.setLayout(null);
	adaptationPanel.setVisible(false);
	mainPanel.add(adaptationPanel);

	label = new Label(
		"Select a cell instance or an ensemble from list to display on map. Right click on map or entity to interact");
	label.setBounds(247, 335, 713, 22);
	mainPanel.add(label);

	// Label timeLabel = new Label("Time 6:00 AM");
	// timeLabel.setBounds(126, 338, 97, 22);
	// mainPanel.add(timeLabel);

	btnStep = new JButton("Step");
	btnStep.setActionCommand(DemonstratorConstant.STEP);
	btnStep.addActionListener(new StepButtonActionListener());
	btnStep.setIcon(new ImageIcon(MainWindow.class
		.getResource("/images/knob_walk.png")));

	btnPlaypause = new JButton("Play");
	// btnPlaypause.setPreferredSize(new Dimension(153, 23));
	btnPlaypause.setIcon(new ImageIcon(MainWindow.class
		.getResource("/images/knob_play_green.png")));
	btnPlaypause.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseClicked(MouseEvent e) {
		controller.play();
	    }

	});

	// display map
	mapPanel = new JXMapViewer();

	// build popup menu
	popupMenu = new JPopupMenu();
	// JMenuItem addNewPersonMenuItem = new JMenuItem("add new entity");
	// addNewPersonMenuItem.addMouseListener(new MouseAdapter() {
	// @Override
	// public void mousePressed(MouseEvent e) {
	// displayEntityWindow(true, null, true);
	// }
	// });
	// popupMenu.add(addNewPersonMenuItem);
	JMenuItem flexibusDelayPopup = new JMenuItem("Flexibus delay");
	flexibusDelayPopup.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mousePressed(MouseEvent e) {
		// TODO integration with collective adaptation here
		System.out.println("Flexibus delay!");
	    }
	});
	popupMenu.add(flexibusDelayPopup);

	label_1 = new Label("Cell details");
	label_1.setBounds(10, 424, 143, 22);
	storyboardOnePanel.add(label_1);

	// entityDetails
	JTabbedPane tabEntity = new JTabbedPane(JTabbedPane.TOP);
	tabEntity.setBounds(10, 452, 223, 119);
	storyboardOnePanel.add(tabEntity);

	entityDetailsList = new JList<String>();
	entityDetailsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	entityDetailsList.setBounds(10, 852, 223, 119);

	entityKnowledgeList = new JList<String>();
	entityKnowledgeList
		.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	entityKnowledgeList.setBounds(10, 852, 223, 119);

	entityDetailScrollPane = new JScrollPane(entityDetailsList);
	tabEntity.addTab("Details", null, entityDetailScrollPane);
	entityKnowledgeScrollPane = new JScrollPane(entityKnowledgeList);
	tabEntity.addTab("Context", null, entityKnowledgeScrollPane);

	monitorList = new JList<String>();
	monitorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	monitorList.setBounds(10, 852, 223, 119);
	DefaultListModel<String> testModelMonitorList = new DefaultListModel<String>();
	monitorList.setModel(testModelMonitorList);

	monitorScrollPane = new JScrollPane(monitorList);
	tabEntity.addTab("Monitors", null, monitorScrollPane);

	tabEntity.addTab("", null, new JPanel());
	tabEntity.setEnabledAt(3, false);
	addButtonToTab(tabEntity);

	// provided fragments
	label_2 = new Label("Provided fragments");
	label_2.setBounds(871, 25, 121, 22);
	storyboardOnePanel.add(label_2);

	providedFragmentsList = new JList<String>();
	providedFragmentsList.setBounds(843, 47, 149, 200);
	storyboardOnePanel.add(providedFragmentsList);

	comboEntities = new JComboBox<String>();
	comboEntities.setBounds(703, 0, 289, 23);
	comboEntities.addActionListener(new SelectedComboEntityListener());
	storyboardOnePanel.add(comboEntities);

	label_3 = new Label("Cell instances");
	label_3.setBounds(10, 19, 143, 22);
	storyboardOnePanel.add(label_3);

	cellInstancesList = new JList();
	cellInstancesList.setBounds(10, 47, 223, 200);
	cellInstancesList.addListSelectionListener(new SelectInstanceListener(
		this));
	cellInstancesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	storyboardOnePanel.add(cellInstancesList);

	label_4 = new Label("Ensemble members");
	label_4.setBounds(10, 577, 143, 22);
	storyboardOnePanel.add(label_4);

	ensembleMembersList = new JList();
	ensembleMembersList.setBounds(10, 616, 223, 141);
	ensembleMembersList.setEnabled(true);
	ensembleMembersList.getSelectionModel().addListSelectionListener(
		new EnsembleListListener(this));
	ensembleMembersList
		.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	storyboardOnePanel.add(ensembleMembersList);

	JLabel lblLog = new JLabel("Log");
	lblLog.setBounds(10, 1168, 223, 22);
	mainPanel.add(lblLog);

	// log inside a scrollpane
	logTextArea = new JTextArea("");
	logTextArea.setBounds(10, 1201, 982, 71);
	logTextArea.setEditable(false);
	JScrollPane logScrollPane = new JScrollPane(logTextArea,
		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	logScrollPane.setBounds(10, 1201, 982, 71);
	mainPanel.add(logScrollPane);

	lblNewLabel = new JLabel("Process model");
	lblNewLabel.setBounds(538, 19, 200, 22);
	storyboardOnePanel.add(lblNewLabel);

	lblProcessExecution = new JLabel("Process execution");
	lblProcessExecution.setBounds(538, 265, 161, 22);
	storyboardOnePanel.add(lblProcessExecution);

	// aggiungere lo scrollPane per far comparire le barre verticali
	mainScrollPane = new JScrollPane(mainPanel);

	lblCorrelatedEntities = new JLabel("Correlated cells");
	lblCorrelatedEntities.setBounds(10, 265, 111, 22);
	storyboardOnePanel.add(lblCorrelatedEntities);

	correlatedEntitiesList = new JList<String>();
	correlatedEntitiesList.setBounds(10, 293, 223, 119);
	correlatedEntitiesList
		.addMouseListener(new CorrelateEntitiesListener());
	storyboardOnePanel.add(correlatedEntitiesList);

	robustnessButton = new JButton("robustness view");
	robustnessButton.setBounds(843, 265, 149, 23);
	robustnessButton.setActionCommand(RobustnessButtonActionListener.OPEN);
	robustnessButton.addActionListener(new RobustnessButtonActionListener(
		this));
	storyboardOnePanel.add(robustnessButton);

	mainScrollPane.getVerticalScrollBar().setUnitIncrement(20);
	mainScrollPane
		.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	mainScrollPane
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

	frame.getContentPane().add(mainScrollPane);

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// change here for final frame size
	frame.setSize(1024, 768);
	frame.setResizable(false);
	// frame.setLocationRelativeTo(null); // *** this will center your app
	frame.setIconImage(ImageIO.read(getClass().getResource(
		"/images/1435065408_gear.png")));

	// Entity preference window
	newEntityFrame = new JFrame("Allow Ensembles Demonstrator");
	newEntityFrame.setType(Type.UTILITY);
	newEntityFrame.setVisible(false);
	newEntityFrame.setSize(530, 600);
	addNewEntityWindow = new AddNewEntityWindow(this);
	newEntityFrame.setContentPane(addNewEntityWindow);

	JMenuBar menuBar = new JMenuBar();
	frame.setJMenuBar(menuBar);

	JMenu mnScenario = new JMenu("File");
	menuBar.add(mnScenario);

	JMenuItem mntmOpen = new JMenuItem("Open");
	mntmOpen.addActionListener(new OpenScenarioListener());
	mnScenario.add(mntmOpen);

	JMenuItem mnExit = new JMenuItem("Exit");
	mnExit.addActionListener(new MenuExitListener());

	mnScenario.add(new JSeparator());
	JMenuItem scenario1 = new JMenuItem("Open scenario 1");
	scenario1.setActionCommand(DemonstratorConstant.SCENARIO1);
	scenario1.addActionListener(new OpenScenarioListener());
	mnScenario.add(scenario1);

	JMenuItem scenario2 = new JMenuItem("Open scenario review");
	scenario2.setActionCommand(DemonstratorConstant.SCENARIOREVIEW);
	scenario2.addActionListener(new OpenScenarioListener());
	mnScenario.add(scenario2);

	mnScenario.add(new JSeparator());
	mnScenario.add(mnExit);

	mnEdit = new JMenu("Edit");
	menuBar.add(mnEdit);

	JMenuItem mnPreferences = new JMenuItem("Preferences");
	mnEdit.add(mnPreferences);

	preferencesDialog = new PreferencesDialog();
	preferencesDialog.setVisible(false);

	mnPreferences.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mousePressed(MouseEvent e) {
		preferencesDialog.setVisible(true);
	    }
	});

	mnHelp = new JMenu("Help");
	menuBar.add(mnHelp);

	abtDialog = new AboutDialog();
	abtDialog.setVisible(false);

	mntmAbout = new JMenuItem("About");
	mntmAbout.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mousePressed(MouseEvent e) {
		abtDialog.setVisible(true);
	    }
	});
	mnHelp.add(mntmAbout);

	// add toolbar
	toolbar = new JToolBar();
	toolbar.setFloatable(false);
	toolbar.setRollover(false);
	toolbar.setEnabled(false);
	toolbar.setBorder(LineBorder.createGrayLineBorder());
	toolbar.setPreferredSize(new Dimension(100, 40));
	toolbar.add(btnPlaypause);
	toolbar.add(btnStep);
	toolbar.addSeparator();

	// create move next/previous entities
	btnPreviousEntity = new JButton("Previous");
	btnPreviousEntity.setIcon(new ImageIcon(MainWindow.class
		.getResource("/images/knob_left.png")));
	btnPreviousEntity
		.setActionCommand(SelectedEntitiesButtonListener.PREVIOUS);
	btnPreviousEntity.addActionListener(new SelectedEntitiesButtonListener(
		this));
	toolbar.add(btnPreviousEntity);

	btnNextEntity = new JButton("Next");
	btnNextEntity.setIcon(new ImageIcon(MainWindow.class
		.getResource("/images/knob_forward.png")));
	btnNextEntity.setActionCommand(SelectedEntitiesButtonListener.NEXT);
	btnNextEntity
		.addActionListener(new SelectedEntitiesButtonListener(this));

	toolbar.add(btnNextEntity);
	toolbar.addSeparator();

	btnDisplayAdaptationPanel = new JButton("Show/hide Adaptation Panel");
	btnDisplayAdaptationPanel.setIcon(new ImageIcon(MainWindow.class
		.getResource("/images/knob_lens.png")));
	btnDisplayAdaptationPanel.setActionCommand("show");
	btnDisplayAdaptationPanel.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("show")) {
		    if (storyboardOnePanel.isVisible()) {
			storyboardOnePanel.setVisible(false);
			adaptationPanel.setVisible(true);
		    } else {
			storyboardOnePanel.setVisible(true);
			adaptationPanel.setVisible(false);
		    }
		}

	    }
	});
	toolbar.add(btnDisplayAdaptationPanel);

	// TODO: add same for ensembles

	// add toolbar
	frame.getContentPane().add(toolbar, BorderLayout.NORTH);

	// add and hide utility view inside another frame
	try {
	    utilityFrame = new JFrame("Allow Ensembles Demonstrator");
	    utilityFrame.setType(Type.UTILITY);
	    utilityView = new UtilityView(this);
	    utilityView.setVisible(true);
	    utilityFrame.setSize(867, 570);
	    utilityFrame.setContentPane(utilityView);
	    showUtilityFrame(false);

	    // frame.getContentPane().add(utilityView);
	} catch (JAXBException | URISyntaxException e1) {
	    logger.error(e1.getMessage(), e1);
	}

	PSFrame = new JFrame("Allow Ensembles Privacy and Security");
	PSFrame.setType(Type.UTILITY);
	PSView = new PSView();
	PSView.setVisible(true);
	PSFrame.setSize(867, 640);
	PSFrame.setContentPane(PSView);
	showPSFrame(false);

	// add and hide activity view inside another frame
	try {
	    refinementFrame = new JFrame("Activity Details");
	    refinementFrame.setType(Type.UTILITY);
	    refinementView = new ActivityWindow(refinementFrame);
	    // refinementView.setVisible(true);
	    refinementFrame.setSize(867, 570);
	    refinementFrame.setContentPane(refinementView);
	    showRefinementFrame(false);

	    // frame.getContentPane().add(utilityView);
	} catch (JAXBException | URISyntaxException e2) {
	    logger.error(e2.getMessage(), e2);
	}

	// combo for entities selection
	lblComboEntities = new JLabel("Cell models");
	lblComboEntities.setBounds(636, 0, 55, 23);
	storyboardOnePanel.add(lblComboEntities);

	//
	try {
	    selectAlternativeFrame = new JFrame(
		    "Allow Ensembles - select alternative");
	    selectAlternativeFrame.setType(Type.UTILITY);
	    selectAlternativeFrame.setSize(675, 435);
	    selectAlternativeWindow = new SelectJourneyAlternativeWindow(this,
		    selectAlternativeFrame);
	    selectAlternativeFrame.setContentPane(selectAlternativeWindow);
	    selectAlternativeFrame.setVisible(false);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}

	// last init part: hide mainScrollPane
	showMainScrollPane(false);
	showToolbar(false);
    }

    public void addButtonToTab(final JTabbedPane tabEntity) {
	JButton button = new JButton("...");
	button.addMouseListener(new EntityDetailActionListener(this));
	tabEntity.setTabComponentAt(tabEntity.getTabCount() - 1, button);

    }

    public void showMainScrollPane(boolean value) {
	mainScrollPane.setVisible(value);
	showToolbar(true);
	refreshWindow();
    }

    private void showToolbar(boolean visible) {
	toolbar.setVisible(visible);
	btnPlaypause.setVisible(visible);
	btnStep.setVisible(visible);
	btnPreviousEntity.setVisible(visible);
	btnNextEntity.setVisible(visible);
    }

    private void addPopup(Component component, JPopupMenu popup) {
	component.addMouseListener(new MapMouseListener(controller, mapPanel,
		popup));
    }

    /**
     * Load route and display on map
     * 
     * @param routeFile
     * @param b
     */
    public void initMap(File routeFile, boolean displayRoutes) {
	// mapPanel = mvcb.buildViewer(routeFile);
	if (displayRoutes) {
	    mapPanel = mvcb.buildViewer(routeFile);
	} else {
	    mapPanel = mvcb.buildViewer(null);
	}
	mapPanel.setBounds(247, 11, 745, 321);
	mapPanel.setLayout(null);
	// mainPanel.add(mapPanel);
	mapPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
	mainPanel.add(mapPanel);

	addPopup(mapPanel, popupMenu);

	refreshWindow();
	logger.info("Route loaded");
    }

    public void loadDomainObjectInstancesTable(
	    List<DoiBean> domainObjectInstances) {
	Vector<Vector<String>> data = convertAndFilterForJtable(domainObjectInstances);

	Vector<String> columnNames = new Vector<String>();
	columnNames.add("Id");
	columnNames.add("Name");
	columnNames.add("Status");

	generalTable = new JTable(data, columnNames) {
	    @Override
	    public boolean isCellEditable(int r, int c) {
		return false; // Disallow the editing of any cell
	    }

	};

	generalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	generalTable.setBounds(10, 10, 200, 223);
	generalTable.setFillsViewportHeight(true);
	generalTable.getSelectionModel().addListSelectionListener(
		new EntityTableSelectionListener(generalTable));

	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	tabbedPane.setBounds(10, 11, 223, 321);
	mainPanel.add(tabbedPane);

	entitiesScrollPane = new JScrollPane(generalTable);
	tabbedPane.addTab("Cell instances", null, entitiesScrollPane, null);
	tabbedPane.setEnabledAt(0, true);

	// For storyboard 2: load ensemble data here
	ensemblesTable = new JTable();
	ensemblesTable.setBounds(10, 10, 200, 223);
	ensemblesTable.getSelectionModel().addListSelectionListener(
		new EnsembleListListener(this));

	ensemblesScrollPane = new JScrollPane(ensemblesTable);
	tabbedPane.addTab("Ensembles", null, ensemblesScrollPane, null);
	tabbedPane.setEnabledAt(1, true);

	refreshWindow();
    }

    public void resetDomainObjectInstanceTable() {
	if (generalTable != null) {
	    generalTable.setModel(new DefaultTableModel());
	}
    }

    /**
     * Convert input and filter only with mappable domainObjectInstances
     * (mappable means with lat and lon)
     * 
     * @param domainObjectInstances
     * @return
     */
    private Vector<Vector<String>> convertAndFilterForJtable(
	    List<DoiBean> domainObjectInstances) {
	Vector<Vector<String>> response = new Vector<Vector<String>>();
	for (DoiBean d : domainObjectInstances) {
	    if (d.getLat() != null && d.getLon() != null) {
		Vector<String> v = new Vector<String>();
		v.add(d.getId());
		v.add(d.getName());
		v.add(d.getStatus());
		response.add(v);
	    }
	}
	return response;
    }

    public void displayProcess(ProcessDiagram p, boolean model,
	    boolean execution) {
	if (p == null) {
	    logger.warn("ProcessDiagram must be not null");
	    return;
	}
	// create processExecutionPanel or processModelPanel on need
	if (processModelPanel == null) {
	    processModelPanel = new ProcessModelPanel(
		    controller.getProcessEngineFacade());

	    processModelPanel.setLayout(new GridBagLayout());
	    JScrollPane modelScrollPane = new JScrollPane(processModelPanel);
	    modelScrollPane.setBounds(247, 47, 586, 200);
	    modelScrollPane.setBorder(new LineBorder(new Color(0, 0, 0)));

	    storyboardOnePanel.add(modelScrollPane);
	}
	if (processExecutionPanel == null) {
	    // update process execution
	    processExecutionPanel = new ProcessModelPanel(
		    controller.getProcessEngineFacade());
	    processExecutionPanel.init(this);
	    processExecutionPanel.setLayout(new FlowLayout());
	    modelScrollPane = new JScrollPane(processExecutionPanel);
	    modelScrollPane.setBounds(247, 293, 745, 464);
	    modelScrollPane.setBorder(new LineBorder(new Color(0, 0, 0)));

	    storyboardOnePanel.add(modelScrollPane);
	}
	// display them
	if (model) {
	    processModelPanel.updateProcess(p);
	}
	if (execution) {
	    processExecutionPanel.updateProcess(p);
	}
	// then refresh window
	refreshWindow();
    }

    public void refreshWindow() {
	frame.getContentPane().validate();
	frame.getContentPane().repaint();
    }

    public void setController(MainController controller) {
	this.controller = controller;
	utilityView.setController(controller);
	PSView.setController(controller);
	addNewEntityWindow.setController(controller);
    }

    public void updateDomainObjectInstancesTable(
	    List<DoiBean> domainObjectInstances) {
	Vector<Vector<String>> data = convertAndFilterForJtable(domainObjectInstances);

	Vector<String> columnNames = new Vector<String>();
	columnNames.add("Id");
	columnNames.add("Name");
	columnNames.add("Status");

	generalTable.setModel(new DefaultTableModel(data, columnNames));

	refreshWindow();
    }

    public void selectFirstEntityInTable() {
	if (generalTable.getModel().getValueAt(0, 0) != null) {
	    generalTable.setRowSelectionInterval(0, 0);
	}
    }

    public String getSelectedEntityInTable() {
	int sr = generalTable.getSelectedRow();
	if (sr == -1 || sr >= generalTable.getModel().getRowCount()) {
	    return "";
	}
	return (String) generalTable.getModel().getValueAt(sr, 0);
    }

    // public void selectPreviousEntity() {
    // if (current == null){
    //
    // }
    // if (generalTable.getRowCount() > 0) {
    // int sr = generalTable.getSelectedRow();
    // if (sr == -1) {
    // sr = 0;
    // }
    // int tsr = sr - 1;
    // if (tsr < 0) {
    // tsr = 0;
    // }
    // generalTable.getSelectionModel().setSelectionInterval(tsr, tsr);
    // // updatedSelectedInstance(controller.getCurrentDoiBean().getName());
    // }
    // }

    // public void selectNextEntity() {
    // if (generalTable.getRowCount() > 0) {
    // int sr = generalTable.getSelectedRow();
    // if (sr == -1) {
    // sr = 0;
    // }
    // int tsr = sr + 1;
    // if (tsr > generalTable.getRowCount()) {
    // tsr = generalTable.getRowCount();
    // }
    // generalTable.getSelectionModel().setSelectionInterval(tsr, tsr);
    // // updatedSelectedInstance(controller.getCurrentDoiBean().getName());
    // }
    // }

    // private void updatedSelectedInstance(String name) {
    // if (cellInstancesList != null) {
    // for (int i = 0; i < cellInstancesList.getModel().getSize(); i++) {
    // String element = cellInstancesList.getModel().getElementAt(i);
    // if (element.equals(name)) {
    // cellInstancesList.setSelectedIndex(i);
    // // cellInstancesList.setSelectedValue(element, true);
    // System.out.println(cellInstancesList.getSelectedValue());
    // break;
    // }
    //
    // }
    // }
    // }

    public void updateSelectedEntityDetails(List<String> toDisplay) {
	String[] array = new String[toDisplay.size()];
	for (int i = 0; i < toDisplay.size(); i++) {
	    array[i] = toDisplay.get(i);
	}
	entityDetailsList.setListData(array);

    }

    public void updateSelectedEntityCorrelations(List<String> pids) {
	String[] array = new String[pids.size()];
	for (int i = 0; i < pids.size(); i++) {
	    array[i] = String.valueOf(pids.get(i));
	}
	correlatedEntitiesList.setListData(array);
    }

    public void updateSelectedEntityProvidedFragments(List<String> values) {
	String[] array = new String[values.size()];
	for (int i = 0; i < values.size(); i++) {
	    array[i] = values.get(i);
	}
	providedFragmentsList.setListData(array);
    }

    public void showUtilityFrame(boolean visible) {
	utilityFrame.setVisible(visible);
    }

    public void showPSFrame(boolean visible) {
	PSFrame.setVisible(visible);
    }

    public void showRefinementFrame(boolean visible) {
	refinementFrame.setVisible(visible);
    }

    /**
     * 
     * @return the model of current process (not the executed one, see
     *         {@link MainController} method getCurrentProcess for it )
     */
    public ProcessDiagram getCurrentProcess() {
	if (generalTable.getRowCount() > 0) {
	    DoiBean current = controller.getCurrentDoiBean();
	    ProcessDiagram process = controller.getProcessEngineFacade()
		    .getModel(current.getName());
	    return process;

	}

	return null;
    }

    public MainController getController() {
	return controller;
    }

    public JXMapViewer getMapViewer() {
	return mapPanel;
    }

    public void updateCellInstances(List<String> toDisplay) {
	String[] array = new String[toDisplay.size()];
	for (int i = 0; i < toDisplay.size(); i++) {
	    array[i] = String.valueOf(toDisplay.get(i));
	}
	cellInstancesList.setListData(array);
    }

    public void updateEntityKnowledge(List<String> toDisplay) {
	String[] array = new String[toDisplay.size()];
	for (int i = 0; i < toDisplay.size(); i++) {
	    array[i] = String.valueOf(toDisplay.get(i));
	}
	entityKnowledgeList.setListData(array);
    }

    public ActivityWindow getActivityWindow() {

	return refinementView;
    }

    public UtilityView getUtilityView() {
	return utilityView;
    }

    public PSView getPSView() {
	return PSView;
    }

    public void updateComboxEntities(List<String> input) {
	comboEntities.removeAllItems();
	Collections.sort(input);
	input.add(0, "");
	for (String s : input) {
	    comboEntities.addItem(s);
	}

	// refreshWindow();
    }

    public void resetCellInstances() {
	String[] temp = { "" };
	cellInstancesList.setListData(temp);
    }

    public void resetCorrelatedCells() {
	String[] temp = { "" };
	correlatedEntitiesList.setListData(temp);
    }

    public void resetCellDetails() {
	String[] temp = { "" };
	entityDetailsList.setListData(temp);
    }

    public void resetContextDetails() {
	String[] temp = { "" };
	entityKnowledgeList.setListData(temp);
    }

    public void resetProcessExecution() {
	if (processExecutionPanel != null) {
	    processExecutionPanel.clear();
	}

    }

    public void resetProcessModel() {
	if (processModelPanel != null) {
	    processModelPanel.clear();
	}

    }

    public void centerMapOn(GeoPosition geoPosition) {
	if (geoPosition == null) {
	    logger.warn("Not possible to center map on null position");
	    return;
	}
	mapPanel.setAddressLocation(geoPosition);
	refreshWindow();
    }

    /**
     * Add selected route on mapPanel
     * 
     * @param r
     *            the route
     * @param color
     *            of the route
     * 
     * @throws NullPointerException
     *             if r is null
     */
    public JXMapViewer displayRouteOnMap(List<Leg> legs, Color color) {
	JXMapViewer mapViewer = mvcb.buildViewer();
	return displayRouteOnMap(mapViewer, legs, color);
    }

    public JXMapViewer displayRouteOnMap(JXMapViewer map, List<Leg> legs,
	    Color color) {
	if (legs == null) {
	    logger.error("Cannot add null route on map");
	    throw new NullPointerException("Cannot add null route on map");
	}
	mvcb.addRoute(map, legs, color);
	return map;
    }

    public void displayRouteOnMapWithLegColors(List<Leg> legs) {
	if (legs == null) {
	    logger.error("Cannot add null route on map");
	    throw new NullPointerException("Cannot add null route on map");
	}
	mvcb.addRoute(mapPanel, legs);
    }

    public void displayAlternativesWindow(UserData ud) {
	// set data and display window
	selectAlternativeWindow.setData(ud);
	selectAlternativeFrame.setVisible(true);
    }

    public void displayEntityWindow(boolean visible, UserData ud,
	    boolean editable) {
	if (ud != null) {
	    addNewEntityWindow.setUsername(ud.getName());
	    addNewEntityWindow.setPreferences(ud.getPreferences());
	    addNewEntityWindow.setEditable(editable);
	} else {
	    addNewEntityWindow.reset();
	    addNewEntityWindow.setEditable(editable);
	}
	newEntityFrame.setVisible(visible);

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

    public void selectCellInstances(int index) {
	cellInstancesList.setSelectedIndex(index);

    }

    public String getSelectedCorrelatedTable(int rowIndex) {
	return (String) generalTable.getModel().getValueAt(rowIndex, 0);
    }

    public String getSelectedCorrelatedEntity() {
	int sr = correlatedEntitiesList.getSelectedIndex();
	if (sr == -1 || sr >= correlatedEntitiesList.getModel().getSize()) {
	    return "";
	}
	return correlatedEntitiesList.getModel().getElementAt(sr);
    }

    public void selectEntityOnTable(String id) {
	for (int i = 0; i < generalTable.getModel().getRowCount(); i++) {
	    if (generalTable.getModel().getValueAt(i, 0) != null
		    && generalTable.getModel().getValueAt(i, 0).equals(id)) {
		generalTable.setRowSelectionInterval(i, i);
	    }
	}

    }

    public void showRobustnessView() {
	robustnessController.showRobustnessView();
    }

    public void updateEnsembles(List<Ensemble> list) {
	Vector<Vector<String>> data = new Vector<Vector<String>>();
	for (Ensemble ensemble : list) {
	    Vector<String> v = new Vector<String>();
	    v.add(ensemble.getName());
	    data.add(v);
	}

	Vector<String> columnNames = new Vector<String>();
	columnNames.add("Name");

	ensemblesTable.setModel(new DefaultTableModel(data, columnNames));

	// populate ensemble members list
	String[] array = new String[list.get(0).getRoles().size()];
	Ensemble ensemble = list.get(0);
	int i = 0;
	for (String role : ensemble.getRoles()) {
	    array[i] = role;
	    i++;
	}
	ensembleMembersList.setListData(array);
    }

    public void resetEnsemblesList() {
	String[] temp = { "" };
	ensembleMembersList.setListData(temp);
    }

    public void resetProcessDisplay() {
	processExecutionPanel = null;
	processModelPanel = null;

    }

    public String getEnsembleInTable(int i) {
	try {
	    return (String) ensemblesTable.getValueAt(i, 0);
	} catch (ArrayIndexOutOfBoundsException e) {
	    logger.error(e.getMessage());
	    return "";
	}
    }

    public void updateMonitors(List<String> values) {
	monitorList.setModel(new DefaultListModel<String>());
	if (values != null && !values.isEmpty()) {
	    DefaultListModel<String> model = (DefaultListModel<String>) monitorList
		    .getModel();
	    for (String v : values) {
		model.addElement(v);
	    }
	}
    }

    public JXMapViewer getMapPanel() {
	return mapPanel;
    }

    public void displayPoints(List<GeoPosition> points, Color color,
	    URL resource) {
	// add bikesharing points
	Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>();
	for (GeoPosition point : points) {
	    MyWaypoint wpbs = WaypointUtil.buildMapIcon("", color, point,
		    resource);
	    waypoints.add(wpbs);
	}
	WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
	waypointPainter.setWaypoints(waypoints);
	waypointPainter.setRenderer(new FancyWaypointRenderer());
	((CompoundPainter<JXMapViewer>) mapPanel.getOverlayPainter())
		.addPainter(waypointPainter);
    }

}
