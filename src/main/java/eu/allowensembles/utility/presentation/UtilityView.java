package eu.allowensembles.utility.presentation;

import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.presentation.main.events.UpdateUserPreferenceEvent;
import eu.allowensembles.utility.controller.Preferences;
import eu.allowensembles.utility.controller.Utility;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.UserData;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

public class UtilityView extends JPanel {

    private static final long serialVersionUID = -3065754932068313302L;

    private static final Logger logger = LogManager.getLogger(MainWindow.class);

    private static JFrame frame;

    private Utility u;
    private Preferences pref = new Preferences();
    private List<Alternative> alt = new ArrayList<Alternative>();
    private JTabbedPane tabbedPane;
    private JPanel requestPanel;
    private JPanel alternativesPanel;
    private JPanel rankedAlternativesPanel;
    private MainWindow window;

    private MainController controller;
    ProcessDiagram current;
    UserData userData;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		try {
		    frame = new JFrame();
		    frame.setTitle("Utility");
		    frame.setType(Type.UTILITY);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setSize(867, 570);
		    frame.setContentPane(new UtilityView(null));
		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public UtilityView(MainWindow window) throws JAXBException,
	    URISyntaxException, IOException {
	setOpaque(true);
	setLayout(null);

	this.window = window;
	JPanel mainPanel = new JPanel();
	mainPanel.setSize(867, 594);
	add(mainPanel);
	mainPanel.setLayout(null);

	u = new Utility();

	tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	// an example how to get current process
	tabbedPane.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent e) {
		if (controller != null) {
		    current = controller.getCurrentProcess();
		    u.setProcessDiagram(current);
	//	    if (current != null) {
	//		System.out.println();
	//	    }
		}
	    }
	});
	tabbedPane.setBounds(0, 0, 841, 510);
	mainPanel.add(tabbedPane);

	requestPanel = new RequestView(pref);
	tabbedPane.insertTab("Request", null, requestPanel, null, 0);

	alternativesPanel = new AlternativesView(alt, window);
	tabbedPane.insertTab("Alternatives", null, alternativesPanel, null, 1);

	//rankedAlternativesPanel = new RankAlternativesView(userData, u, controller);
	tabbedPane.insertTab("Ranked alternatives", null, rankedAlternativesPanel, null, 2);
	//rankedAlternativesPanel.setLayout(null);

	Button cancelButton = new Button("Cancel");
	cancelButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	    }
	});

    }

    public void loadRoutes() throws JAXBException {

	userData = new UserData();
	userData = controller.getUserData(controller.getCurrentUser());
	alt = new ArrayList<Alternative>();
	alt = userData.getAlternatives();

    }

    class EventBusChangeRecorder {
	@Subscribe
	public void preferencesChange(UpdateUserPreferenceEvent event)
		throws IOException, JAXBException {

		double sum = event.userPreference.getTTweight()
		    + event.userPreference.getCweight()
		    + event.userPreference.getNCweight()
		    + event.userPreference.getRCweight()
		    + event.userPreference.getUSPweight()
		    + event.userPreference.getWSDweight()
		    + event.userPreference.getWDweight();
	    pref.setTmax(pref.getTmax());
	    pref.setTTweight(pref.getTTweight() / sum);
	    pref.setTTweight(Math.round(pref.getTTweight() * 100.0) / 100.0);
	    pref.setCmax(pref.getCmax());
	    pref.setCweight(pref.getCweight() / sum);
	    pref.setCweight(Math.round(pref.getCweight() * 100.0) / 100.0);
	    pref.setNCweight(pref.getNCweight() / sum);
	    pref.setNCweight(Math.round(pref.getNCweight() * 100.0) / 100.0);
	    pref.setNoCmax(pref.getNoCmax());
	    pref.setRCweight(pref.getRCweight() / sum);
	    pref.setRCweight(Math.round(pref.getRCweight() * 100.0) / 100.0);
	    pref.setUSPweight(pref.getUSPweight() / sum);
	    pref.setUSPweight(Math.round(pref.getUSPweight() * 100.0) / 100.0);
	    pref.setWSDweight(pref.getWSDweight() / sum);
	    pref.setWSDweight(Math.round(pref.getWSDweight() * 100.0) / 100.0);
	    pref.setWmax(pref.getWmax());
	    pref.setWDweight(pref.getWDweight() / sum);
	    pref.setWDweight(Math.round(pref.getWDweight() * 100.0) / 100.0);

	    tabbedPane.removeAll();
	    requestPanel = new RequestView(pref);
	    tabbedPane.insertTab("Request", null, requestPanel, null, 0);
	    
	    alternativesPanel = new AlternativesView(alt, window);
	    tabbedPane.insertTab("Alternatives", null, alternativesPanel, null, 1);
	    userData = controller.getUserData(controller.getCurrentUser());
	    rankedAlternativesPanel = new RankAlternativesView(userData, u, controller);
	    tabbedPane.insertTab("Ranked alternatives", null,rankedAlternativesPanel, null, 2);
	    rankedAlternativesPanel.setLayout(null);
	    
	}
    }

    public void setController(MainController controller) {
	this.controller = controller;
    }

    public void init() {
	MainController.register(new EventBusChangeRecorder());
	try {
	    loadRoutes();
	} catch (JAXBException e) {
	    logger.error(e.getMessage(), e);
	}

    }

    public void setData(UserData ud) {
	userData = ud;
	 if (userData != null && userData.getAlternatives() != null
			    && userData.getPreferences() != null) {
	
		alt = ud.getAlternatives();
		pref = ud.getPreferences();
		double sum = pref.getTTweight() + pref.getCweight()
			+ pref.getNCweight() + pref.getRCweight() + pref.getUSPweight()
			+ pref.getWSDweight() + pref.getWDweight();
		pref.setTmax(pref.getTmax());
		pref.setTTweight(pref.getTTweight() / sum);
		pref.setCmax(pref.getCmax());
		pref.setCweight(pref.getCweight() / sum);
		pref.setNCweight(pref.getNCweight() / sum);
		pref.setNoCmax(pref.getNoCmax());
		pref.setRCweight(pref.getRCweight() / sum);
		pref.setUSPweight(pref.getUSPweight() / sum);
		pref.setWSDweight(pref.getWSDweight() / sum);
		pref.setWmax(pref.getWmax());
		pref.setWDweight(pref.getWDweight() / sum);
		tabbedPane.removeAll();
		requestPanel = new RequestView(pref);
		tabbedPane.insertTab("Request", null, requestPanel, null, 0);
	
		try {
		    alternativesPanel = new AlternativesView(alt, window);
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (JAXBException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		tabbedPane.insertTab("Alternatives", null, alternativesPanel, null, 1);
	
		rankedAlternativesPanel = new RankAlternativesView(userData, u,
			controller);
		tabbedPane.insertTab("Ranked alternatives", null,
			rankedAlternativesPanel, null, 2);
		rankedAlternativesPanel.setLayout(null);
	 }
    }

}
