package eu.allowensembles.utility.presentation;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

import eu.allowensembles.controller.MainController;
import eu.allowensembles.utility.controller.Preferences;
import eu.allowensembles.utility.controller.Utility;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.UserData;


public class RankAlternativesView extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4805451254510960735L;
	
	private JTable rankedAlternativesTable;
	private JComboBox<String> comboBoxTime, comboBoxCost, comboBoxReliability, comboBoxWalking,comboBoxSecurity,
		comboBoxPrivacy, comboBoxNoC;
	private List<Alternative> rankedList;
	private Preferences preferences = new Preferences();
	private List <Alternative> alternatives = new ArrayList<Alternative>();
	private Utility utility;
	
	public RankAlternativesView(UserData userData, Utility u, MainController controller){
		
		if(userData!=null){
			preferences = userData.getPreferences();
			alternatives = userData.getAlternatives();
		}
		utility = u;
		Label label = new Label("Utility component - Type of function");
		label.setBounds(24, 25, 200, 20);
		add(label);
		
		Label labelTime = new Label("Travel time");
		
		labelTime.setBounds(24, 45, 110, 20);
		add(labelTime);
		comboBoxTime = new JComboBox<String>();
		comboBoxTime.addItem("exp(0.5)");
		comboBoxTime.addItem("exp(0.6)");
		comboBoxTime.addItem("exp(0.7)");
		comboBoxTime.addItem("exp(0.8)");
		comboBoxTime.addItem("exp(0.9)");
		comboBoxTime.addItem("exp(1)");
		comboBoxTime.setBounds(135,45,110,22);
		add(comboBoxTime);
		
		Label labelCost = new Label("Cost");
		labelCost.setBounds(24, 65, 110, 20);
		add(labelCost);
		comboBoxCost = new JComboBox<String>();
		comboBoxCost.addItem("exp(0.5)");
		comboBoxCost.addItem("exp(0.6)");
		comboBoxCost.addItem("exp(0.7)");
		comboBoxCost.addItem("exp(0.8)");
		comboBoxCost.addItem("exp(0.9)");
		comboBoxCost.addItem("exp(1)");
		comboBoxCost.setBounds(135,65,110,22);
		add(comboBoxCost);
		
		Label labelReliability = new Label("Reliability");
		labelReliability.setBounds(24, 85, 110, 20);
		add(labelReliability);
		comboBoxReliability = new JComboBox<String>();
		comboBoxReliability.addItem("exp(0.01)");
		comboBoxReliability.addItem("exp(0.02)");
		comboBoxReliability.addItem("exp(0.03)");
		comboBoxReliability.addItem("exp(0.04)");
		comboBoxReliability.addItem("exp(0.05)");
		comboBoxReliability.addItem("exp(0.06)");
		comboBoxReliability.addItem("exp(0.07)");
		comboBoxReliability.addItem("exp(0.08)");
		comboBoxReliability.addItem("exp(0.09)");
		comboBoxReliability.setBounds(135,85,110,22);
		add(comboBoxReliability);
		
		Label labelWalking = new Label("Walking distance");
		labelWalking.setBounds(24, 105, 110, 20);
		add(labelWalking);
		comboBoxWalking = new JComboBox<String>();
		comboBoxWalking.addItem("exp(0.1)");
		comboBoxWalking.addItem("exp(0.2)");
		comboBoxWalking.addItem("exp(0.3)");
		comboBoxWalking.addItem("exp(0.4)");
		comboBoxWalking.addItem("exp(0.5)");
		comboBoxWalking.addItem("exp(0.6)");
		comboBoxWalking.addItem("exp(0.7)");
		comboBoxWalking.addItem("exp(0.8)");
		comboBoxWalking.addItem("exp(0.9)");
		comboBoxWalking.setBounds(135,105,110,22);
		add(comboBoxWalking);
		
		Label labelSecurity = new Label("Security");
		labelSecurity.setBounds(24, 125, 110, 20);
		add(labelSecurity);
		comboBoxSecurity = new JComboBox<String>();
		comboBoxSecurity.addItem("polynomial(2)");
		comboBoxSecurity.addItem("polynomial(3)");
		comboBoxSecurity.addItem("polynomial(4)");
		comboBoxSecurity.setBounds(135,125,110,22);
		add(comboBoxSecurity);
		
		Label labelPrivacy = new Label("Privacy");
		labelPrivacy.setBounds(24, 145, 110, 20);
		add(labelPrivacy);
		comboBoxPrivacy = new JComboBox<String>();
		comboBoxPrivacy.addItem("polynomial(2)");
		comboBoxPrivacy.addItem("polynomial(3)");
		comboBoxPrivacy.addItem("polynomial(4)");
		comboBoxPrivacy.setBounds(135,145,110,22);
		add(comboBoxPrivacy);
		
		Label labelNumberOfChanges = new Label("Number of changes");
		labelNumberOfChanges.setBounds(24, 165, 110, 20);
		add(labelNumberOfChanges);
		comboBoxNoC = new JComboBox<String>();
		comboBoxNoC.addItem("linear");
		comboBoxNoC.setBounds(135,165,110,22);
		add(comboBoxNoC);
		
		String[] rankedColNames = {"Trip Solution","Utility value"};
		Object[][] rankedRowData = new Object[alternatives.size()][2];

		rankedList = u.rankAlternatives(userData,controller);
		rankedAlternativesTable = new JTable(rankedRowData,rankedColNames);
		rankedAlternativesTable.setEnabled(false);
		JPanel rankedPanel = new JPanel();
		rankedPanel.setBounds(360, 28, 200, 200);
		
		rankedPanel.setLayout(new BorderLayout());
		rankedPanel.add(rankedAlternativesTable.getTableHeader(), BorderLayout.PAGE_START);
		rankedPanel.add(rankedAlternativesTable, BorderLayout.CENTER);
		
		rankedAlternativesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		rankedAlternativesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		add(rankedPanel);

		Button calculateUtilityButton = new Button("Calculate Utility");
		calculateUtilityButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	utility.setTimeConst(0.1*comboBoxTime.getSelectedIndex()+0.5);
	    		utility.setCostConst(0.1*comboBoxCost.getSelectedIndex()+0.5);
	    		utility.setReliabilityConst(0.01*comboBoxReliability.getSelectedIndex()+0.01);
	    		utility.setWalkConst(0.1*comboBoxWalking.getSelectedIndex()+0.1);
	    		utility.setSecurityConst(1.0*comboBoxSecurity.getSelectedIndex()+2.0);
	    		utility.setPrivacyConst(1.0*comboBoxPrivacy.getSelectedIndex()+2.0);
	    		utility.setNoCConst(1.0*comboBoxNoC.getSelectedIndex()+1.0);
	    		rankedList = utility.rankAlternatives(userData, controller); 
		    	for(int i=0;i<rankedList.size();i++){
		    /*		Utility u = new Utility((0.1*comboBoxTime.getSelectedIndex()+0.5),(0.1*comboBoxCost.getSelectedIndex()+0.5), 
		    				(0.01*comboBoxReliability.getSelectedIndex()+0.01),(0.1*comboBoxWalking.getSelectedIndex()+0.1),
		    				(1.0*comboBoxSecurity.getSelectedIndex()+2.0), (1.0*comboBoxPrivacy.getSelectedIndex()+2.0),
		    				(1.0*comboBoxNoC.getSelectedIndex()+1.0));*/
		    		rankedAlternativesTable.setValueAt("Alternative "+rankedList.get(i).getId(), i, 0);
		    		rankedAlternativesTable.setValueAt(String.format("%.12f", rankedList.get(i).getUtility()), i, 1);
			    	
		    	}
		    }
		});
		
		calculateUtilityButton.setBounds(180, 210, 150, 22);
		add(calculateUtilityButton);
		
		JPanel graphPanel = new GraphRepresentationView(u, preferences);
		graphPanel.setBounds(20, 250, 550, 220);
		graphPanel.setLayout(new BorderLayout());
		graphPanel.setBorder(BorderFactory.createTitledBorder("Graph representation"));
		add(graphPanel);
	}
}