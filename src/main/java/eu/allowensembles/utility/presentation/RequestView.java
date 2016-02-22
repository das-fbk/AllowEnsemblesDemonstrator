package eu.allowensembles.utility.presentation;

import java.awt.BorderLayout;
import java.awt.Label;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import eu.allowensembles.utility.controller.Preferences;

public class RequestView extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3903234037202671309L;
	private JTable table, tableWeights;
	
	public RequestView(Preferences pref){
		
		setLayout(null);
		
		double sum = pref.getTTweight()
			    + pref.getCweight()
			    + pref.getNCweight()
			    + pref.getRCweight()
			    + pref.getUSPweight()
			    + pref.getWSDweight()
			    + pref.getWDweight();
	//	if(sum>1.0){
	    	pref.setTmax(pref.getTmax());
			pref.setTTweight(pref.getTTweight() / sum);
			pref.setTTweight(Math.round(pref.getTTweight()*100.0)/100.0);		
			pref.setCmax(pref.getCmax());
			pref.setCweight(pref.getCweight() / sum);
			pref.setCweight(Math.round(pref.getCweight()*100.0)/100.0);	
			pref.setNCweight(pref.getNCweight() / sum);
			pref.setNCweight(Math.round(pref.getNCweight()*100.0)/100.0);	
			pref.setNoCmax((int) pref.getNoCmax());
			pref.setRCweight(pref.getRCweight() / sum);
			pref.setRCweight(Math.round(pref.getRCweight()*100.0)/100.0);	
			pref.setUSPweight(pref.getUSPweight() / sum);
			pref.setUSPweight(Math.round(pref.getUSPweight()*100.0)/100.0);	
			pref.setWSDweight(pref.getWSDweight() / sum);
			pref.setWSDweight(Math.round(pref.getWSDweight()*100.0)/100.0);	
			pref.setWmax(pref.getWmax());
			pref.setWDweight(pref.getWDweight() / sum);
			pref.setWDweight(Math.round(pref.getWDweight()*100.0)/100.0);	
	//	}

		Label label = new Label("Utility component receives by demonstrator a list of preferences and weights of a request as input");
		label.setBounds(24, 25, 550, 22);
		add(label);
		String[] columnNames = {"Preference", "Value"};

		Object[][] data = {{"Maximum travel time (minutes)", pref.getTmax()},
				{"Maximum cost (euros)", pref.getCmax()},
				{"Maximum walking distance (meters)", pref.getWmax()},
				{"Maximum number of changes", pref.getNoCmax()}};
		
		table = new JTable(data,columnNames);
		table.setEnabled(false);

		JPanel prefPanel = new JPanel();
		prefPanel.setBounds(41, 68, 300, 150);
		
		prefPanel.setLayout(new BorderLayout());
		prefPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		prefPanel.add(table, BorderLayout.CENTER);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(230);
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		add(prefPanel);
		String[] columnNames2 = {"Weight", "Value"};
		Object[][] data2 = {{"Time", pref.getTTweight()},
		{"Cost", pref.getCweight()},
		{"Reliability", pref.getRCweight()},
		{"Walking distance", pref.getWDweight()},
		{"Security", pref.getUSPweight()},
		{"Privacy", pref.getWSDweight()},
		{"Number of changes", pref.getNCweight()}};

		tableWeights = new JTable(data2,columnNames2);
		tableWeights.setEnabled(false);
		
		JPanel weightPanel = new JPanel();
		weightPanel.setBounds(380, 68, 200, 150);
		
		weightPanel.setLayout(new BorderLayout());
		weightPanel.add(tableWeights.getTableHeader(), BorderLayout.PAGE_START);
		weightPanel.add(tableWeights, BorderLayout.CENTER);
		
		tableWeights.getColumnModel().getColumn(0).setPreferredWidth(130);
		tableWeights.getColumnModel().getColumn(1).setPreferredWidth(70);
		add(weightPanel);
		

	}
	
}