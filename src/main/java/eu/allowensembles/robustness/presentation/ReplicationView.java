package eu.allowensembles.robustness.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import eu.allowensembles.robustness.controller.Message;

import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

public class ReplicationView extends JPanel {

	private static final long serialVersionUID = -5534280945980759792L;

	// Vertices
	private static final String STYLE_FUTURE = "fontSize=13;fontStyle=1";
	private static final String STYLE_ABSTRACT = "dashed=true;dashPattern=5;rounded=true;fontSize=13;fontStyle=1";
	private static final String STYLE_EXECUTED = "fillColor=90EE90;fontSize=13;fontStyle=1";
	private static final String STYLE_PLANED = "fillColor=66A3FF;fontSize=13;fontStyle=1";
	private static final String STYLE_COMPENSATE = "fillColor=FFA500;fontSize=13;fontStyle=1";
	private static final String STYLE_FAILURE = "fillColor=CD5C5C;fontSize=13;fontStyle=1";

	// Edges
	private static final String STYLE_LINE = "strokeColor=000000;endArrow=none;strokeWidth=2;fontSize=13;fontStyle=1";
	private static final String STYLE_LINK_DOWN = "strokeColor=FF0000;endArrow=none;strokeWidth=4;fontSize=13;fontStyle=1";
	private static final String STYLE_RUNNING = "fillColor=66A3FF;fontSize=13;fontStyle=1";


	private List<Object> messageAncors = new ArrayList<>();
	private boolean autoScroll = true;
	private final mxGraphComponent graphComponent;
	private int blackLinesProgress;
	private int replicaProgress = 50;
	private int futureProgress = replicaProgress + 500;
	private int futureStart = futureProgress;
	private Object[] failedNodesEnd = new Object[3];
	private Object[] failedLinksEnd = new Object[2];
	
	private ArrayList<Object> futureObjects = new ArrayList<>();

	private mxGraph graph = new mxGraph();

	public enum ActivityState {
		EXECUTED, PLANNED, COMPENSATE, FAILURE, FUTURE, ABSTRACT,
	}
	
	public enum MessageState {
		CURRENT, FUTURE
	}

	public ReplicationView() {

		this.setLayout(new BorderLayout());

		graphComponent = new mxGraphComponent(graph);
		graphComponent.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		graphComponent.setAutoScroll(true);
		new LineBorder(Color.BLACK);
		graphComponent.setEnabled(false);
		add(graphComponent, BorderLayout.CENTER);

		initReplicationView();

	}

	private void initReplicationView() {

		Object parent = graph.getDefaultParent();
		// the three replica lines
		graph.getModel().beginUpdate();

		blackLinesProgress = 2000;

		Object r1a = graph.insertVertex(parent, "r1a", "", 0, 30, 0, 0, STYLE_RUNNING);
		Object r1b = graph.insertVertex(parent, "r1b", "", blackLinesProgress, 30, 0, 0, STYLE_RUNNING);
		graph.insertEdge(parent, null, "", r1a, r1b, STYLE_LINE);

		Object r2a = graph.insertVertex(parent, "r2a", "", 0, 160, 0, 0, STYLE_RUNNING);
		Object r2b = graph.insertVertex(parent, "r2b", "", blackLinesProgress, 160, 0, 0, STYLE_RUNNING);
		graph.insertEdge(parent, null, "", r2a, r2b, STYLE_LINE);

		Object r3a = graph.insertVertex(parent, "r3a", "", 0, 290, 0, 0, STYLE_RUNNING);
		Object r3b = graph.insertVertex(parent, "r3b", "", blackLinesProgress, 290, 0, 0, STYLE_RUNNING);
		graph.insertEdge(parent, null, "", r3a, r3b, STYLE_LINE);

		graph.insertVertex(parent, "", "", 0, 320, 0, 0);
		graph.getModel().endUpdate();
	}

	/**
	 * Draws a message with a label
	 * 
	 * @param srcRep
	 *            source replica
	 * @param destRep
	 *            destination replica
	 * @param label
	 *            label for the edge
	 */
	private void drawMessage(int srcRep, int destRep, String label, MessageState state) {
		assert label != null;
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		
		int progress;
		if (state == MessageState.FUTURE) {
			progress = futureProgress;
		} else {
			progress = replicaProgress;
		}
		Object RSrc = graph.insertVertex(parent, null, "", 10 + progress, 10 + srcRep * 130 + 20, 0, 0,
				STYLE_RUNNING);
		messageAncors.add(RSrc);
		Object RDst = graph.insertVertex(parent, null, "", 10 + progress + 75, 10 + destRep * 130 + 20, 0, 0,
				STYLE_RUNNING);
		messageAncors.add(RDst);
		if (state == MessageState.FUTURE) {
			futureObjects.add(RDst);
			futureObjects.add(RSrc);
		}
		graph.insertEdge(parent, null, "\n\n"+label, RSrc, RDst, "endSize=10;align=left;fontColor=green;fontSize=13;fontStyle=1");

		graph.getModel().endUpdate();

		if (autoScroll && state == MessageState.CURRENT) {
			graphComponent.scrollCellToVisible(RDst, true);
		}
		stretchLines();
	}

	/**
	 * Draws a message that is sent across a failed link
	 * 
	 * @param srcRep
	 *            source replica
	 * @param destRep
	 *            destination replica
	 * @param label
	 *            label for the edge
	 */
	private void drawFailedMessage(Message m) {
		assert m.type != null;
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		Object RSrc = graph.insertVertex(parent, null, "", 10 + replicaProgress, 10 + m.sourceReplica * 130 + 20, 0, 0,
				STYLE_RUNNING);
		messageAncors.add(RSrc);
		Object RDst = null;
		if (m.getFailure() == 0) {
			RDst = graph.insertVertex(parent, null, "", 10 + replicaProgress + 75, 30 + 65, 0, 0, STYLE_RUNNING);
		} else if (m.getFailure() == 1) {
			RDst = graph.insertVertex(parent, null, "", 10 + replicaProgress + 75, 130 + 30 + 65, 0, 0, STYLE_RUNNING);
		}
		messageAncors.add(RDst);
		graph.insertEdge(parent, null, m.type.name(), RSrc, RDst, "endSize=10;align=left;fontColor=red;fontSize=13;fontStyle=1");

		graph.getModel().endUpdate();

		if (autoScroll) {
			graphComponent.scrollCellToVisible(RDst, true);
		}
		stretchLines();
	}

	/**
	 * Draw all included messages
	 * 
	 * @param messages
	 */
	public void drawMessages(ArrayList<Message> messages) {
		for (Message m : messages) {
			if (m.isFailed()) {
				drawFailedMessage(m);
			} else {
				drawMessage(m.sourceReplica, m.destionationReplica, m.type.name(), MessageState.CURRENT);
			}
		}
		replicaProgress += 120;

		stretchLines();
	}

	/**
	 * Draw one message
	 * 
	 * @param message
	 */
	public void drawMessage(Message message) {
		if (message.isFailed()) {
			drawFailedMessage(message);
		} else {
			drawMessage(message.sourceReplica, message.destionationReplica, message.type.name(), MessageState.CURRENT);
		}
		replicaProgress += 120;

		stretchLines();
	}

	/**
	 * Stretches the black lines to cover more space for additional messages to
	 * be displayed
	 */
	public void stretchLines() {
		
		graph.getModel().beginUpdate();
		//int displacement = replicaProgress - blackLinesProgress + 100;
		int displacement = futureProgress - blackLinesProgress + 100;
		int futureDisplacement = replicaProgress - futureStart;
		if (futureDisplacement > 0) {
			futureStart += futureDisplacement + 200;
			futureProgress += futureDisplacement + 200;
			graph.moveCells(futureObjects.toArray(), futureDisplacement + 200, 0);
		}
		if (displacement > 0) {
			Object[] vertex = graph.getChildCells(graph.getDefaultParent(), true, false);
			Object[] endNodes = { vertex[1], vertex[3], vertex[5] };
			graph.moveCells(endNodes, displacement, 0);
			
			blackLinesProgress += displacement;
		}
		if (displacement > 0) {
			for (Object o : failedNodesEnd) {
				if (o != null) {
					graph.moveCells(new Object[] { o }, displacement, 0);
				}
			}
			for (Object o : failedLinksEnd) {
				if (o != null) {
					graph.moveCells(new Object[] { o }, displacement, 0);
				}
			}
		}
		
		graph.getModel().endUpdate();
	}

	/**
	 * Draw activity name in graph
	 * 
	 * @param replica
	 *            replica executing the activity
	 * @param label
	 *            name of the activity executed
	 */
	public void drawActivityForOne(int replica, String label, ActivityState state) {
		assert label != null;

		replicaProgress += drawActivity(replica, label, state);
		stretchLines();
	}
	
	
	private int drawActivity(int replica, String label, ActivityState state) {
		graph.getModel().beginUpdate();

		int width = 80 + (int) (label.length() * 5);
		Object vertex = null;
		boolean skipAutoScroll = false;
		switch (state) {
		case COMPENSATE:
			vertex = graph.insertVertex(graph.getDefaultParent(), null, label, replicaProgress + 80, replica * 130 + 15,
					width, 30, STYLE_COMPENSATE);
			break;
		case EXECUTED:
			vertex = graph.insertVertex(graph.getDefaultParent(), null, label, replicaProgress + 80, replica * 130 + 15,
					width, 30, STYLE_EXECUTED);
			break;
		case PLANNED:
			vertex = graph.insertVertex(graph.getDefaultParent(), null, label, replicaProgress + 80, replica * 130 + 15,
					width, 30, STYLE_PLANED);
			break;
		case FAILURE:
			vertex = graph.insertVertex(graph.getDefaultParent(), null, label, replicaProgress + 80, replica * 130 + 15,
					width, 30, STYLE_FAILURE);
			break;
		case FUTURE:
			vertex = graph.insertVertex(graph.getDefaultParent(), null, label, futureProgress + 80, replica * 130 + 15,
					width, 30, STYLE_FUTURE);
			futureObjects.add(vertex);
			skipAutoScroll = true;
			break;
		case ABSTRACT:
			vertex = graph.insertVertex(graph.getDefaultParent(), null, label, futureProgress + 80, replica * 130 + 15,
					width, 30, STYLE_ABSTRACT);
			futureObjects.add(vertex);
			skipAutoScroll = true;
			break;
		}
		
		graph.getModel().endUpdate();
		
		if (autoScroll && !skipAutoScroll) {
			graphComponent.scrollCellToVisible(vertex, true);
		}
		return width + 80;
	}
	
	
	public void drawActivityForAll(ArrayList<Integer> activeReplicas, String label, ActivityState state) {
		int width = 0;
		replicaProgress += 100;
		for (int i: activeReplicas) {
			width = drawActivity(i, label, state);
		}
		
		replicaProgress += width;
		stretchLines();
	}

	/**
	 * Set if autoscrolling should be enabled
	 * 
	 * @param autoScroll
	 */
	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}

	/**
	 * Display cutting link between two nodes
	 * 
	 * @param from
	 * @param to
	 * @param replica
	 */
	public void cutLink(int from, int to, int replica) {
		if (failedLinksEnd[replica] == null) {
			Object begin = graph.insertVertex(graph.getDefaultParent(), "", "", replicaProgress,
					replica * 130 + 30 + 65, 0, 0);
			failedLinksEnd[replica] = graph.insertVertex(graph.getDefaultParent(), "", "", blackLinesProgress,
					replica * 130 + 30 + 65, 0, 0);
			graph.insertEdge(graph.getDefaultParent(), null, "", begin, failedLinksEnd[replica], STYLE_LINK_DOWN);
		} else {
			graph.moveCells(new Object[] { failedLinksEnd[replica] }, replicaProgress - blackLinesProgress, 0);
			failedLinksEnd[replica] = null;
		}
	}

	/**
	 * Visualize failed replica To be called whenever the state of the replica
	 * changes
	 * 
	 * @param replicaId
	 *            replica that fails
	 */
	public void failReplica(int replicaId) {
		if (failedNodesEnd[replicaId] == null) {
			Object begin = graph.insertVertex(graph.getDefaultParent(), "", "", replicaProgress, replicaId * 130 + 30,
					0, 0);
			failedNodesEnd[replicaId] = graph.insertVertex(graph.getDefaultParent(), "", "", blackLinesProgress,
					replicaId * 130 + 30, 0, 0);
			graph.insertEdge(graph.getDefaultParent(), null, "", begin, failedNodesEnd[replicaId], STYLE_LINK_DOWN);
		} else {
			double displacement = replicaProgress - graph.getBoundingBox(failedNodesEnd[replicaId]).getX();
			graph.moveCells(new Object[] { failedNodesEnd[replicaId] }, displacement, 0);
			failedNodesEnd[replicaId] = null;
		}
		stretchLines();
	}

	public void drawFutureActivity(String activity, int randomMaster, boolean activelyReplicated, boolean isAbstract) {
		ActivityState state = ActivityState.FUTURE;
		if (isAbstract) {
			state = ActivityState.ABSTRACT;
		}
		int width =0;
		if (activelyReplicated) {
			for (int i = 0; i < 3; i++) {
				width = drawActivity(i, activity, state);
			}
			futureProgress += width + 50;
			
		} else {
			width = drawActivity(randomMaster, activity, state);
			futureProgress += width;
			for (int i = 0; i < 3; i++) {
				if (i != randomMaster) {
					drawMessage(randomMaster, i, "", MessageState.FUTURE);
				}
			}
			futureProgress += 100;
		}
		stretchLines();
		
	}
	
	public void beginDrawingFuture() {
		futureProgress = replicaProgress;
		futureStart = futureProgress;
		stretchLines();
	}
	
	public void clearFuture() {
		graph.getModel().beginUpdate();
		graph.removeCells(futureObjects.toArray(), true);
		graph.getModel().endUpdate();
		futureObjects.clear();
	}

}
