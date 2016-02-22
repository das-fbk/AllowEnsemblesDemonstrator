package eu.allowensembles.evoknowledge.controller;

import eu.allowensembles.utils.Alternative;

public interface IUtilityParameterEstimator {

	/**
	 * Predicts the travel time for a given alternative to travel.
	 * 
	 * @param alternative Alternative to predict travel time of.
	 * 
	 * @return Predicted travel time of the given alternative.
	 */
	double predictTravelTime(Alternative alternative);
	
}
