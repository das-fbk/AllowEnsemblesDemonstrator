package eu.allowensembles.utility.controller;

import java.util.List;

import eu.allowensembles.utils.Alternative;

public interface IUtility {

    /**
     * Calculates utility values for a list of alternatives and returns a ranked
     * list
     * 
     * @return ranked list of alternatives
     */
    public List<Alternative> rankAlternatives(Preferences prefs,
	    List<Alternative> alternatives);

}
