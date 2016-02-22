package eu.allowensembles.presentation.main.events;

public class SelectedAbstractActivityEvent {
    private String EventLabel;

    public SelectedAbstractActivityEvent(String label) {
	EventLabel = label;
    }

    public String getLabel() {
	return EventLabel;
    }

}
