package eu.allowensembles.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import eu.allowensembles.presentation.main.map.viewer.MyWaypoint;
import eu.allowensembles.utility.controller.Preferences;

/**
 * All data for a user
 */
public class UserData {

    private String name;
    private List<Alternative> alternatives = new ArrayList<Alternative>();
    private ImmutableList<Alternative> originalAlternatives = null;
    private Preferences preferences;
    private Alternative selectedAlternative;
    private MyWaypoint mapIcon;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<Alternative> getAlternatives() {
	return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
	this.alternatives = alternatives;
	if (originalAlternatives == null) {
	    this.originalAlternatives = ImmutableList.copyOf(alternatives);
	}
    }

    public void setPreferences(Preferences pref) {
	this.preferences = pref;
    }

    public Preferences getPreferences() {
	return preferences;
    }

    public void setSelectedAlternative(Alternative sa) {
	this.selectedAlternative = sa;
    }

    public Alternative getSelectedAlternative() {
	return selectedAlternative;
    }

    public MyWaypoint getMapIcon() {
	return mapIcon;
    }

    public void setMapIcon(MyWaypoint mapIcon) {
	this.mapIcon = mapIcon;
    }

    public ImmutableList<Alternative> getOriginalAlternatives() {
	return originalAlternatives;
    }

}
