package eu.allowensembles.robustness.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import eu.allowensembles.robustness.controller.Activity;
import eu.allowensembles.robustness.controller.RobustnessAnnotationHandler;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import javax.swing.border.EmptyBorder;

public class workflowView extends JPanel {

	private static final long serialVersionUID = -8538833243920964963L;
	private static final String STYLE_ABSTRACT = "dashed=true;dashPattern=5;rounded=true;fontSize=13;fontStyle=1";
	private static final String STYLE_RUNNING = "fillColor=FF0000;fontSize=13;fontStyle=1";
	private static final String STYLE_ABSTRACT_EXECUTED = "dashed=true;dashPattern=5;rounded=true;fillColor=90EE90;fontSize=13;fontStyle=1";
	private static final String STYLE_ABSTRACT_RUNNING = "dashed=true;dashPattern=5;rounded=true;fillColor=FFA500;fontSize=13;fontStyle=1";
	private static final String STYLE_EXECUTED = "fillColor=90EE90;fontSize=13;fontStyle=1";
	private static final String STYLE_DESCRIPTION = "fillColor=FFFFFF;fontSize=13;fontStyle=1";
	private static final String STYLE_DEFAULT = "fontSize=13;fontStyle=1";
	private final mxGraph graph = new mxGraph();
	private final mxGraphComponent graphComponent;

	private final RobustnessAnnotationHandler annotationHandler;
	private boolean autoScroll = true;

	public workflowView(RobustnessAnnotationHandler annotationHandler) {
		this.annotationHandler = annotationHandler;
		this.setLayout(new BorderLayout());
		this.setBorder(new LineBorder(Color.BLACK));

		// create graphic component
		graphComponent = new mxGraphComponent(graph);
		graphComponent.getGraphControl().setBorder(null);
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				if (cell != null) {
					mxCell activity = (mxCell) cell;
					if (activity.isVertex() && activity.getGeometry().getHeight() == 30) {
						showDetails(cell);
					}
				}
			}
		});

		graphComponent.setBorder(new EmptyBorder(10, 0, 0, 0));
		graphComponent.setEnabled(false);
		graphComponent.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		graphComponent.setAutoScroll(true);
		add(graphComponent, BorderLayout.CENTER);
	}

	/**
	 * Update the workflow View with the changed processDiagram
	 * 
	 * @param processDiagram
	 * @param executedActivities 
	 */
	public void updateWorkflowView(ProcessDiagram processDiagram, boolean drawCurrentActivityAsExecuted, ProcessActivity currentActivity, ArrayList<ProcessActivity> executedActivities) {

		graph.selectAll();
		graph.removeCells();

		graph.getModel().beginUpdate();
		List<Object> activityObjs = new ArrayList<Object>();

		Object lastUpdate = null;

		Object parent = graph.getDefaultParent();
		try {
			for (ProcessActivity a: executedActivities) {
				if (a.isAbstract()) {
					continue;
				}
				int l = a.getName().length();
				int w = (int) (80 + (l * 5));
				activityObjs.add(graph.insertVertex(parent, null, a.getName(), 10, 20, w, 30, getStyle(a, drawCurrentActivityAsExecuted, currentActivity)));
				lastUpdate = activityObjs.get(activityObjs.size() - 1);
			}
			if (currentActivity != null && !currentActivity.isAbstract() && !drawCurrentActivityAsExecuted) {
				int l = currentActivity.getName().length();
				int w = (int) (80 + (l * 5));
				activityObjs.add(graph.insertVertex(parent, null, currentActivity.getName(), 10, 20, w, 30, getStyle(currentActivity, drawCurrentActivityAsExecuted, currentActivity)));
				lastUpdate = activityObjs.get(activityObjs.size() - 1);
			}

			for (ProcessActivity a : processDiagram.getActivities()) {
				if (a.isExecuted()) {
					continue;
				}
				int l = a.getName().length();
				int w = (int) (80 + (l * 5));
				activityObjs.add(graph.insertVertex(parent, null, a.getName(), 10, 20, w, 30, getStyle(a, drawCurrentActivityAsExecuted, currentActivity)));
				if (a.isExecuted()) {
					lastUpdate = activityObjs.get(activityObjs.size() - 1);
				}
			}

			for (int i = 1; i < activityObjs.size(); i++) {
				graph.insertEdge(parent, null, "", activityObjs.get(i - 1), activityObjs.get(i));
			}

			// apply layout to graph
			mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
			layout.setOrientation(SwingConstants.WEST);
			layout.execute(graph.getDefaultParent());

		} finally {
			graph.getModel().endUpdate();
		}

		if (lastUpdate != null && autoScroll) {
			graphComponent.scrollCellToVisible(lastUpdate, true);
		}
	}

	/**
	 * Shows the details of an activity when clicked.
	 * 
	 * @param activity
	 */
	public void showDetails(Object activity) {
		Object parent = graph.getDefaultParent();

		try {

			double dWidth = 60;
			double dHeight = 60;

			double aWidth = graph.getBoundingBox(activity).getWidth();
			double x = graph.getBoundingBox(activity).getX();
			double y = graph.getBoundingBox(activity).getY();

			// Position properties in the middle, underneath the activity
			x += (aWidth - dWidth) / 2;

			Activity properties = annotationHandler.getActivity(graph.getLabel(activity));
			String label;
			if (properties == null) {
				label = "No Properties";
			} else {
				label = properties.details();
			}
			graph.getModel().beginUpdate();
			Object details = graph.insertVertex(parent, null, label, x, y + 60, dWidth, dHeight, STYLE_DESCRIPTION);

			graph.insertEdge(parent, null, "", details, activity, "endSize=0");
			updateUI();

		} finally {
			graph.getModel().endUpdate();
		}

	}

	/**
	 * Determines the visual style for an activity
	 * 
	 * @param pa
	 * @return
	 */
	private String getStyle(ProcessActivity pa, boolean drawCurrentActivityAsExecuted, ProcessActivity currentActivity) {
		if (pa.isAbstract() && pa.isExecuted()) {
			return STYLE_ABSTRACT_EXECUTED;
		}
		if (pa.isAbstract() && pa.isRunning()) {
			return STYLE_ABSTRACT_RUNNING;
		}
		if (pa.isAbstract()) {
			return STYLE_ABSTRACT;
		}
		if (pa.isRunning()) {
			return STYLE_RUNNING;
		}
		if (pa.isExecuted() && (!pa.equals(currentActivity) || drawCurrentActivityAsExecuted)) {
			return STYLE_EXECUTED;
		}
		return STYLE_DEFAULT;

	}

	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}

}
