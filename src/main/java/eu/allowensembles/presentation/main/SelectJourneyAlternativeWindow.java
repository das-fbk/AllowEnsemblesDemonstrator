package eu.allowensembles.presentation.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.controller.events.SelectJourneyEvent;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.UserData;

public class SelectJourneyAlternativeWindow extends JPanel {

    private static final long serialVersionUID = 1135109067926912831L;

    private static final Logger logger = LogManager
	    .getLogger(SelectJourneyAlternativeWindow.class);

    private JFrame frame;
    private JTable table;

    private MainWindow window;

    private String currentName;

    /**
     * Create the panel.
     * 
     * @param mainWindow
     * 
     * @param frame
     */
    public SelectJourneyAlternativeWindow(MainWindow mainWindow, JFrame frame) {
	this.window = mainWindow;
	this.frame = frame;
	setLayout(null);

	JLabel lblDescription = new JLabel(
		"Please select a route. Here are optimized and ranked solutions based on user preference");
	lblDescription.setBounds(10, 10, 577, 23);
	add(lblDescription);

	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setBounds(10, 44, 645, 307);
	add(scrollPane);

	table = new JTable();
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setBounds(20, 44, 635, 302);

	table.getSelectionModel().addListSelectionListener(
		new SelectRowAlternativeListener(window));
	scrollPane.setViewportView(table);

	JButton btnSave = new JButton("Select");
	btnSave.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		int r = table.getSelectedRow();
		if (r != -1) {
		    MainController.post(new SelectJourneyEvent(r));
		    frame.setVisible(false);
		}
	    }
	});
	btnSave.setBounds(566, 367, 89, 23);
	add(btnSave);
    }

    public void setData(UserData ud) {
	try {
	    if (ud == null) {
		logger.warn("Cannot display null journey alternatives ");
		return;
	    }
	    // convert for display
	    String[][] matrix = new String[ud.getAlternatives().size()][7];
	    for (int i = 0; i < ud.getAlternatives().size(); i++) {
		Alternative alt = ud.getAlternatives().get(i);
		matrix[i][0] = String.valueOf(alt.getId());
		matrix[i][1] = String.valueOf(alt.getCost());
		matrix[i][2] = String.valueOf(alt.getTravelTime());
		matrix[i][3] = String.valueOf(alt.getWalkingDistance());
		matrix[i][4] = alt.getModes();
		matrix[i][5] = String.valueOf(alt.getNoOfChanges());
		matrix[i][6] = String.valueOf(alt.getUtility());
	    }
	    this.currentName = ud.getName();
	    table.setModel(new DefaultTableModel(matrix, new String[] { "Id",
		    "Cost", "Travel time", "Walking distance", "Tran. modes",
		    "Number of changes", "Utility" }));
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
    }

    private class SelectRowAlternativeListener implements ListSelectionListener {

	private MainWindow window;

	public SelectRowAlternativeListener(MainWindow window) {
	    this.window = window;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
	    int selectedRow = table.getSelectedRow();
	    if (selectedRow >= 0 && currentName != null) {
		System.out.println("Selected: " + selectedRow);
		// get selected route using index
		UserData ud = window.getController().getUserData(currentName);
		if (ud != null && ud.getAlternatives() != null
			&& !ud.getAlternatives().isEmpty()
			&& selectedRow < ud.getAlternatives().size()) {
		    Alternative alt = ud.getAlternatives().get(selectedRow);
		    window.displayRouteOnMapWithLegColors(alt.getLegs());
		}
	    }
	}
    }
}
