package eu.allowensembles.presentation.main.events;

import eu.allowensembles.utility.controller.Preferences;

public class UpdateUserPreferenceEvent {

    public Preferences userPreference;

    public UpdateUserPreferenceEvent(Preferences userPreference) {
	this.userPreference = userPreference;
    }

}
