package eu.allowensembles.privacyandsecurity.presentation;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.Window.Type;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dfki.layer.Coordinate;
import de.dfki.layer.Layer;
import de.dfki.layer.SafetyArea;
import eu.allowensembles.controller.MainController;
import eu.allowensembles.evoknowledge.presentation.GraphicsView;
import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.UserData;
//import eu.allowensembles.privacyandsecurity.controller.Alternative;
//import eu.allowensembles.privacyandsecurity.controller.Coordinate;

public class PSView extends JPanel {

    private static final long serialVersionUID = 6484315110464555427L;
    private static JFrame frame;
    private JTable table, tableAlternatives, tableFilteredAlternatives,
	    tableRelativeImportance;
    private MainController controller;
    private List<Alternative> origAlt = new ArrayList<Alternative>();
    UserData userData;
    private static final Logger logger = LogManager.getLogger(MainWindow.class);
    private JPanel EntityInformationPanel, AlternativesPanel, SafetyPrefPanel,
	    PrivacyPrefPanel, EvoKnowledgePanel, SafetyPanel,
	    FilteredAlternativesPanel;
    private Checkbox checkboxUnsafe, checkboxUnsafe_, checkboxCrowded,
	    checkboxCrowded_, checkboxPolluted, checkboxPolluted_,
	    checkboxUntrustedprov, checkboxUntrustedprov_;
    private JSlider sliderName, sliderEmail, sliderPhone, sliderGPS,
	    sliderTime, sliderCost, sliderReliability, sliderWalkingDistance,
	    sliderSafety, sliderPrivacy, sliderNumOfChanges;
    private Choice modelTypeChoice, robustnessChoice;
    private JTextField nameText, numberOfChangesField, walkingDistanceField,
	    maxCostField, maxTravelTimeField;
    private JCheckBox carCheckBox, flexibusCheckBox, walkCheckBox,
	    carpoolingCheckBox, cashCheckBox, creditCardCheckBox,
	    payPalCheckBox, serviceCardCheckBox;
    private Layer<SafetyArea> newLayer;
    private String[] columnNames2 = { "Alternatives", "Transportation modes",
	    "Safety level" };
    private String[][] matrix2;
    private DefaultTableModel tableModelFilt;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		try {
		    // MainController mc = new MainController(null);
		    // mc.onStoryboardLoaded(null);
		    frame = new JFrame();
		    frame.setTitle("Safety and Privacy");
		    frame.setType(Type.UTILITY);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setSize(867, 594);
		    frame.setContentPane(new PSView());

		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public PSView() throws IOException {
	setOpaque(true);
	setLayout(null);

	JPanel mainPanel = new JPanel();
	mainPanel.setSize(867, 594);
	add(mainPanel);
	mainPanel.setLayout(null);

	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	tabbedPane.setBounds(0, 0, 841, 580);
	mainPanel.add(tabbedPane);

	/*** Alternatives panel ***/

	AlternativesPanel = new JPanel();
	tabbedPane.addTab("Alternatives", null, AlternativesPanel, null);
	AlternativesPanel.setLayout(null);
	Label label = new Label(
		"Safety and Privacy component receives a list of alternatives from the demonstrator");
	label.setBounds(41, 25, 550, 22);
	AlternativesPanel.add(label);

	/*** Safety and Privacy constraints panel ***/

	JPanel SPConstraintsPanel = new JPanel();
	tabbedPane.addTab("Safety and Privacy constraints", null,
		SPConstraintsPanel, null);
	SPConstraintsPanel.setLayout(null);
	JTabbedPane tabbedPane1 = new JTabbedPane(JTabbedPane.TOP);
	tabbedPane1.setBounds(0, 0, 641, 580);
	SPConstraintsPanel.add(tabbedPane1);

	/* Entity information tab */

	EntityInformationPanel = new JPanel();
	tabbedPane1.addTab("Entity information", null, EntityInformationPanel,
		null);
	EntityInformationPanel.setLayout(null);
	Label mlabel = new Label("Model type");
	mlabel.setBounds(24, 25, 90, 22);
	EntityInformationPanel.add(mlabel);
	JLabel lblName = new JLabel("Name");
	lblName.setBounds(24, 66, 90, 22);
	EntityInformationPanel.add(lblName);

	/* Preferences tab */

	JPanel entityPreferencesPanel = new JPanel();
	tabbedPane1.addTab("Options", null, entityPreferencesPanel, null);
	entityPreferencesPanel.setLayout(null);
	carCheckBox = new JCheckBox("Car");
	carCheckBox.setBounds(10, 41, 97, 23);
	entityPreferencesPanel.add(carCheckBox);
	JLabel lblTransportationType = new JLabel("Transportation type");
	lblTransportationType.setBounds(10, 11, 200, 23);
	entityPreferencesPanel.add(lblTransportationType);
	flexibusCheckBox = new JCheckBox("Flexibus");
	flexibusCheckBox.setBounds(10, 69, 97, 23);
	entityPreferencesPanel.add(flexibusCheckBox);
	walkCheckBox = new JCheckBox("Walk");
	walkCheckBox.setBounds(10, 98, 97, 23);
	entityPreferencesPanel.add(walkCheckBox);
	carpoolingCheckBox = new JCheckBox("Carpooling");
	carpoolingCheckBox.setBounds(10, 129, 110, 23);
	entityPreferencesPanel.add(carpoolingCheckBox);
	numberOfChangesField = new JTextField();
	numberOfChangesField.setBounds(312, 42, 44, 20);
	entityPreferencesPanel.add(numberOfChangesField);
	numberOfChangesField.setColumns(10);
	JLabel lblNumverOfChanges = new JLabel("Number of changes");
	lblNumverOfChanges.setBounds(184, 42, 134, 20);
	entityPreferencesPanel.add(lblNumverOfChanges);
	JLabel lblWalkingDistance = new JLabel("Walking distance");
	lblWalkingDistance.setBounds(184, 70, 114, 20);
	entityPreferencesPanel.add(lblWalkingDistance);
	walkingDistanceField = new JTextField();
	walkingDistanceField.setColumns(10);
	walkingDistanceField.setBounds(312, 70, 44, 20);
	entityPreferencesPanel.add(walkingDistanceField);
	JLabel lblMaxCost = new JLabel("Max cost");
	lblMaxCost.setBounds(184, 101, 114, 20);
	entityPreferencesPanel.add(lblMaxCost);
	maxCostField = new JTextField();
	maxCostField.setColumns(10);
	maxCostField.setBounds(312, 101, 44, 20);
	entityPreferencesPanel.add(maxCostField);
	JLabel lblMaxTravelTime = new JLabel("Max travel time");
	lblMaxTravelTime.setBounds(184, 132, 114, 20);
	entityPreferencesPanel.add(lblMaxTravelTime);
	maxTravelTimeField = new JTextField();
	maxTravelTimeField.setColumns(10);
	maxTravelTimeField.setBounds(312, 132, 44, 20);
	entityPreferencesPanel.add(maxTravelTimeField);
	JLabel lblUtilityPreferences = new JLabel("Utility preferences");
	lblUtilityPreferences.setBounds(184, 11, 168, 23);
	entityPreferencesPanel.add(lblUtilityPreferences);
	// robustnessChoice = new Choice();
	// robustnessChoice.setBounds(312, 163, 134, 20);
	// robustnessChoice.add("low");
	// robustnessChoice.add("medium");
	// robustnessChoice.add("high");
	// entityPreferencesPanel.add(robustnessChoice);
	// JLabel lblRobustness = new JLabel("Robustness level");
	// lblRobustness.setBounds(184, 163, 110, 23);
	// entityPreferencesPanel.add(lblRobustness);

	JLabel lblPaymentMethod = new JLabel("Payment method");
	lblPaymentMethod.setBounds(10, 215, 114, 23);
	entityPreferencesPanel.add(lblPaymentMethod);
	cashCheckBox = new JCheckBox("Cash");
	cashCheckBox.setBounds(10, 243, 97, 23);
	entityPreferencesPanel.add(cashCheckBox);
	creditCardCheckBox = new JCheckBox("Credit card");
	creditCardCheckBox.setBounds(10, 273, 110, 23);
	entityPreferencesPanel.add(creditCardCheckBox);
	payPalCheckBox = new JCheckBox("Paypal");
	payPalCheckBox.setBounds(10, 303, 97, 23);
	entityPreferencesPanel.add(payPalCheckBox);
	serviceCardCheckBox = new JCheckBox("Smart card");
	serviceCardCheckBox.setBounds(10, 333, 110, 23);
	entityPreferencesPanel.add(serviceCardCheckBox);

	JLabel lblUserWeights = new JLabel("User weights");
	lblUserWeights.setBounds(184, 218, 97, 16);
	entityPreferencesPanel.add(lblUserWeights);
	JLabel lblTime = new JLabel("Time");
	lblTime.setBounds(184, 330, 70, 16);
	entityPreferencesPanel.add(lblTime);
	sliderTime = new JSlider();
	sliderTime.setBounds(312, 330, 168, 16);
	entityPreferencesPanel.add(sliderTime);
	JLabel lblCost = new JLabel("Cost");
	lblCost.setBounds(184, 303, 70, 16);
	entityPreferencesPanel.add(lblCost);
	sliderCost = new JSlider();
	sliderCost.setBounds(312, 303, 168, 16);
	entityPreferencesPanel.add(sliderCost);
	JLabel lblReliability = new JLabel("Reliability");
	lblReliability.setBounds(184, 357, 70, 16);
	entityPreferencesPanel.add(lblReliability);
	sliderReliability = new JSlider();
	sliderReliability.setBounds(312, 359, 168, 16);
	entityPreferencesPanel.add(sliderReliability);
	JLabel lblWalkingdistance = new JLabel("Walking distance");
	lblWalkingdistance.setBounds(184, 266, 110, 30);
	entityPreferencesPanel.add(lblWalkingdistance);
	sliderWalkingDistance = new JSlider();
	sliderWalkingDistance.setBounds(312, 276, 168, 16);
	entityPreferencesPanel.add(sliderWalkingDistance);
	JLabel lblSafety = new JLabel("Security");
	lblSafety.setBounds(184, 384, 70, 30);
	entityPreferencesPanel.add(lblSafety);
	sliderSafety = new JSlider();
	sliderSafety.setBounds(312, 394, 168, 16);
	entityPreferencesPanel.add(sliderSafety);
	JLabel lblPrivacy = new JLabel("Privacy");
	lblPrivacy.setBounds(184, 411, 70, 30);
	entityPreferencesPanel.add(lblPrivacy);
	sliderPrivacy = new JSlider();
	sliderPrivacy.setBounds(312, 421, 168, 16);
	entityPreferencesPanel.add(sliderPrivacy);
	JLabel lblNumOfChanges = new JLabel("Number of changes");
	lblNumOfChanges.setBounds(184, 236, 130, 30);
	entityPreferencesPanel.add(lblNumOfChanges);
	sliderNumOfChanges = new JSlider();
	sliderNumOfChanges.setBounds(312, 246, 168, 16);
	entityPreferencesPanel.add(sliderNumOfChanges);

	/* Safety and Privacy tab */

	JPanel entityPrivacyandSecurityPanel = new JPanel();
	tabbedPane1.addTab("Privacy and security", null,
		entityPrivacyandSecurityPanel, null);
	entityPrivacyandSecurityPanel.setLayout(null);
	tabbedPane1.setSelectedIndex(2);
	JLabel lblNewLabel = new JLabel("Safety preferences");
	lblNewLabel.setBounds(10, 10, 200, 22);
	entityPrivacyandSecurityPanel.add(lblNewLabel);

	SafetyPrefPanel = new JPanel();
	SafetyPrefPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
	SafetyPrefPanel.setBounds(20, 43, 590, 170);
	entityPrivacyandSecurityPanel.add(SafetyPrefPanel);
	SafetyPrefPanel.setLayout(null);

	JLabel lblStrictly = new JLabel("Strictly");
	lblStrictly.setBounds(195, 11, 63, 22);
	SafetyPrefPanel.add(lblStrictly);
	JLabel lblFlexible = new JLabel("Flexible");
	lblFlexible.setBounds(298, 11, 63, 22);
	SafetyPrefPanel.add(lblFlexible);

	JLabel lblAvoidUnsafeAreas = new JLabel("Avoid unsafe areas");
	lblAvoidUnsafeAreas.setBounds(10, 38, 172, 22);
	SafetyPrefPanel.add(lblAvoidUnsafeAreas);
	JLabel lblAvoidCrowdedAreas = new JLabel("Avoid crowded areas");
	lblAvoidCrowdedAreas.setBounds(10, 68, 172, 22);
	SafetyPrefPanel.add(lblAvoidCrowdedAreas);
	JLabel lblAvoidPollutedAreas = new JLabel("Avoid polluted areas");
	lblAvoidPollutedAreas.setBounds(10, 99, 172, 22);
	SafetyPrefPanel.add(lblAvoidPollutedAreas);
	JLabel lblAvoidUntrustedAreas = new JLabel("Avoid untrusted providers");
	lblAvoidUntrustedAreas.setBounds(10, 127, 172, 22);
	SafetyPrefPanel.add(lblAvoidUntrustedAreas);

	JLabel lblSensitivityOfData = new JLabel("Privacy preferences");
	lblSensitivityOfData.setBounds(10, 224, 200, 22);
	entityPrivacyandSecurityPanel.add(lblSensitivityOfData);
	PrivacyPrefPanel = new JPanel();
	PrivacyPrefPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
	PrivacyPrefPanel.setBounds(20, 257, 590, 167);
	entityPrivacyandSecurityPanel.add(PrivacyPrefPanel);
	PrivacyPrefPanel.setLayout(null);
	JLabel lblNotSensitive = new JLabel("share");
	lblNotSensitive.setBounds(180, 11, 122, 26);
	PrivacyPrefPanel.add(lblNotSensitive);
	JLabel lblDoNotShare = new JLabel("do not share");
	lblDoNotShare.setBounds(345, 11, 122, 26);
	PrivacyPrefPanel.add(lblDoNotShare);
	JLabel lblName_1 = new JLabel("Name");
	lblName_1.setBounds(10, 47, 114, 22);
	PrivacyPrefPanel.add(lblName_1);
	JLabel lblEmail = new JLabel("Email");
	lblEmail.setBounds(10, 77, 114, 22);
	PrivacyPrefPanel.add(lblEmail);
	JLabel lblPhone = new JLabel("Phone");
	lblPhone.setBounds(10, 107, 114, 22);
	PrivacyPrefPanel.add(lblPhone);
	JLabel lblGpsLocation = new JLabel("GPS location");
	lblGpsLocation.setBounds(10, 137, 114, 22);
	PrivacyPrefPanel.add(lblGpsLocation);

	tabbedPane1.setSelectedIndex(0);

	/*** EvoKnowledge parameters panel ***/

	EvoKnowledgePanel = new JPanel();
	EvoKnowledgePanel.setLayout(null);
	tabbedPane.addTab("Safety levels", null, EvoKnowledgePanel, null);

	/* Load safety partitioning layer */

	String fileName = "src/main/resources/map/safety.layer";
	List<String> lines = Files.readAllLines(Paths.get(fileName));
	newLayer = new Layer<SafetyArea>("safety");
	for (String line : lines) {
	    String tokens[] = line.split(";;");

	    // Parse vertices.
	    String vertices[] = tokens[1].split(",");
	    List<Coordinate> polygon = new ArrayList<Coordinate>(
		    vertices.length);

	    for (String vertex : vertices) {
		String coord[] = vertex.split(" ");
		polygon.add(new Coordinate(Double.parseDouble(coord[0]), Double
			.parseDouble(coord[1])));
	    }
	    int safetyLevel = Integer.parseInt(tokens[2]);
	    SafetyArea newArea = new SafetyArea(tokens[0], polygon, safetyLevel);
	    // System.out.println("Adding new area " + newArea.getName());
	    newLayer.addArea(newArea);
	}

	/* Filtered alternatives panel */

	FilteredAlternativesPanel = new JPanel();
	tabbedPane.addTab("Filtered alternatives", null,
		FilteredAlternativesPanel, null);
	FilteredAlternativesPanel.setLayout(null);
	JLabel lblNewLabel1 = new JLabel("List of filtered alternatives");
	lblNewLabel1.setBounds(150, 60, 200, 22);
	FilteredAlternativesPanel.add(lblNewLabel1);
	matrix2 = new String[9][3];
	for (int i = 0; i < 9; i++) {
	    matrix2[i][0] = "";
	    matrix2[i][1] = "";
	    matrix2[i][2] = "";
	}
	tableModelFilt = new DefaultTableModel(matrix2, columnNames2);
	tableFilteredAlternatives = new JTable(tableModelFilt);
	JPanel FilteredPanel = new JPanel();
	FilteredPanel.setBounds(150, 90, 410, 180);
	FilteredPanel.setLayout(new BorderLayout());
	FilteredPanel.add(tableFilteredAlternatives.getTableHeader(),
		BorderLayout.PAGE_START);
	FilteredPanel.add(tableFilteredAlternatives, BorderLayout.CENTER);
	tableFilteredAlternatives.getColumnModel().getColumn(0)
		.setPreferredWidth(100);
	tableFilteredAlternatives.getColumnModel().getColumn(1)
		.setPreferredWidth(180);
	tableFilteredAlternatives.getColumnModel().getColumn(2)
		.setPreferredWidth(130);

	FilteredAlternativesPanel.add(FilteredPanel);
    }

    public void setData() throws IOException {

	/*** Set initial alternatives ***/

	String[] columnNames = { "Alternative Id", "Transportation modes" };
	table = new JTable();
	table.setBounds(41, 50, 425, 192);
	String[][] matrix = new String[origAlt.size()][2];
	for (int i = 0; i < origAlt.size(); i++) {
	    Alternative alt = origAlt.get(i);
	    matrix[i][0] = "Route " + String.valueOf(alt.getId());
	    matrix[i][1] = alt.getModes();
	}
	final DefaultTableModel tableModel = new DefaultTableModel(matrix,
		columnNames);
	tableAlternatives = new JTable(tableModel);
	tableAlternatives.setEnabled(false);

	JPanel routePanel = new JPanel();
	routePanel.setBounds(41, 50, 425, 192);
	routePanel.setLayout(new BorderLayout());
	routePanel.add(tableAlternatives.getTableHeader(),
		BorderLayout.PAGE_START);
	routePanel.add(tableAlternatives, BorderLayout.CENTER);
	tableAlternatives.getColumnModel().getColumn(0).setPreferredWidth(150);
	tableAlternatives.getColumnModel().getColumn(1).setPreferredWidth(150);

	AlternativesPanel.add(routePanel);

	if (userData.getPreferences().isTtCar())
	    carCheckBox.setSelected(true);
	else
	    carCheckBox.setSelected(false);
	if (userData.getPreferences().isTtFlexibus())
	    flexibusCheckBox.setSelected(true);
	else
	    flexibusCheckBox.setSelected(false);
	if (userData.getPreferences().isTtWalk())
	    walkCheckBox.setSelected(true);
	else
	    walkCheckBox.setSelected(false);
	if (userData.getPreferences().isTtCarpooling())
	    carpoolingCheckBox.setSelected(true);
	else
	    carpoolingCheckBox.setSelected(false);

	if (userData.getPreferences().isPmCash())
	    cashCheckBox.setSelected(true);
	else
	    cashCheckBox.setSelected(false);
	if (userData.getPreferences().isPmCreditCard())
	    creditCardCheckBox.setSelected(true);
	else
	    creditCardCheckBox.setSelected(false);
	if (userData.getPreferences().isPmPaypal())
	    payPalCheckBox.setSelected(true);
	else
	    payPalCheckBox.setSelected(false);
	if (userData.getPreferences().isPmServiceCard())
	    serviceCardCheckBox.setSelected(true);
	else
	    serviceCardCheckBox.setSelected(false);

	if (userData.getPreferences().isUnsafear()) {
	    checkboxUnsafe = new Checkbox("", true);
	    checkboxUnsafe.setBounds(207, 38, 29, 22);
	    SafetyPrefPanel.add(checkboxUnsafe);
	    checkboxUnsafe_ = new Checkbox("");
	    checkboxUnsafe_.setBounds(308, 38, 29, 22);
	    SafetyPrefPanel.add(checkboxUnsafe_);
	} else {
	    checkboxUnsafe = new Checkbox("");
	    checkboxUnsafe.setBounds(207, 38, 29, 22);
	    SafetyPrefPanel.add(checkboxUnsafe);
	    checkboxUnsafe_ = new Checkbox("", true);
	    checkboxUnsafe_.setBounds(308, 38, 29, 22);
	    SafetyPrefPanel.add(checkboxUnsafe_);
	}
	if (userData.getPreferences().isCrowdedar()) {
	    checkboxCrowded = new Checkbox("", true);
	    checkboxCrowded.setBounds(207, 68, 29, 22);
	    SafetyPrefPanel.add(checkboxCrowded);
	    checkboxCrowded_ = new Checkbox("");
	    checkboxCrowded_.setBounds(308, 68, 29, 22);
	    SafetyPrefPanel.add(checkboxCrowded_);
	} else {
	    checkboxCrowded = new Checkbox("");
	    checkboxCrowded.setBounds(207, 68, 29, 22);
	    SafetyPrefPanel.add(checkboxCrowded);
	    checkboxCrowded_ = new Checkbox("", true);
	    checkboxCrowded_.setBounds(308, 68, 29, 22);
	    SafetyPrefPanel.add(checkboxCrowded_);
	}
	if (userData.getPreferences().isPollutedar()) {
	    checkboxPolluted = new Checkbox("", true);
	    checkboxPolluted.setBounds(207, 99, 29, 22);
	    SafetyPrefPanel.add(checkboxPolluted);
	    checkboxPolluted_ = new Checkbox("");
	    checkboxPolluted_.setBounds(308, 99, 29, 22);
	    SafetyPrefPanel.add(checkboxPolluted_);
	} else {
	    checkboxPolluted = new Checkbox("");
	    checkboxPolluted.setBounds(207, 99, 29, 22);
	    SafetyPrefPanel.add(checkboxPolluted);
	    checkboxPolluted_ = new Checkbox("", true);
	    checkboxPolluted_.setBounds(308, 99, 29, 22);
	    SafetyPrefPanel.add(checkboxPolluted_);
	}
	if (userData.getPreferences().isUntrustedprov()) {
	    checkboxUntrustedprov = new Checkbox("", true);
	    checkboxUntrustedprov.setBounds(207, 127, 29, 22);
	    SafetyPrefPanel.add(checkboxUntrustedprov);
	    checkboxUntrustedprov_ = new Checkbox("");
	    checkboxUntrustedprov_.setBounds(308, 127, 29, 22);
	    SafetyPrefPanel.add(checkboxUntrustedprov_);
	} else {
	    checkboxUntrustedprov = new Checkbox("");
	    checkboxUntrustedprov.setBounds(207, 127, 29, 22);
	    SafetyPrefPanel.add(checkboxUntrustedprov);
	    checkboxUntrustedprov_ = new Checkbox("", true);
	    checkboxUntrustedprov_.setBounds(308, 127, 29, 22);
	    SafetyPrefPanel.add(checkboxUntrustedprov_);
	}

	sliderName = new JSlider();
	sliderName.setBounds(189, 47, 200, 26);
	sliderName
		.setValue((int) (userData.getPreferences().getNamesens() * 100));
	PrivacyPrefPanel.add(sliderName);
	sliderEmail = new JSlider();
	sliderEmail.setBounds(189, 77, 200, 26);
	sliderEmail
		.setValue((int) (userData.getPreferences().getEmailsens() * 100));
	PrivacyPrefPanel.add(sliderEmail);
	sliderPhone = new JSlider();
	sliderPhone.setBounds(189, 107, 200, 26);
	sliderPhone
		.setValue((int) (userData.getPreferences().getPhonesens() * 100));
	PrivacyPrefPanel.add(sliderPhone);
	sliderGPS = new JSlider();
	sliderGPS.setBounds(189, 137, 200, 26);
	sliderGPS
		.setValue((int) (userData.getPreferences().getGpssens() * 100));
	PrivacyPrefPanel.add(sliderGPS);

	/*** Set entity information ***/

	nameText = new JTextField(userData.getName());
	nameText.setBounds(124, 67, 150, 20);
	EntityInformationPanel.add(nameText);
	nameText.setColumns(10);
	modelTypeChoice = new Choice();
	modelTypeChoice.setBounds(124, 27, 150, 20);
	modelTypeChoice.add("Person");
	EntityInformationPanel.add(modelTypeChoice);

	/*** Set preferences ***/

	double noCmax = userData.getPreferences().getNoCmax();
	String noCmaxString = String.valueOf(noCmax);
	numberOfChangesField.setText(noCmaxString);
	double wMax = userData.getPreferences().getWmax();
	String wMaxString = String.valueOf(wMax);
	walkingDistanceField.setText(wMaxString);
	double cMax = userData.getPreferences().getCmax();
	String cMaxString = String.valueOf(cMax);
	maxCostField.setText(cMaxString);
	double tMax = userData.getPreferences().getTmax();
	String tMaxString = String.valueOf(tMax);
	maxTravelTimeField.setText(tMaxString);
	sliderTime
		.setValue((int) (userData.getPreferences().getTTweight() * 100));
	sliderCost
		.setValue((int) (userData.getPreferences().getCweight() * 100));
	sliderReliability.setValue((int) (userData.getPreferences()
		.getRCweight() * 100));
	sliderWalkingDistance.setValue((int) (userData.getPreferences()
		.getWDweight() * 100));
	sliderSafety
		.setValue((int) (userData.getPreferences().getUSPweight() * 100));
	sliderPrivacy
		.setValue((int) (userData.getPreferences().getWSDweight() * 100));
	sliderNumOfChanges.setValue((int) (userData.getPreferences()
		.getNCweight() * 100));

	/*** Set alternatives value in EvoKnowledge parameters panel ***/

	DefaultListModel<String> alternativeList = new DefaultListModel<String>();
	final JList list = new JList(alternativeList);
	list.setBounds(12, 335, 108, 182);
	list.setBorder(BorderFactory.createTitledBorder("Alternatives"));
	final List<Alternative> altList = new ArrayList<Alternative>();
	for (int i = 0; i < origAlt.size(); i++) {
	    Alternative curr_alt = origAlt.get(i);
	    altList.add(curr_alt);
	}
	for (int i = 0; i < altList.size(); i++) {
	    alternativeList.addElement("Route " + (i + 1));
	}
	EvoKnowledgePanel.add(list);
	final String[] columnNames1 = { "Transportation modes", "Safety level" };
	String[][] matrix1 = new String[origAlt.size()][2];
	for (int i = 0; i < origAlt.size(); i++) {
	    Alternative alt = origAlt.get(i);
	    matrix1[i][0] = alt.getModes();
	    matrix1[i][1] = "";
	}
	final DefaultTableModel tableModelSaf = new DefaultTableModel(matrix1,
		columnNames1);
	tableAlternatives = new JTable(tableModelSaf);
	tableAlternatives.setEnabled(false);
	SafetyPanel = new JPanel();
	SafetyPanel.setBounds(125, 341, 350, 176);
	SafetyPanel.setLayout(new BorderLayout());
	SafetyPanel.add(tableAlternatives.getTableHeader(),
		BorderLayout.PAGE_START);
	SafetyPanel.add(tableAlternatives, BorderLayout.CENTER);
	tableAlternatives.getColumnModel().getColumn(0).setPreferredWidth(170);
	tableAlternatives.getColumnModel().getColumn(1).setPreferredWidth(120);

	EvoKnowledgePanel.add(SafetyPanel);

	list.addListSelectionListener(new ListSelectionListener() {
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
		    int index = list.getSelectedIndex();
		    int safLevel = altList.get(index).calculateSafetyLevel(
			    newLayer);
		    if (safLevel != 0) {
			/* Set values in EvoKnowledge parameters panel */
			tableModelSaf.setValueAt(safLevel, index, 1);
			if (safLevel < 4) {
			    /* Set values in filtered alternatives panel */
			    tableModelFilt.setValueAt("Route " + (index + 1),
				    index, 0);
			    tableModelFilt.setValueAt(
				    tableModelSaf.getValueAt(index, 0), index,
				    1);
			    tableModelFilt.setValueAt(safLevel, index, 2);
			}
		    } else {
			/* Set values in EvoKnowledge parameters panel */
			tableModelSaf.setValueAt("-", index, 1);
			/* Set values in filtered alternatives panel */
			tableModelFilt.setValueAt("Route " + (index + 1),
				index, 0);
			tableModelFilt.setValueAt(
				tableModelSaf.getValueAt(index, 0), index, 1);
			tableModelFilt.setValueAt("-", index, 2);
		    }
		}
	    }
	});
	GroupLayout layout = new GroupLayout(EvoKnowledgePanel);
	EvoKnowledgePanel.setLayout(layout);
	JPanel safetyGraph = setupGraphPanel();
	layout.setAutoCreateGaps(true);
	layout.setAutoCreateContainerGaps(true);
	layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(
		safetyGraph));
	layout.setVerticalGroup(layout.createParallelGroup().addComponent(
		safetyGraph));

	JLabel lblNewLabel2 = new JLabel(
		"<html><body>Next step: Ranking<br><br>"
			+ "The following metrics will be calculated and provided to the Utility component:<br><br>"
			+ "1. #USP (Unsatisfied Safety Parameters)<br>"
			+ "2. WSD (Willingness to Share Data): "
			+ "<span>1/M &times &Sigma</span<sup>M</sup><sub>j=1</sub> s(a<sub>j</sub> , p) &times r<sub>j</sub><br>"
			+ "&nbsp&nbsp&nbsp&nbsp<b>a</b>, vector of privacy attributes;<br>"
			+ "&nbsp&nbsp&nbsp s(a<sub>j</sub> , p), user-specified level of sensitivity related to "
			+ "the jth attribute w.r.t. a provider p;<br>"
			+ "&nbsp&nbsp&nbsp <b>r</b>, mask vector, r<sub>j</sub>=1 if the jth attribute is requested by a provider, r<sub>j</sub>=0 otherwise. </body></html>");
	lblNewLabel2.setBounds(150, 220, 620, 302);
	FilteredAlternativesPanel.add(lblNewLabel2);
	/*
	 * JPanel CriteriaImpPanel = new JPanel();
	 * tabbedPane.addTab("Criteria importance", null, CriteriaImpPanel,
	 * null); CriteriaImpPanel.setLayout(null); JLabel lblNewLabel3 = new
	 * JLabel("<html><body>Our criteria:<br><br>" + "1. Utility<br>" +
	 * "2. USP<br>" + "3. WSD</body></html>"); lblNewLabel3.setBounds(150,
	 * 30, 200, 122); CriteriaImpPanel.add(lblNewLabel3);
	 * 
	 * JLabel lblNewLabel4 = new JLabel(
	 * "<html><body>Relations (example):<br><br>" +
	 * "Utility -----3-------------1/3----USP<br>" +
	 * "Utility----1/9--------------9----WSD<br>" +
	 * "WSD-------1--------------1----USP</body></html>");
	 * lblNewLabel4.setBounds(150, 130, 300, 162);
	 * CriteriaImpPanel.add(lblNewLabel4); final String[] columnNames3 = {
	 * "<html><body>Intensity of <br>relative importance</body></html>",
	 * "Definition" };
	 * 
	 * final Object[][] data3 = { { "1", "Equal importance" },
	 * 
	 * { "3", "Moderate importance" }, { "5", "Strong importance" }, { "7",
	 * "Very strong importance" }, { "9", "Extreme importance" }, {
	 * "2,4,6,8", "Intermediate values" }, { "1.1,1.2,1.3,...",
	 * "Very close importance" } }; final DefaultTableModel tableRelImp =
	 * new DefaultTableModel(data3, columnNames3); tableRelativeImportance =
	 * new JTable(tableRelImp); JPanel relImpTabPanel = new JPanel();
	 * relImpTabPanel.setBounds(460, 170, 300, 140);
	 * relImpTabPanel.setLayout(new BorderLayout());
	 * relImpTabPanel.add(tableRelativeImportance.getTableHeader(),
	 * BorderLayout.PAGE_START); relImpTabPanel.add(tableRelativeImportance,
	 * BorderLayout.CENTER);
	 * tableRelativeImportance.getColumnModel().getColumn(0)
	 * .setPreferredWidth(100);
	 * tableRelativeImportance.getColumnModel().getColumn(1)
	 * .setPreferredWidth(140); CriteriaImpPanel.add(relImpTabPanel); //
	 * FilteredAlternativesPanel.add(new //
	 * GraphicsView(ImageIO.read(getClass
	 * ().getResource("/images/WSD_formula.png")))); /*
	 * 
	 * final String[] columnNames2 = { "Alternative",
	 * "Transportation modes", "Safety level" };
	 * 
	 * final Object[][] data2 = { { "Alternative 2", modes2,
	 * altList.get(1).getSafetyCriticalityLevel() },
	 * 
	 * { "Alternative 4", modes4, altList.get(3).getSafetyCriticalityLevel()
	 * } };
	 * 
	 * final DefaultTableModel tableModelF = new DefaultTableModel(data2,
	 * columnNames2); tableFilteredAlternatives = new JTable(tableModelF);
	 * 
	 * JPanel filteredPanel = new JPanel(); filteredPanel.setBounds(150, 90,
	 * 400, 60); filteredPanel.setLayout(new BorderLayout());
	 * 
	 * filteredPanel.add(tableFilteredAlternatives.getTableHeader(),
	 * BorderLayout.PAGE_START);
	 * 
	 * filteredPanel.add(tableFilteredAlternatives, BorderLayout.CENTER);
	 * tableFilteredAlternatives
	 * .getColumnModel().getColumn(0).setPreferredWidth(100);
	 * tableFilteredAlternatives
	 * .getColumnModel().getColumn(1).setPreferredWidth(140);
	 * tableFilteredAlternatives
	 * .getColumnModel().getColumn(2).setPreferredWidth(130);
	 */
	// CriteriaImpPanel.add(filteredPanel);
	/*
	 * Button cancelButton = new Button("Cancel");
	 * cancelButton.addActionListener(new ActionListener() {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { } });
	 * cancelButton.setBounds(571, 516, 70, 22);
	 * mainPanel.add(cancelButton);
	 * 
	 * Button saveButton = new Button("Save"); saveButton.setBounds(476,
	 * 516, 70, 22); mainPanel.add(saveButton);
	 */

    }

    private JPanel setupGraphPanel() throws IOException {
	JPanel ret = new JPanel(new BorderLayout());
	ret.setBorder(BorderFactory
		.createTitledBorder("Partitioning of Trento area map according to safety"));
	ret.add(new GraphicsView(ImageIO.read(getClass().getResource(
		"/images/safety_partitioning.png"))));
	return ret;
    }

    public void setController(MainController controller) {
	this.controller = controller;
    }

    public void init() throws IOException {
	try {
	    loadRoutes();
	    if (userData != null && userData.getAlternatives() != null
		    && userData.getPreferences() != null) {
		setData();
	    }
	} catch (JAXBException e) {
	    logger.error(e.getMessage(), e);
	}
    }

    public void loadRoutes() throws JAXBException {
	userData = new UserData();
	userData = controller.getUserData(controller.getCurrentUser());
	if (userData != null && userData.getOriginalAlternatives() != null) {
	    origAlt = new ArrayList<Alternative>();
	    origAlt = userData.getOriginalAlternatives();
	}
    }

    public void setData(UserData ud) {
	userData = ud;
	try {
	    init();
	} catch (IOException e) {
	    logger.error(e.getMessage(), e);
	}
    }
}
