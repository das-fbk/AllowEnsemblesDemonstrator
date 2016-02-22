package eu.allowensembles.presentation.main;

import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.presentation.main.action.SavePreferenceAction;
import eu.allowensembles.utility.controller.Preferences;
import eu.allowensembles.utils.DoiBean;
import eu.allowensembles.utils.UserData;

public class AddNewEntityWindow extends JPanel {

    private static final long serialVersionUID = -747733756975664043L;

    private static final Logger logger = LogManager
	    .getLogger(AddNewEntityWindow.class);

    private static final int SLIDER_MAXIMUM = 100;
    private static final int SLIDER_MINIMUM = 0;

    private JTextField nameText;
    private JTextField numberOfChangesField;
    private JTextField walkingDistanceField;
    private JTextField maxCostField;
    private JTextField maxTravelTimeField;

    private Choice modelTypeChoice;

    private JCheckBox carCheckBox;

    private JCheckBox flexibusCheckbox;

    private JCheckBox walkCheckbox;

    private JCheckBox carpoolingCheckBox;

    // private Choice robustnessChoice;

    private JCheckBox cashCheckBox;

    private JCheckBox creditCardCheckBox;

    private JCheckBox payPalCheckBox;

    private JCheckBox serviceCardCheckBox;

    private JSlider sliderTime;

    private JSlider sliderCost;

    private JSlider sliderReliability;

    private JSlider sliderWalkingDistance;

    private JSlider sliderSecurity;

    private JSlider sliderPrivacy;

    private JSlider sliderNumOfChanges;

    private Checkbox avoidUnsafeAreaCheckBox;

    private MainController controller;

    private MainWindow window;

    private JSlider sliderSensitivityName;

    private JSlider sliderSensitivityEmail;

    private JSlider sliderSensitivityPhone;

    private JSlider sliderSensitivityGpsLocation;

    private static int nameCounter = 100;

    public AddNewEntityWindow(MainWindow mainWindow) {
	setOpaque(true);
	setLayout(null);

	this.window = mainWindow;

	JPanel mainPanel = new JPanel();
	mainPanel.setLocation(0, 0);
	mainPanel.setSize(530, 600);
	// contentPane.add(mainPanel);
	add(mainPanel);
	mainPanel.setLayout(null);

	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	tabbedPane.setBounds(0, 0, 520, 500);
	mainPanel.add(tabbedPane);

	JPanel entityInformationPanel = new JPanel();
	tabbedPane.addTab("Entity informations", null, entityInformationPanel,
		null);
	entityInformationPanel.setLayout(null);

	Label label = new Label("Model type");
	label.setBounds(24, 25, 90, 22);
	entityInformationPanel.add(label);

	modelTypeChoice = new Choice();
	modelTypeChoice.setBounds(124, 27, 150, 20);
	modelTypeChoice.add("Person");
	modelTypeChoice.setEnabled(false);
	entityInformationPanel.add(modelTypeChoice);

	JLabel lblName = new JLabel("Name");
	lblName.setBounds(24, 66, 90, 22);
	entityInformationPanel.add(lblName);

	nameText = new JTextField("Max Plank");
	nameText.setEnabled(false);
	nameText.setEditable(false);
	nameText.setBounds(124, 67, 150, 20);
	nameText.setColumns(10);
	nameText.requestFocusInWindow();
	entityInformationPanel.add(nameText);

	JPanel entityPreferencesPanel = new JPanel();
	tabbedPane.addTab("Options", null, entityPreferencesPanel, null);
	entityPreferencesPanel.setLayout(null);

	carCheckBox = new JCheckBox("Car");
	carCheckBox.setBounds(10, 41, 97, 23);
	entityPreferencesPanel.add(carCheckBox);

	JLabel lblTransportationType = new JLabel("Transportation type");
	lblTransportationType.setBounds(10, 11, 200, 23);
	entityPreferencesPanel.add(lblTransportationType);

	flexibusCheckbox = new JCheckBox("Flexibus");
	flexibusCheckbox.setBounds(10, 67, 97, 23);
	entityPreferencesPanel.add(flexibusCheckbox);

	walkCheckbox = new JCheckBox("Walk");
	walkCheckbox.setBounds(10, 93, 97, 23);
	entityPreferencesPanel.add(walkCheckbox);

	numberOfChangesField = new JTextField();
	numberOfChangesField.setBounds(308, 42, 44, 20);
	entityPreferencesPanel.add(numberOfChangesField);
	numberOfChangesField.setColumns(10);

	JLabel lblNumverOfChanges = new JLabel("Number of changes");
	lblNumverOfChanges.setBounds(184, 42, 114, 20);
	entityPreferencesPanel.add(lblNumverOfChanges);

	JLabel lblWalkingDistance = new JLabel("Walking distance");
	lblWalkingDistance.setBounds(184, 70, 114, 20);
	entityPreferencesPanel.add(lblWalkingDistance);

	walkingDistanceField = new JTextField();
	walkingDistanceField.setColumns(10);
	walkingDistanceField.setBounds(308, 70, 44, 20);
	entityPreferencesPanel.add(walkingDistanceField);

	JLabel lblMaxCost = new JLabel("Max cost");
	lblMaxCost.setBounds(184, 101, 114, 20);
	entityPreferencesPanel.add(lblMaxCost);

	maxCostField = new JTextField();
	maxCostField.setColumns(10);
	maxCostField.setBounds(308, 101, 44, 20);
	entityPreferencesPanel.add(maxCostField);

	JLabel lblMaxTravelTime = new JLabel("Max travel time");
	lblMaxTravelTime.setBounds(184, 132, 114, 20);
	entityPreferencesPanel.add(lblMaxTravelTime);

	maxTravelTimeField = new JTextField();
	maxTravelTimeField.setColumns(10);
	maxTravelTimeField.setBounds(308, 132, 44, 20);
	entityPreferencesPanel.add(maxTravelTimeField);

	carpoolingCheckBox = new JCheckBox("Carpooling");
	carpoolingCheckBox.setBounds(10, 119, 97, 23);
	entityPreferencesPanel.add(carpoolingCheckBox);

	JLabel lblUtilityPreferences = new JLabel("Utility preferences");
	lblUtilityPreferences.setBounds(184, 11, 168, 23);
	entityPreferencesPanel.add(lblUtilityPreferences);

	// robustnessChoice = new Choice();
	// robustnessChoice.setBounds(308, 163, 134, 20);
	// robustnessChoice.add("low");
	// robustnessChoice.add("medium");
	// robustnessChoice.add("high");
	//
	// entityPreferencesPanel.add(robustnessChoice);

	// JLabel lblRobustness = new JLabel("Robustness level");
	// lblRobustness.setBounds(184, 163, 97, 23);
	// entityPreferencesPanel.add(lblRobustness);

	JLabel lblPaymentMethod = new JLabel("Payment method");
	lblPaymentMethod.setBounds(10, 215, 114, 23);
	entityPreferencesPanel.add(lblPaymentMethod);

	cashCheckBox = new JCheckBox("Cash");
	cashCheckBox.setSelected(true);
	cashCheckBox.setBounds(10, 243, 97, 23);
	entityPreferencesPanel.add(cashCheckBox);

	creditCardCheckBox = new JCheckBox("Credit card");
	creditCardCheckBox.setSelected(true);
	creditCardCheckBox.setBounds(10, 273, 97, 23);
	entityPreferencesPanel.add(creditCardCheckBox);

	payPalCheckBox = new JCheckBox("Paypal");
	payPalCheckBox.setSelected(true);
	payPalCheckBox.setBounds(10, 303, 97, 23);
	entityPreferencesPanel.add(payPalCheckBox);

	serviceCardCheckBox = new JCheckBox("Smart card");
	serviceCardCheckBox.setSelected(true);
	serviceCardCheckBox.setBounds(10, 333, 97, 23);
	entityPreferencesPanel.add(serviceCardCheckBox);

	JLabel lblUserWeights = new JLabel("User weights");
	lblUserWeights.setBounds(184, 218, 97, 16);
	entityPreferencesPanel.add(lblUserWeights);

	JLabel lblTime = new JLabel("Time");
	lblTime.setBounds(184, 333, 70, 16);
	entityPreferencesPanel.add(lblTime);

	sliderTime = new JSlider();
	sliderTime.setBounds(308, 333, 168, 16);
	sliderTime.setMaximum(SLIDER_MAXIMUM);
	sliderTime.setMinimum(SLIDER_MINIMUM);
	entityPreferencesPanel.add(sliderTime);

	JLabel lblCost = new JLabel("Cost");
	lblCost.setBounds(184, 303, 70, 16);
	entityPreferencesPanel.add(lblCost);

	sliderCost = new JSlider();
	sliderCost.setBounds(308, 303, 168, 16);
	sliderCost.setMaximum(SLIDER_MAXIMUM);
	sliderCost.setMinimum(SLIDER_MINIMUM);
	entityPreferencesPanel.add(sliderCost);

	JLabel lblReliability = new JLabel("Reliability");
	lblReliability.setBounds(184, 361, 70, 16);
	entityPreferencesPanel.add(lblReliability);

	sliderReliability = new JSlider();
	sliderReliability.setBounds(308, 361, 168, 16);
	sliderReliability.setMaximum(SLIDER_MAXIMUM);
	sliderReliability.setMinimum(SLIDER_MINIMUM);
	entityPreferencesPanel.add(sliderReliability);

	JLabel lblWalkingdistance = new JLabel("Walking distance");
	lblWalkingdistance.setBounds(184, 269, 114, 30);
	entityPreferencesPanel.add(lblWalkingdistance);

	sliderWalkingDistance = new JSlider();
	sliderWalkingDistance.setBounds(308, 273, 168, 16);
	sliderWalkingDistance.setMaximum(SLIDER_MAXIMUM);
	sliderWalkingDistance.setMinimum(SLIDER_MINIMUM);
	entityPreferencesPanel.add(sliderWalkingDistance);

	JLabel lblSecurity = new JLabel("Security");
	lblSecurity.setBounds(184, 385, 70, 30);
	entityPreferencesPanel.add(lblSecurity);

	sliderSecurity = new JSlider();
	sliderSecurity.setBounds(308, 389, 168, 16);
	entityPreferencesPanel.add(sliderSecurity);

	JLabel lblPrivacy = new JLabel("Privacy");
	lblPrivacy.setBounds(184, 416, 70, 30);
	entityPreferencesPanel.add(lblPrivacy);

	sliderPrivacy = new JSlider();
	sliderPrivacy.setBounds(308, 417, 168, 16);
	sliderPrivacy.setMaximum(SLIDER_MAXIMUM);
	sliderPrivacy.setMinimum(SLIDER_MINIMUM);
	entityPreferencesPanel.add(sliderPrivacy);

	JLabel lblNumOfChanges = new JLabel("Num of changes");
	lblNumOfChanges.setBounds(184, 243, 114, 30);
	entityPreferencesPanel.add(lblNumOfChanges);

	sliderNumOfChanges = new JSlider();
	sliderNumOfChanges.setBounds(308, 250, 168, 16);
	sliderNumOfChanges.setMaximum(SLIDER_MAXIMUM);
	sliderNumOfChanges.setMinimum(SLIDER_MINIMUM);
	entityPreferencesPanel.add(sliderNumOfChanges);

	JPanel entityPrivacyandSecurityPanel = new JPanel();
	tabbedPane.addTab("Privacy and security", null,
		entityPrivacyandSecurityPanel, null);
	entityPrivacyandSecurityPanel.setLayout(null);

	JLabel lblNewLabel = new JLabel("Safety preferences");
	lblNewLabel.setBounds(10, 10, 200, 22);
	entityPrivacyandSecurityPanel.add(lblNewLabel);

	JPanel panel = new JPanel();
	panel.setBorder(new LineBorder(new Color(0, 0, 0)));
	panel.setBounds(20, 43, 422, 170);
	entityPrivacyandSecurityPanel.add(panel);
	panel.setLayout(null);

	JLabel lblAvoidUnsafeAreas = new JLabel("Avoid unsafe areas");
	lblAvoidUnsafeAreas.setBounds(10, 38, 172, 22);
	panel.add(lblAvoidUnsafeAreas);

	avoidUnsafeAreaCheckBox = new Checkbox("");
	avoidUnsafeAreaCheckBox.setBounds(207, 38, 29, 22);
	panel.add(avoidUnsafeAreaCheckBox);

	JLabel lblStrictly = new JLabel("Strictly");
	lblStrictly.setBounds(195, 11, 63, 22);
	panel.add(lblStrictly);

	JLabel lblFlexible = new JLabel("Flexible");
	lblFlexible.setBounds(298, 11, 63, 22);
	panel.add(lblFlexible);

	Checkbox checkbox_1 = new Checkbox("");
	checkbox_1.setBounds(308, 38, 29, 22);
	panel.add(checkbox_1);

	JLabel lblAvoidCrowdedAreas = new JLabel("Avoid crowded areas");
	lblAvoidCrowdedAreas.setBounds(10, 66, 172, 22);
	panel.add(lblAvoidCrowdedAreas);

	JLabel lblAvoidPollutedAreas = new JLabel("Avoid polluted areas");
	lblAvoidPollutedAreas.setBounds(10, 99, 172, 22);
	panel.add(lblAvoidPollutedAreas);

	JLabel lblAvoidUntrustedAreas = new JLabel("Avoid untrusted areas");
	lblAvoidUntrustedAreas.setBounds(10, 127, 172, 22);
	panel.add(lblAvoidUntrustedAreas);

	Checkbox checkbox_2 = new Checkbox("");
	checkbox_2.setBounds(207, 66, 29, 22);
	panel.add(checkbox_2);

	Checkbox checkbox_3 = new Checkbox("");
	checkbox_3.setBounds(207, 99, 29, 22);
	panel.add(checkbox_3);

	Checkbox checkbox_4 = new Checkbox("");
	checkbox_4.setBounds(207, 127, 29, 22);
	panel.add(checkbox_4);

	Checkbox checkbox_5 = new Checkbox("");
	checkbox_5.setBounds(308, 66, 29, 22);
	panel.add(checkbox_5);

	Checkbox checkbox_6 = new Checkbox("");
	checkbox_6.setBounds(308, 99, 29, 22);
	panel.add(checkbox_6);

	Checkbox checkbox_7 = new Checkbox("");
	checkbox_7.setBounds(308, 127, 29, 22);
	panel.add(checkbox_7);

	JPanel panel_1 = new JPanel();
	panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
	panel_1.setBounds(20, 257, 422, 214);
	entityPrivacyandSecurityPanel.add(panel_1);
	panel_1.setLayout(null);

	sliderSensitivityName = new JSlider();
	sliderSensitivityName.setBounds(189, 64, 200, 26);
	sliderSensitivityName.setMaximum(SLIDER_MAXIMUM);
	sliderSensitivityName.setMinimum(SLIDER_MINIMUM);
	panel_1.add(sliderSensitivityName);

	JLabel lblName_1 = new JLabel("Name");
	lblName_1.setBounds(10, 64, 114, 22);
	panel_1.add(lblName_1);

	JLabel lblEmail = new JLabel("Email");
	lblEmail.setBounds(10, 97, 114, 22);
	panel_1.add(lblEmail);

	JLabel lblPhone = new JLabel("Phone");
	lblPhone.setBounds(10, 140, 114, 22);
	panel_1.add(lblPhone);

	JLabel lblGpsLocation = new JLabel("GPS location");
	lblGpsLocation.setBounds(10, 181, 114, 22);
	panel_1.add(lblGpsLocation);

	sliderSensitivityEmail = new JSlider();
	sliderSensitivityEmail.setBounds(189, 101, 200, 26);
	sliderSensitivityEmail.setMaximum(SLIDER_MAXIMUM);
	sliderSensitivityEmail.setMinimum(SLIDER_MINIMUM);
	panel_1.add(sliderSensitivityEmail);

	sliderSensitivityPhone = new JSlider();
	sliderSensitivityPhone.setBounds(189, 140, 200, 26);
	sliderSensitivityPhone.setMaximum(SLIDER_MAXIMUM);
	sliderSensitivityPhone.setMinimum(SLIDER_MINIMUM);
	panel_1.add(sliderSensitivityPhone);

	sliderSensitivityGpsLocation = new JSlider();
	sliderSensitivityGpsLocation.setBounds(189, 177, 200, 26);
	sliderSensitivityGpsLocation.setMaximum(SLIDER_MAXIMUM);
	sliderSensitivityGpsLocation.setMinimum(SLIDER_MINIMUM);
	panel_1.add(sliderSensitivityGpsLocation);

	JLabel lblNotSensitive = new JLabel("not sensitive");
	lblNotSensitive.setBounds(171, 11, 122, 26);
	panel_1.add(lblNotSensitive);

	JLabel lblDoNotShare = new JLabel("do not share");
	lblDoNotShare.setBounds(319, 11, 91, 26);
	panel_1.add(lblDoNotShare);

	JLabel lblSensitivityOfData = new JLabel(
		"Sensitivity of data attributes");
	lblSensitivityOfData.setBounds(10, 224, 200, 22);
	entityPrivacyandSecurityPanel.add(lblSensitivityOfData);

	JButton cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		window.displayEntityWindow(false, null, false);
	    }
	});
	cancelButton.setBounds(371, 516, 80, 22);
	mainPanel.add(cancelButton);

	JButton saveButton = new JButton("Save");
	saveButton.addActionListener(new SavePreferenceAction(this));
	saveButton.setBounds(276, 516, 70, 22);
	mainPanel.add(saveButton);
    }

    public double getCmax() {
	String t = maxCostField.getText();
	if (t != null && !t.isEmpty()) {
	    try {
		return Double.valueOf(t);
	    } catch (NumberFormatException nfe) {
		logger.error(nfe.getMessage(), nfe);
	    }
	}
	return 0;
    }

    public double getCweight() {
	return Double.valueOf(sliderCost.getValue()) / 100;
    }

    public double getNCweight() {
	return Double.valueOf(sliderNumOfChanges.getValue()) / 100;
    }

    public int getNoCmax() {
	String t = numberOfChangesField.getText();
	if (t != null && !t.isEmpty()) {
	    try {
		return Integer.valueOf(t);
	    } catch (NumberFormatException nfe) {
		logger.error(nfe.getMessage(), nfe);
	    }
	}
	return 0;
    }

    public double getRCweight() {
	return Double.valueOf(sliderReliability.getValue()) / 100;
    }

    public long getTmax() {
	String t = maxTravelTimeField.getText();
	if (t != null && !t.isEmpty()) {
	    try {
		return Integer.valueOf(t);
	    } catch (NumberFormatException nfe) {
		logger.error(nfe.getMessage(), nfe);
	    }
	}
	return 0;
    }

    public double getTTweight() {
	return Double.valueOf(sliderTime.getValue()) / 100;
    }

    public double getUSPweight() {
	return Double.valueOf(sliderSecurity.getValue()) / 100;
    }

    public double getWDweight() {
	return Double.valueOf(sliderWalkingDistance.getValue()) / 100;
    }

    public double getWmax() {
	String t = walkingDistanceField.getText();
	if (t != null && !t.isEmpty()) {
	    try {
		return Double.valueOf(t);
	    } catch (NumberFormatException nfe) {
		logger.error(nfe.getMessage(), nfe);
	    }
	}
	return 0;
    }

    public double getWSDweight() {
	return Double.valueOf(sliderWalkingDistance.getValue()) / 100;
    }

    public boolean getTtCar() {
	return carCheckBox.isSelected();
    }

    public boolean getTtFlexibus() {
	return flexibusCheckbox.isSelected();
    }

    public boolean getTtWalk() {
	return walkCheckbox.isSelected();
    }

    public boolean getTtCarpooling() {
	return carpoolingCheckBox.isSelected();
    }

    public boolean getPmCash() {
	return cashCheckBox.isSelected();
    }

    public boolean getPmCreditCard() {
	return creditCardCheckBox.isSelected();
    }

    public boolean getPmPaypal() {
	return payPalCheckBox.isSelected();
    }

    public void setController(MainController controller) {
	this.controller = controller;
    }

    public void setPreferences(Preferences preferences) {
	// transportation type
	carCheckBox.setSelected(preferences.isTtCar());
	flexibusCheckbox.setSelected(preferences.isTtFlexibus());
	walkCheckbox.setSelected(preferences.isTtWalk());
	carpoolingCheckBox.setSelected(preferences.isTtCarpooling());
	// payment method
	cashCheckBox.setSelected(preferences.isPmCash());
	creditCardCheckBox.setSelected(preferences.isPmCreditCard());
	payPalCheckBox.setSelected(preferences.isPmPaypal());
	serviceCardCheckBox.setSelected(preferences.isPmServiceCard());
	// utility preferences
	numberOfChangesField.setText(String.valueOf(preferences.getNoCmax()));
	walkingDistanceField.setText(String.valueOf(preferences.getWmax()));
	maxCostField.setText(String.valueOf(preferences.getCmax()));
	maxTravelTimeField.setText(String.valueOf(preferences.getTmax()));

	// user weights
	sliderTime.setValue((int) (100 * preferences.getTTweight()));
	sliderCost.setValue((int) (100 * preferences.getCweight()));
	sliderReliability.setValue((int) (100 * preferences.getRCweight()));
	sliderWalkingDistance.setValue((int) (100 * preferences.getWDweight()));
	sliderNumOfChanges.setValue((int) (100 * preferences.getNCweight()));

    }

    public void displayWindow(boolean visible, boolean editable) {
	window.displayEntityWindow(visible, null, editable);
    }

    public void save(String userName, Preferences userPreference) {
	if (userPreference == null) {
	    return;
	}
	DoiBean db = controller.getCurrentDoiBean();
	if (db != null && db.getName() != null && db.getName().equals(userName)) {
	    // existing user
	    UserData ud = controller.getUserData(db.getName());
	    if (ud != null) {
		ud.setPreferences(userPreference);
	    }
	    return;
	} else {
	    // new user
	    controller.buildUserDoiBean(userName, userPreference);
	    return;
	}
    }

    public boolean getPmServiceCard() {
	return serviceCardCheckBox.isSelected();
    }

    public double getNamesens() {
	return sliderSensitivityName.getValue() / 100d;
    }

    public double getEmailsens() {
	return sliderSensitivityEmail.getValue() / 100d;
    }

    public double getPhonesens() {
	return sliderSensitivityPhone.getValue() / 100d;
    }

    public double getGpssens() {
	return sliderSensitivityGpsLocation.getValue() / 100d;
    }

    public void setUsername(String name) {
	nameText.setText(name);
    }

    public void reset() {
	nameCounter++;
	nameText.setText("User_" + nameCounter);
	nameText.setEditable(false);
	setPreferences(new Preferences());
    }

    public String getUserName() {
	return nameText.getText();
    }

    public void setEditable(boolean editable) {
	nameText.setEditable(editable);
    }
}
