package eu.allowensembles.presentation.main.process;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.primitives.Ints;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import eu.allowensembles.controller.ProcessEngineFacade;
import eu.allowensembles.presentation.main.MainWindow;
import eu.allowensembles.presentation.main.action.DraggingMouseAdapter;
import eu.allowensembles.presentation.main.action.listener.MouseActivityListener;
import eu.allowensembles.utils.OrderedGraph;
import eu.fbk.das.process.engine.api.domain.AbstractActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ScopeActivity;
import eu.fbk.das.process.engine.api.domain.WhileActivity;

/**
 * Panel for process model visualization using {@link mxGraph}
 */
public class ProcessModelPanel extends JPanel {

    /**
     * Internal class to get together information about image and coordinate
     */
    private class CoordinateImage {

	public int x;
	public int y;
	public Image img;

	public CoordinateImage(Image img, int tx, int ty) {
	    this.img = img;
	    this.x = tx;
	    this.y = ty;
	}

    }

    private static final long serialVersionUID = 3711288024677922473L;

    private static final Logger logger = LogManager
	    .getLogger(ProcessModelPanel.class);

    // graph styles
    private static final String STYLE_ABSTRACT = "verticalAlign=top;dashed=true;dashPattern=5;rounded=true;fillColor=FFFFFF";
    private static final String STYLE_RUNNING = "verticalAlign=top;fillColor=FF0000";
    private static final String STYLE_ABSTRACT_EXECUTED = "verticalAlign=top;dashed=true;dashPattern=5;rounded=true;fillColor=90EE90";
    private static final String STYLE_ABSTRACT_RUNNING = "verticalAlign=top;dashed=true;dashPattern=5;rounded=true;fillColor=90EE90";
    private static final String STYLE_EXECUTED = "verticalAlign=top;fillColor=90EE90";
    private static final String STYLE_DEFAULT = "verticalAlign=top;fillColor=FFFFFF";
    private static final String STYLE_WHILE = "verticalAlign=top;fillColor=FFFFFF";
    private static final String STYLE_SCOPE = "verticalAlign=center;strokeWidth=2;dashed=true;dashed=1;shadow=1;dashPattern=1;rounded=true;fillColor=FFFFFF";
    private static final String STYLE_WHILE_EXECUTED = "verticalAlign=top;dashed=true;rounded=true;fillColor=90EE90";
    private static final String STYLE_WHILE_RUNNING = "verticalAlign=top;dashed=true;rounded=true;fillColor=FFA500";
    // private static final String STYLE_SCOPE =
    // "verticalAlign=top;strokeWidth=2;html=1;shape=mxgraph.flowchart.document;whiteSpace=wrap;dashed=1;dashPattern=1;";

    private mxGraphComponent graphComponent;

    private ProcessEngineFacade pef;

    private int idgenerator = 0;

    // save refinements for later use
    // use pid and name of activity for key
    private Map<String, OrderedGraph> refinements = new HashMap<String, OrderedGraph>();

    private BufferedImage replyImage;
    private BufferedImage invokeImage;

    // temporary map current processes's activity names
    private Map<String, ProcessActivity> activityName = new HashMap<String, ProcessActivity>();

    private mxGraphLayout layout;

    private List<CoordinateImage> images = new ArrayList<CoordinateImage>();

    private int sx;

    private int sy = 20;

    private mxCell lastCell;

    public ProcessModelPanel(ProcessEngineFacade pef) {
	this.pef = pef;
	// create graphic component
	graphComponent = new mxGraphComponent(new mxGraph());
	// build layout for graph
	layout = new mxParallelEdgeLayout(graphComponent.getGraph());
	// layout.setOrientation(SwingConstants.NORTH);
	// layout.setUseBoundingBox(true);
	// load images
	replyImage = mxUtils.loadImage("/images/1443529214_icon-reply.png");
	invokeImage = mxUtils.loadImage("/images/invoke_24.png");
	// add graphComponent to panel
	graphComponent.setBorder(null);
	graphComponent.setEnabled(false);
	add(graphComponent);

	// test for scroll by dragging
	setAutoscrolls(true);

	DraggingMouseAdapter ma = new DraggingMouseAdapter(this);
	addMouseListener(ma);
	addMouseMotionListener(ma);
    }

    /**
     * Init panel: add click listener on graph
     * 
     * @param mainWindow
     */
    public void init(MainWindow mainWindow) {
	if (graphComponent != null) {
	    graphComponent.getGraphControl().addMouseListener(
		    new MouseActivityListener(graphComponent, mainWindow));
	} else {
	    logger.warn("GraphComponet is null, impossible to ini processModelPanel");
	}
    }

    /**
     * Clear graph
     * 
     * @see {@link mxGraphModel#clear}
     */
    public void clear() {
	mxGraph current = graphComponent.getGraph();
	((mxGraphModel) current.getModel()).clear();
	activityName.clear();
	images.clear();
	lastCell = null;
	sx = 0;
	sy = 20;
    }

    @Override
    public void paint(Graphics g) {
	super.paint(g);
	if (images != null) {
	    for (CoordinateImage ci : images) {
		g.drawImage(ci.img, ci.x, ci.y, this);
	    }
	}

    }

    /**
     * Update internal graph using {@link ProcessDiagram} informations
     * 
     * @param pd
     *            , {@link ProcessDiagram} used to display information as graph
     */
    public void updateProcess(ProcessDiagram pd) {
	if (pd.getActivities() == null || pd.getActivities().isEmpty()) {
	    logger.warn("No activities to show, updateProcess failed");
	    return;
	}
	clear();
	// update
	mxGraph current = graphComponent.getGraph();
	current = getGraphFromProcessDiagram(pd, current).getGraph();
	// apply layout to graph
	layout.execute(current.getDefaultParent());
	// add image icons
	// addIcons(current, current.getDefaultParent());
    }

    // private void addIcons(mxGraph current, Object object) {
    // // for each cell vertex, add icon to represent type of vertex
    // Object[] vertices = mxGraphModel.getChildCells(current.getModel(),
    // object, true, false);
    // if (vertices == null) {
    // logger.warn("Vertices null, cannot add image icons");
    // return;
    // }
    // CoordinateImage ci = null;
    // for (int i = 0; i < vertices.length; i++) {
    // mxCell cell = (mxCell) vertices[i];
    // if (cell.getValue() != null && cell.getValue() instanceof String) {
    // String label = (String) cell.getValue();
    // ProcessActivity pa = activityName.get(label);
    // if (pa != null) {
    // int dx = 0;
    // int dy = 0;
    // // if there is a parent
    // if (cell.getParent() != null
    // && cell.getParent().getGeometry() != null) {
    // dx = graphComponent.getX()
    // + (int) cell.getParent().getGeometry().getX();
    // dy = graphComponent.getY()
    // + (int) cell.getParent().getGeometry().getY();
    // } else {
    // dx = graphComponent.getX();
    // dy = graphComponent.getY();
    // }
    // mxGeometry geometry = cell.getGeometry();
    // if (geometry != null) {
    // int tx = (int) (geometry.getX() + dx + graphComponent
    // .getAlignmentX());
    // int ty = (int) (geometry.getY() + dy);
    // if (pa.isSend()) {
    // ci = new CoordinateImage(getImageCopy(invokeImage),
    // tx, ty);
    // images.add(ci);
    // } else if (pa.isReceive()) {
    // ci = new CoordinateImage(getImageCopy(replyImage),
    // tx, ty);
    // images.add(ci);
    // }
    // }
    // // proceed recursive with childs
    // if (cell.getChildCount() != 0) {
    // addIcons(current, cell);
    // }
    // }
    // }
    //
    // }
    //
    // }

    // private Image getImageCopy(BufferedImage img) {
    // return img.getScaledInstance(img.getWidth(), img.getHeight(),
    // Image.SCALE_DEFAULT);
    //
    // }

    private OrderedGraph getGraphFromProcessDiagram(ProcessDiagram pd,
	    mxGraph current) {
	List<String> order = new ArrayList<String>();
	OrderedGraph response = new OrderedGraph();
	Object parent = current.getDefaultParent();
	current.getModel().beginUpdate();
	try {
	    // build graph using list of activities
	    Object lastInserted = null;
	    for (int i = 0; i < pd.getActivities().size(); i++) {
		ProcessActivity pa = pd.getActivities().get(i);
		activityName.put(pa.getName(), pa.clone());
		if (lastInserted == null) {
		    Object v = insertVertex(current, parent, pa.getName(),
			    getStyle(pa));
		    order.add(pa.getName());
		    lastInserted = v;
		    // handle abstract activity case
		    current = getGraphForActivity(pd, current, pa);

		} else {
		    Object v = insertVertex(current, parent, pa.getName(),
			    getStyle(pa));
		    current.insertEdge(parent, null, "", lastInserted, v);
		    order.add(pa.getName());
		    lastInserted = v;
		    // handle abstract activity case
		    current = getGraphForActivity(pd, current, pa);
		}
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	} finally {
	    current.getModel().endUpdate();
	}
	response.setGraph(current);
	response.setOrder(order);
	return response;
    }

    private mxGraph getGraphForActivity(ProcessDiagram pd, mxGraph current,
	    ProcessActivity pa) {
	if (pef != null && pa != null && pd.getCurrentActivity() != null
		&& pd.getCurrentActivity().getName() != null
		&& pa.getName() != null) {
	    if (pa.isAbstract()) {
		return getGraphForAbstractActivity(pd, current,
			(AbstractActivity) pa);
	    } else if (pa.isScope()) {
		return getGraphForScopeActivity(pd, current, (ScopeActivity) pa);
	    } else if (pa.isWhile()) {
		return getGraphForWhileActivity(pd, current, (WhileActivity) pa);
	    }
	}
	return current;
    }

    private mxGraph getGraphForWhileActivity(ProcessDiagram pd,
	    mxGraph current, WhileActivity pa) {
	logger.debug(pa.getName());
	ProcessDiagram subProcess = pa.getDefaultBranch();
	OrderedGraph ref = new OrderedGraph();
	ref = getGraphFromProcessDiagram(subProcess, ref.getGraph());
	ref.setActivityName(pa.getName());
	refinements.put(pd.getpid() + "_" + pa.getName(), ref);
	current = insertIn(current, ref, pa, getStyle(pa));
	return current;
    }

    private mxGraph getGraphForScopeActivity(ProcessDiagram pd,
	    mxGraph current, ScopeActivity pa) {
	logger.debug(pa.getName());
	ProcessDiagram subProcess = pa.getBranch();
	OrderedGraph ref = new OrderedGraph();
	ref = getGraphFromProcessDiagram(subProcess, ref.getGraph());
	ref.setActivityName(pa.getName());
	refinements.put(pd.getpid() + "_" + pa.getName(), ref);
	current = insertIn(current, ref, pa, getStyle(pa));
	return current;
    }

    private mxGraph getGraphForAbstractActivity(ProcessDiagram pd,
	    mxGraph current, AbstractActivity pa) {
	if ((pef.hasRefinements(pd) && pd.getCurrentActivity().getName()
		.equals(pa.getName()))) {
	    logger.debug(pa.getName());
	    ProcessDiagram subProcess = pef.getRefinement(pd);
	    OrderedGraph ref = new OrderedGraph();
	    ref = getGraphFromProcessDiagram(subProcess, ref.getGraph());
	    ref.setActivityName(pa.getName());
	    refinements.put(pd.getpid() + "_" + pa.getName(), ref);
	    current = insertIn(current, ref, pa, getStyle(pa));
	} else if (!pa.isRunning() && pa.isExecuted()) {
	    OrderedGraph gra = refinements
		    .get(pd.getpid() + "_" + pa.getName());
	    current = insertIn(current, gra, pa, getStyle(pa));
	} else if (!pa.isRunning() || pa.isExecuted()) {
	    OrderedGraph gra = refinements
		    .get(pd.getpid() + "_" + pa.getName());
	    current = insertIn(current, gra, pa, getStyle(pa));
	}
	return current;
    }

    private String getStyle(ProcessActivity pa) {
	if (pa.isAbstract() && pa.isExecuted()) {
	    return STYLE_ABSTRACT_EXECUTED;
	}
	if (pa.isAbstract() && pa.isRunning()) {
	    return STYLE_ABSTRACT_RUNNING;
	}
	if (pa.isAbstract()) {
	    return STYLE_ABSTRACT;
	}
	if (pa.isWhile() && pa.isExecuted()) {
	    return STYLE_WHILE_EXECUTED;
	}
	if (pa.isWhile() && pa.isRunning()) {
	    return STYLE_WHILE_RUNNING;
	}
	if (pa.isWhile()) {
	    return STYLE_WHILE;
	}
	if (pa.isScope()) {
	    return STYLE_SCOPE;
	}

	// For better display, set running&executed style after type activity
	// check
	if (pa.isRunning()) {
	    return STYLE_RUNNING;
	}
	if (pa.isExecuted()) {
	    return STYLE_EXECUTED;
	}
	return STYLE_DEFAULT;

    }

    private mxGraph insertIn(mxGraph graph, OrderedGraph ref,
	    ProcessActivity pa, String style) {
	if (ref == null || ref.getGraph() == null) {
	    return graph;
	}
	// update current Graph using processdiagram in input, using pa as
	// reference
	Object parent = graph.getDefaultParent();

	graph.getModel().beginUpdate();
	try {
	    mxCell target = (mxCell) ((mxGraphModel) graph.getModel())
		    .getCell(pa.getName());

	    target.getGeometry()
		    .setHeight(target.getGeometry().getHeight() * 4);

	    activityName.put(pa.getName(), pa.clone());

	    // add link all refGraph cells to graph, using order as reference
	    mxICell lastInserted = null;
	    Map<String, Object> cells = ((mxGraphModel) ref.getGraph()
		    .getModel()).getCells();
	    double tempw = 0;
	    int dx = 20;
	    for (String key : ref.getOrder()) {
		// avoid to use numbered keys (edges)
		if (Ints.tryParse(key) == null) {
		    mxCell cell = (mxCell) cells.get(key);

		    if (activityName.get(cell.getId()) != null) {
			ProcessActivity act = activityName.get(cell.getId());
			style = getStyle(act);
		    }
		    cell.setStyle(style);

		    if (lastInserted == null) {
			lastInserted = target.insert(cell);
			cell.getGeometry().setY(
				target.getGeometry().getY() + 50);
			cell.getGeometry().setX(dx);
			dx += 20 + cell.getGeometry().getWidth();
		    } else {
			mxICell t1 = target.insert(cell);
			cell.getGeometry().setY(
				target.getGeometry().getY() + 50);
			cell.getGeometry().setX(dx);
			dx += 20 + cell.getGeometry().getWidth();
			graph.insertEdge(parent, String.valueOf(++idgenerator),
				"", lastInserted, t1);
			lastInserted = t1;
		    }
		    tempw += cell.getGeometry().getWidth() + 20;
		}

	    }
	    // set father width
	    target.getGeometry().setWidth(tempw + 20);

	} finally {
	    graph.getModel().endUpdate();
	}
	return graph;
    }

    private Object insertVertex(mxGraph current, Object parent, String name,
	    String style) {
	double w = 200;
	double x = 0;
	if (lastCell == null) {
	    x = 0;
	} else {
	    if (isParentAbstract(lastCell) || isParentScope(lastCell)
		    || isParentWhile(lastCell)) {
		// last cell was inside refinement, we need to find father of
		// this refinement and use his coordinate to calculate
		// coordinate for placement
		mxICell pc = getRoot(lastCell);
		if (pc.getGeometry() != null) {
		    x = pc.getGeometry().getX() + pc.getGeometry().getWidth()
			    + 30;
		}
	    } else {
		x = lastCell.getGeometry().getX()
			+ lastCell.getGeometry().getWidth() + 30;
	    }
	}
	Object v = current
		.insertVertex(parent, name, name, x, sy, w, 30, style);
	lastCell = (mxCell) v;
	return v;
    }

    private boolean isParentScope(mxCell cell) {
	mxICell parent = getRoot(cell.getParent());
	if (parent == null || parent.getStyle() == null) {
	    return false;
	}
	return parent.getStyle().equalsIgnoreCase(STYLE_SCOPE);
    }

    private boolean isParentWhile(mxCell cell) {
	mxICell parent = getRoot(cell.getParent());
	if (parent == null || parent.getStyle() == null) {
	    return false;
	}
	return parent.getStyle().equalsIgnoreCase(STYLE_WHILE)
		|| parent.getStyle().equalsIgnoreCase(STYLE_WHILE_RUNNING)
		|| parent.getStyle().equalsIgnoreCase(STYLE_WHILE_EXECUTED);
    }

    private boolean isParentAbstract(mxCell cell) {
	mxICell parent = getRoot(cell.getParent());
	if (parent == null || parent.getStyle() == null) {
	    return false;
	}
	return parent.getStyle().equalsIgnoreCase(STYLE_ABSTRACT)
		|| parent.getStyle().equalsIgnoreCase(STYLE_ABSTRACT_RUNNING)
		|| parent.getStyle().equalsIgnoreCase(STYLE_ABSTRACT_EXECUTED);
    }

    private mxICell getRoot(mxICell cell) {
	if (cell == null || cell.getGeometry() == null) {
	    return null;
	}
	mxICell parent = getRoot(cell.getParent());
	if (parent != null) {
	    return parent;
	}
	return cell;
    }

}
