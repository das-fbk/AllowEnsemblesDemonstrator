package eu.allowensembles.presentation.main;

import java.awt.Color;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class AdaptationPanel extends JPanel {

    private static final long serialVersionUID = -678781782553883964L;
    private JTable table;

    public AdaptationPanel() {
	// setSize(new Dimension(1024, 850));
	// setBounds(new Rectangle(0, 0, 1024, 850));
	// setLayout(null);

	JLabel lblSelectedEnsembleDetails = new JLabel(
		"Selected ensemble details");
	lblSelectedEnsembleDetails.setBounds(10, 21, 223, 14);
	add(lblSelectedEnsembleDetails);

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
	list.setBounds(10, 56, 223, 251);
	add(list);

	JLabel lblRoleInstances = new JLabel("Role instances");
	lblRoleInstances.setBounds(10, 331, 123, 14);
	add(lblRoleInstances);

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
	list_1.setBounds(10, 369, 223, 449);
	add(list_1);

	JLabel lblAdaptationProblem = new JLabel("Issues");
	lblAdaptationProblem.setBounds(435, 21, 232, 14);
	add(lblAdaptationProblem);

	table = new JTable();
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	table.setModel(new DefaultTableModel(new Object[][] {
		{ "1", "Adaptation problem 1" },
		{ "2", "Adaptation problem 2" }, }, new String[] { "Id",
		"Description" }));
	table.getColumnModel().getColumn(0).setResizable(false);
	table.getColumnModel().getColumn(1).setPreferredWidth(175);
	table.setBounds(270, 57, 710, 251);
	add(table);

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
	list_2.setBounds(270, 369, 232, 251);
	add(list_2);

	JLabel lblIssues = new JLabel("Issues");
	lblIssues.setBounds(270, 641, 81, 14);
	add(lblIssues);

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
	list_3.setBounds(270, 675, 232, 143);
	add(list_3);

	JPanel panel = new JPanel();
	panel.setBorder(new LineBorder(new Color(0, 0, 0)));
	panel.setBounds(525, 369, 455, 251);
	add(panel);

	JLabel lblSolvers = new JLabel("Solvers");
	lblSolvers.setBounds(525, 641, 46, 14);
	add(lblSolvers);

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
	list_4.setBounds(525, 675, 455, 143);
	add(list_4);

	JLabel lblRoleFragment = new JLabel("Role fragment");
	lblRoleFragment.setBounds(270, 331, 134, 14);
	add(lblRoleFragment);
    }

}
