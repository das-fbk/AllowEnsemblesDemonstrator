package eu.allowensembles.presentation.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

public class AdaptationProblemWindow {

    private JFrame frame;
    private JTable table;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    AdaptationProblemWindow window = new AdaptationProblemWindow();
		    window.frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the application.
     */
    public AdaptationProblemWindow() {
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
	frame = new JFrame();
	frame.setBounds(100, 100, 1024, 650);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JPanel adaptationPanel = new JPanel();
	adaptationPanel.setSize(new Dimension(1024, 650));
	adaptationPanel.setBounds(new Rectangle(0, 0, 1024, 650));
	frame.getContentPane().add(adaptationPanel, BorderLayout.CENTER);
	adaptationPanel.setLayout(null);

	JLabel lblSelectedEnsembleDetails = new JLabel(
		"Selected ensemble details");
	lblSelectedEnsembleDetails.setBounds(10, 21, 223, 14);
	adaptationPanel.add(lblSelectedEnsembleDetails);

	JList list = new JList();
	list.setModel(new AbstractListModel() {
	    String[] values = new String[] { "Name:", "Type:", "Status:" };

	    @Override
	    public int getSize() {
		return values.length;
	    }

	    @Override
	    public Object getElementAt(int index) {
		return values[index];
	    }
	});
	list.setBounds(10, 56, 223, 78);
	adaptationPanel.add(list);

	JLabel lblRoleInstances = new JLabel("Role instances");
	lblRoleInstances.setBounds(10, 159, 123, 14);
	adaptationPanel.add(lblRoleInstances);

	JList list_1 = new JList();
	list_1.setModel(new AbstractListModel() {
	    String[] values = new String[] { "Passenger", "   P1", "   P2",
		    "   P3", "Driver", "   D1" };

	    @Override
	    public int getSize() {
		return values.length;
	    }

	    @Override
	    public Object getElementAt(int index) {
		return values[index];
	    }
	});
	list_1.setBounds(10, 184, 223, 397);
	adaptationPanel.add(list_1);

	JLabel lblAdaptationProblem = new JLabel("Adaptation problem");
	lblAdaptationProblem.setBounds(435, 21, 232, 14);
	adaptationPanel.add(lblAdaptationProblem);

	table = new JTable();
	table.setBounds(270, 57, 710, 77);
	adaptationPanel.add(table);

	JLabel lblRoleFragments = new JLabel("Role fragments");
	lblRoleFragments.setBounds(332, 159, 129, 14);
	adaptationPanel.add(lblRoleFragments);

	JList list_2 = new JList();
	list_2.setModel(new AbstractListModel() {
	    String[] values = new String[] { "Walk pickup point" };

	    @Override
	    public int getSize() {
		return values.length;
	    }

	    @Override
	    public Object getElementAt(int index) {
		return values[index];
	    }
	});
	list_2.setBounds(270, 184, 232, 189);
	adaptationPanel.add(list_2);

	JLabel lblIssues = new JLabel("Issues");
	lblIssues.setBounds(332, 396, 81, 14);
	adaptationPanel.add(lblIssues);

	JList list_3 = new JList();
	list_3.setModel(new AbstractListModel() {
	    String[] values = new String[] { "None" };

	    @Override
	    public int getSize() {
		return values.length;
	    }

	    @Override
	    public Object getElementAt(int index) {
		return values[index];
	    }
	});
	list_3.setBounds(270, 429, 232, 152);
	adaptationPanel.add(list_3);

	JPanel panel = new JPanel();
	panel.setBorder(new LineBorder(new Color(0, 0, 0)));
	panel.setBounds(525, 180, 455, 193);
	adaptationPanel.add(panel);

	JLabel lblSolvers = new JLabel("Solvers");
	lblSolvers.setBounds(580, 396, 46, 14);
	adaptationPanel.add(lblSolvers);

	JList list_4 = new JList();
	list_4.setModel(new AbstractListModel() {
	    String[] values = new String[] { "Issue (c) -> fragment" };

	    @Override
	    public int getSize() {
		return values.length;
	    }

	    @Override
	    public Object getElementAt(int index) {
		return values[index];
	    }
	});
	list_4.setBounds(525, 429, 455, 152);
	adaptationPanel.add(list_4);
    }
}
