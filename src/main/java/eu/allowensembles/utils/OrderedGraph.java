package eu.allowensembles.utils;

import java.util.ArrayList;
import java.util.List;

import com.mxgraph.view.mxGraph;

/**
 * Internal class to store graph and order information
 */
public class OrderedGraph {

    private mxGraph graph = new mxGraph();
    private List<String> order = new ArrayList<String>();
    private String activityName;

    public mxGraph getGraph() {
	return graph;
    }

    public List<String> getOrder() {
	return order;
    }

    public void setGraph(mxGraph graph) {
	this.graph = graph;
    }

    public void setOrder(List<String> order) {
	this.order = order;
    }

    public String getActivityName() {
	return activityName;
    }

    public void setActivityName(String activityName) {
	this.activityName = activityName;
    }

}