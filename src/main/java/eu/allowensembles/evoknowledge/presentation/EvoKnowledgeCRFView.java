package eu.allowensembles.evoknowledge.presentation;

import static eu.allowensembles.DemonstratorConstant.STORYBOARD1_FOLDER;
import static eu.allowensembles.DemonstratorConstant.STORYBOARD1_MAIN_XML;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import eu.allowensembles.evoknowledge.controller.Context;
import eu.allowensembles.evoknowledge.controller.Context.TimeOfDay;
import eu.allowensembles.evoknowledge.controller.Context.Weather;
import eu.allowensembles.evoknowledge.controller.Context.Weekday;
import eu.allowensembles.evoknowledge.controller.EvoKnowledgeCRF;
import eu.allowensembles.evoknowledge.controller.IEvoKnowledgeCRFModel;
import eu.allowensembles.evoknowledge.controller.Segment;
import eu.allowensembles.presentation.main.map.Routes;
import eu.allowensembles.presentation.main.map.Routes.Route.Leg;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.ResourceLoader;

/**
 * Graphical user interface of EvoKnowledge component for Allow Ensembles
 * demonstrator. The window displays the given input alternative as well the
 * output alternative with corrected travel parameters. Additionally, the CRF
 * model (the street graph) and the individual segments (i.e. the mapping of the
 * alternative trace to the map) are shown which are the basis for prediction.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class EvoKnowledgeCRFView extends JFrame {
    // Serialization Id.
    private static final long serialVersionUID = -4911313980694978755L;

    // Images of the alternatives to display.
    private static Map<Integer, BufferedImage> alternativesImages;

    static {
	URL resource = ResourceLoader.class.getResource("/evo/images");
	try {
	    Path file = Paths.get(resource.toURI());
	    alternativesImages = loadAlternativeImages(file);

	} catch (URISyntaxException e) {
	    e.printStackTrace();

	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    private static Map<Integer, BufferedImage> loadAlternativeImages(Path path)
	    throws IOException {
	HashMap<Integer, BufferedImage> ret = new HashMap<Integer, BufferedImage>();

	for (int i = 0; i < 9; i++) {
	    Path imagePath = path.resolve((i + 1) + ".png");
	    BufferedImage temp = ImageIO.read(imagePath.toFile());
	    ret.put((i + 1), temp);
	}
	return ret;
    }

    // Underlying EvoKnowlegde CRF model.
    private IEvoKnowledgeCRFModel model;
    private Context context;

    // Estimated input alternative shown in the GUI.
    private Alternative alternative;

    // Corrected output alternative shown in the GUI.
    private Alternative predictedAlternative;

    // List of map matched segments.
    private List<Segment> alternativeSegments;
    private List<Segment> predictedSegments;

    // Panels of GUI.
    private JPanel main;
    private JPanel pInputAlternative;
    private JTree inputTree;

    private JPanel pOutputAlternative;
    private JTree outputTree;

    private JPanel pSegments;
    private JTree segmentTree;

    private JPanel pContext;
    private JPanel pGraph;

    /**
     * Constructor.
     * 
     * @param model
     *            EvoKnowledge model instance.
     * @param alternative
     *            Alternative to display.
     * 
     * @throws IOException
     */
    public EvoKnowledgeCRFView(EvoKnowledgeCRF model, Alternative alternative)
	    throws IOException {
	this.model = model;
	this.context = new Context();
	context.setTimeOfDay(TimeOfDay.Afternoon);
	context.setWeekday(Weekday.Tuesday);
	this.alternative = alternative;
	alternativeSegments = model.getSegments(alternative);
	predictedAlternative = model.getPredictedAlternative(alternative,
		context);
	predictedSegments = model.getSegments(predictedAlternative);
	initializeGUI();
    }

    private void initializeGUI() throws IOException {
	main = new JPanel();

	GroupLayout layout = new GroupLayout(main);
	main.setLayout(layout);
	pInputAlternative = createInputAlternativePanel();
	pInputAlternative.setPreferredSize(new Dimension(230, 350));
	pOutputAlternative = createOutputAlternativePanel();
	pSegments = createSegmentsPanel();
	pContext = createContextPanel();
	pGraph = setupGraphPanel();

	layout.setAutoCreateGaps(true);
	layout.setAutoCreateContainerGaps(true);

	ParallelGroup g = layout
		.createParallelGroup(GroupLayout.Alignment.LEADING);
	g.addComponent(pInputAlternative, GroupLayout.DEFAULT_SIZE,
		GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
	g.addComponent(pOutputAlternative, GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

	ParallelGroup g2 = layout
		.createParallelGroup(GroupLayout.Alignment.LEADING);
	g2.addComponent(pContext, GroupLayout.DEFAULT_SIZE,
		GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
	g2.addComponent(pSegments, GroupLayout.PREFERRED_SIZE,
		GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

	layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(g)
		.addComponent(pGraph).addGroup(g2));

	layout.setVerticalGroup(layout
		.createParallelGroup()
		.addGroup(
			layout.createSequentialGroup()
				.addComponent(pInputAlternative)
				.addComponent(pOutputAlternative))
		.addComponent(pGraph)
		.addGroup(
			layout.createSequentialGroup().addComponent(pContext)
				.addComponent(pSegments)));
	layout.linkSize(SwingConstants.HORIZONTAL, pInputAlternative, pSegments);
	layout.linkSize(SwingConstants.HORIZONTAL, pInputAlternative,
		pOutputAlternative);
	layout.linkSize(SwingConstants.HORIZONTAL, pSegments, pContext);

	// Setup JFrame.
	setContentPane(main);

	// Create EvoKnowledge panel.
	setTitle("Allow Ensembles Demonstrator");
	setSize(1200, 670);
	setLocationRelativeTo(null);
	setResizable(false);
    }

    private void createJTreeFromSegments(JTree tree) {
	DefaultMutableTreeNode top = new DefaultMutableTreeNode("Legs");

	int legId = 0;
	String mode = "";
	int n = 0;
	DefaultMutableTreeNode node = null;
	for (int i = 0; i < alternativeSegments.size(); i++) {
	    Segment s = alternativeSegments.get(i);
	    Segment ps = predictedSegments.get(i);

	    if (!mode.equals(s.getMode())) {

		if (node != null) {
		    top.add(node);
		}
		mode = s.getMode();
		node = new DefaultMutableTreeNode("Leg " + legId);
		legId++;
	    }
	    DefaultMutableTreeNode sNode = new DefaultMutableTreeNode(
		    "Segment " + n++);
	    sNode.add(new DefaultMutableTreeNode("tt: " + s.getTravelTime()));
	    sNode.add(new DefaultMutableTreeNode("Corrected tt: "
		    + ps.getTravelTime()));
	    sNode.add(new DefaultMutableTreeNode("c: " + s.getCosts()));
	    sNode.add(new DefaultMutableTreeNode("Corrected c: "
		    + ps.getCosts()));
	    node.add(sNode);
	}
	top.add(node);
	DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
	model.setRoot(top);
	model.reload();
    }

    private JPanel createSegmentsPanel() {
	JPanel ret = new JPanel(new BorderLayout());
	ret.setBorder(BorderFactory
		.createTitledBorder("EvoKnowledge model segments"));
	segmentTree = new JTree();
	DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) segmentTree
		.getCellRenderer();
	renderer.setLeafIcon(null);
	renderer.setClosedIcon(null);
	renderer.setOpenIcon(null);
	createJTreeFromSegments(segmentTree);
	JScrollPane scrollPanel = new JScrollPane(segmentTree);
	ret.add(scrollPanel);
	return ret;
    }

    private static JTree createJTreeFromAlternative(Alternative alt, JTree tree) {
	DefaultMutableTreeNode top = new DefaultMutableTreeNode(
		"Alternative Id: " + alt.getId());
	top.add(new DefaultMutableTreeNode("Duration: " + alt.getTravelTime()));
	top.add(new DefaultMutableTreeNode("Costs: " + alt.getCost()));
	top.add(new DefaultMutableTreeNode("Walking distance: "
		+ alt.getWalkingDistance()));

	DefaultMutableTreeNode legs = new DefaultMutableTreeNode("Legs");
	top.add(legs);

	for (int i = 0; i < alt.getLegs().size(); i++) {
	    Leg l = alt.getLegs().get(i);
	    DefaultMutableTreeNode legNode = new DefaultMutableTreeNode("Leg "
		    + i);
	    legNode.add(new DefaultMutableTreeNode("Duration: "
		    + l.getDuration()));
	    legNode.add(new DefaultMutableTreeNode("Costs: " + l.getCost()));
	    legNode.add(new DefaultMutableTreeNode("Mode: "
		    + l.getTransportType().getType()));
	    legNode.add(new DefaultMutableTreeNode("Walking distance: "
		    + l.getWalkingDistance()));
	    legs.add(legNode);
	}

	DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
	model.setRoot(top);
	model.reload();
	return tree;
    }

    private void update() {
	predictedAlternative = model.getPredictedAlternative(alternative,
		context);
	createJTreeFromAlternative(predictedAlternative, outputTree);

	predictedSegments = model.getSegments(predictedAlternative);
	createJTreeFromSegments(segmentTree);

	// Container c = pGraph;
	// BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(),
	// BufferedImage.TYPE_INT_ARGB);
	// c.paint(im.getGraphics());
	// try {
	// ImageIO.write(im, "PNG", new File("/Users/Andi/Desktop/shot.png"));
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
    }

    private JPanel createContextPanel() {
	JPanel ret = new JPanel();
	GroupLayout layout = new GroupLayout(ret);
	ret.setLayout(layout);
	ret.setBorder(BorderFactory.createTitledBorder("Context"));

	// Weekday.
	JLabel lDay = new JLabel("Weekday:");
	JComboBox<Weekday> cDay = new JComboBox<Weekday>(
		Context.WEEKDAYS.clone());
	cDay.setSelectedItem(context.getWeekday());
	cDay.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(ItemEvent event) {

		if (event.getStateChange() == ItemEvent.SELECTED) {
		    Weekday item = (Weekday) event.getItem();
		    context.setWeekday(item);
		    update();
		}
	    }
	});

	// Time of day.
	JLabel lTime = new JLabel("Time of day:");
	JComboBox<TimeOfDay> cTime = new JComboBox<TimeOfDay>(
		Context.TIME_OF_DAY.clone());
	cTime.setSelectedItem(context.getTimeOfDay());
	cTime.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(ItemEvent event) {

		if (event.getStateChange() == ItemEvent.SELECTED) {
		    TimeOfDay item = (TimeOfDay) event.getItem();
		    context.setTimeOfDay(item);
		    update();
		}

	    }
	});

	// Weather.
	JLabel lWeather = new JLabel("Weather:");
	JComboBox<Weather> cWeather = new JComboBox<Weather>(
		Context.WEATHER.clone());
	cWeather.setSelectedItem(context.getWeather());
	cWeather.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(ItemEvent event) {

		if (event.getStateChange() == ItemEvent.SELECTED) {
		    Weather item = (Weather) event.getItem();
		    context.setWeather(item);
		    update();
		}
	    }
	});

	layout.setHorizontalGroup(layout.createParallelGroup()
		.addComponent(lDay).addComponent(cDay).addComponent(lTime)
		.addComponent(cTime).addComponent(lWeather)
		.addComponent(cWeather));

	layout.setVerticalGroup(layout.createSequentialGroup()
		.addComponent(lDay).addComponent(cDay).addComponent(lTime)
		.addComponent(cTime).addComponent(lWeather)
		.addComponent(cWeather));
	return ret;
    }

    private JPanel createInputAlternativePanel() {
	JPanel ret = new JPanel(new BorderLayout());
	ret.setBorder(BorderFactory.createTitledBorder("Input Itinerary"));
	Color color = UIManager.getColor("Panel.background");
	inputTree = new JTree();
	DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) inputTree
		.getCellRenderer();
	renderer.setLeafIcon(null);
	renderer.setClosedIcon(null);
	renderer.setOpenIcon(null);
	createJTreeFromAlternative(alternative, inputTree);
	JScrollPane scrollPanel = new JScrollPane(inputTree);
	scrollPanel.setBackground(color);
	ret.add(scrollPanel);
	return ret;
    }

    private JPanel createOutputAlternativePanel() {
	JPanel ret = new JPanel(new BorderLayout());
	ret.setBorder(BorderFactory.createTitledBorder("Corrected Itinerary"));
	Color color = UIManager.getColor("Panel.background");
	outputTree = new JTree();
	DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) outputTree
		.getCellRenderer();
	renderer.setLeafIcon(null);
	renderer.setClosedIcon(null);
	renderer.setOpenIcon(null);
	createJTreeFromAlternative(predictedAlternative, outputTree);
	JScrollPane scrollPanel = new JScrollPane(outputTree);
	scrollPanel.setBackground(color);
	ret.add(scrollPanel);
	return ret;
    }

    private JPanel setupGraphPanel() throws IOException {
	JPanel ret = new JPanel();
	// ret.setBorder(BorderFactory.createTitledBorder("EvoKnowledge Model"));
	BufferedImage preview = alternativesImages.get(alternative.getId());
	GraphicsView w = new GraphicsView(preview);
	ret.add(w);

	return ret;
	/*
	 * if (model == null) return new JPanel();
	 * 
	 * Graph m = model.getModel(); m.addAttribute("ui.quality");
	 * m.addAttribute("ui.antialias"); String css =
	 * getClass().getClassLoader().getResource("evo/format.css")
	 * .getFile().toString(); m.addAttribute("ui.stylesheet",
	 * "url('file:///" + css + "')");
	 * 
	 * for (Edge e : m.getEdgeSet()) { e.removeAttribute("ui.class");
	 * e.setAttribute("ui.class", "notin"); }
	 * 
	 * for (Segment s : alternativeSegments) { Edge e =
	 * m.getEdge(s.getLabel());
	 * 
	 * if (e == null) { System.out.println("Not found " + s.getLabel());
	 * continue; }
	 * 
	 * if (s.getMode().equals("car") || s.getMode().equals("carsharing") ||
	 * s.getMode().equals("carSharing")) { e.addAttribute("ui.class",
	 * "car");
	 * 
	 * } else if (s.getMode().equals("bicycle")) {
	 * e.addAttribute("ui.class", "bicycle");
	 * 
	 * } else if (s.getMode().equals("bus")) { e.addAttribute("ui.class",
	 * "bus");
	 * 
	 * } else if (s.getMode().equals("walk")) { e.addAttribute("ui.class",
	 * "walk");
	 * 
	 * } else if (s.getMode().equals("train")) { e.addAttribute("ui.class",
	 * "train");
	 * 
	 * } else { throw new IllegalArgumentException("Error: Unknown mode"); }
	 * } Viewer viewer = new Viewer(model.getModel(),
	 * Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD); ViewPanel view =
	 * viewer.addDefaultView(false); // false indicates // "no JFrame".
	 * double[] env = getAlternativeBoundaries(alternative);
	 * view.getCamera().setViewCenter(env[0] + 0.5 * (env[1] - env[0]),
	 * env[2] + 0.5 * (env[3] - env[2]), 0);
	 * view.getCamera().setViewPercent(0.3);
	 * view.setBorder(BorderFactory.createTitledBorder
	 * ("EvoKnowledge Model")); view.validate(); return view;
	 */
    }

    /*
     * private static double[] getAlternativeBoundaries(Alternative alt) {
     * double[] envelope = new double[] { 180.0, -180.0, 90.0, -90.0 };
     * 
     * for (Leg l : alt.getLegs()) { List<Coordinate> trace =
     * Util.polylineToPoints(l.getGeometry());
     * 
     * for (Coordinate c : trace) { if (c.x < envelope[0]) envelope[0] = c.x; if
     * (c.x > envelope[1]) envelope[1] = c.x; if (c.y < envelope[2]) envelope[2]
     * = c.y; if (c.y > envelope[3]) envelope[3] = c.y; } } return envelope; }
     */

    public static void main(String args[]) throws IOException, JAXBException,
	    URISyntaxException {
	EvoKnowledgeCRF model = new EvoKnowledgeCRF();
	System.setProperty("org.graphstream.ui.renderer",
		"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
	List<Alternative> alt = loadRoutes(new Object());
	EvoKnowledgeCRFView view = new EvoKnowledgeCRFView(model, alt.get(4));
	view.setVisible(true);
    }

    private static final String FILE_SEPARATOR = "/";

    private static List<Alternative> loadRoutes(Object temp)
	    throws JAXBException, URISyntaxException, IOException {

	URL res = temp.getClass().getResource(
		FILE_SEPARATOR + STORYBOARD1_FOLDER + FILE_SEPARATOR
			+ STORYBOARD1_MAIN_XML);
	File f = new File(res.toURI());
	ResourceLoader.loadStoryboard(f);

	File routesFile = ResourceLoader.getRouteFile();
	JAXBContext context = JAXBContext.newInstance(Routes.class);
	Routes r = new Routes();
	r = (Routes) context.createUnmarshaller().unmarshal(routesFile);

	List<Alternative> alt = new ArrayList<Alternative>();
	for (int i = 0; i < r.getRoute().size(); i++) {
	    double walkingDistance = 0;
	    long travelTime = 0;
	    double cost = 0;
	    String modes = "";
	    List<Leg> leg = r.getRoute().get(i).getLeg();
	    for (int j = 0; j < leg.size(); j++) {
		walkingDistance += leg.get(j).getWalkingDistance();
		travelTime += leg.get(j).getDuration();
		cost += leg.get(j).getCost();
		modes += leg.get(j).getTransportType().getType() + " ";
	    }
	    alt.add(new Alternative(Integer
		    .valueOf(r.getRoute().get(i).getId()), r.getRoute().get(i)
		    .getNoOfChanges(), walkingDistance, travelTime, cost,
		    modes, leg, 0));
	}
	return alt;
    }
}