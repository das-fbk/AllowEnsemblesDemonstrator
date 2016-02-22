package eu.allowensembles.evoknowledge.controller;

import java.util.List;

import org.graphstream.graph.Graph;

import eu.allowensembles.utils.Alternative;

public interface IEvoKnowledgeCRFModel {

	Graph getModel();
	
	List<Segment> getSegments(Alternative alt);
	
	Alternative getPredictedAlternative(Alternative alt, Context context);
	
}
