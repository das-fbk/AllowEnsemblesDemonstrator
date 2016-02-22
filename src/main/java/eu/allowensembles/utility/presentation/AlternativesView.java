package eu.allowensembles.utility.presentation;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jxmapviewer.JXMapViewer;

import eu.allowensembles.evoknowledge.controller.EvoKnowledgeCRF;
import eu.allowensembles.evoknowledge.presentation.EvoKnowledgeCRFView;
import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.presentation.main.action.listener.RobustnessButtonActionListener;
import eu.allowensembles.presentation.main.map.Routes;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.ResourceLoader;

public class AlternativesView extends JPanel {

    private static final long serialVersionUID = 1136611069545730820L;

    private JTable tableAlternatives;
    private JXMapViewer mapPanel;
    private File routesFile;
    private JAXBContext context;
    private Routes r;
    
    private static Comparator<Alternative> descUtility = new Comparator<Alternative>() {
        @Override
        public int compare(Alternative rt1, Alternative rt2){
            return (int) ((rt2.getId() - rt1.getId()) * 100);
        }
	};

    /**
     * @throws JAXBException
     * @throws FileNotFoundException
     * 
     */

    public AlternativesView(final List<Alternative> alt, MainWindow window)
	    throws JAXBException, FileNotFoundException {

	setLayout(null);
	DefaultListModel<String> alternativeList = new DefaultListModel<String>();

	final JList<String> list = new JList<String>(alternativeList);
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	list.setBounds(20, 26, 108, 308);
	list.setBorder(BorderFactory.createTitledBorder("Alternatives"));
	List<Alternative> alternatives = new ArrayList<Alternative>();
	for (Alternative a : alt) {
		 alternatives.add(a);
	}
	alternatives.sort(descUtility);
	for (int i=alternatives.size()-1;i>=0;i--) {
		 alternativeList.addElement("alternative " + alternatives.get(i).getId());
	}

	final String[] columnRoute = { "Route Characteristics", "Values" };
	final Object[][] dataRoute = { { "Total travel time", "" },
		{ "Total walking distance", "" }, { "Total cost", "" },
		{ "Number of changes", "" }, { "Modes of Transportation" } };
	final DefaultTableModel tableModel = new DefaultTableModel(dataRoute,
		columnRoute);
	tableAlternatives = new JTable(tableModel);
	tableAlternatives.setEnabled(false);

	list.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
		    int index = list.getSelectedIndex();
		    tableModel.setValueAt(alt.get(index).getTravelTime(), 0, 1);
		    tableModel.setValueAt(alt.get(index).getWalkingDistance(),
			    1, 1);
		    tableModel.setValueAt(alt.get(index).getCost(), 2, 1);
		    tableModel
			    .setValueAt(alt.get(index).getNoOfChanges(), 3, 1);
		    tableModel.setValueAt(alt.get(index).getModes(), 4, 1);

		    routesFile = ResourceLoader.getRouteFile();
		    try {
			context = JAXBContext.newInstance(Routes.class);
			r = new Routes();
			r = (Routes) context.createUnmarshaller().unmarshal(
				routesFile);

			mapPanel = window.displayRouteOnMap(
				r.getRoute().get(index).getLeg(), Color.green);
			mapPanel.setBounds(500, 11, 300, 421);
			mapPanel.setLayout(null);
			add(mapPanel);
			mapPanel.repaint();

		    } catch (JAXBException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		    }
		}

	    }
	});

	JPanel routePanel = new JPanel();
	routePanel.setBounds(170, 26, 300, 150);
	routePanel.setLayout(new BorderLayout());
	routePanel.add(tableAlternatives.getTableHeader(),
		BorderLayout.PAGE_START);
	routePanel.add(tableAlternatives, BorderLayout.CENTER);
	tableAlternatives.getColumnModel().getColumn(0).setPreferredWidth(150);
	tableAlternatives.getColumnModel().getColumn(1).setPreferredWidth(150);

	Button robustnessButton = new Button("Robustness View");
	robustnessButton.setActionCommand(RobustnessButtonActionListener.OPEN);
	robustnessButton.addActionListener(new RobustnessButtonActionListener(
		window));
	robustnessButton.setBounds(180, 200, 150, 22);
	add(robustnessButton);

	Button evoKnowledgeButton = new Button("EvoKnowledge View");
	evoKnowledgeButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		try {
		    EvoKnowledgeCRF model = new EvoKnowledgeCRF();
		    EvoKnowledgeCRFView view;
		    int index = 0;
		    if (!list.isSelectionEmpty())
			index = list.getSelectedIndex();
		    view = new EvoKnowledgeCRFView(model, alt.get(index));
		    view.setVisible(true);
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	});

	evoKnowledgeButton.setBounds(180, 250, 150, 22);
	if (alternativeList.isEmpty())
	    evoKnowledgeButton.setEnabled(false);
	else
	    evoKnowledgeButton.setEnabled(true);

	add(evoKnowledgeButton);

	add(list);
	add(routePanel);

    }
}
