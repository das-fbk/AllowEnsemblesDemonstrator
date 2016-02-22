package eu.allowensembles.controller.events;

public class SelectJourneyEvent {

    private int alternativeIndex;

    public SelectJourneyEvent(int r) {
	this.alternativeIndex = r;
    }

    public int getAlternativeIndex() {
	return alternativeIndex;
    }

}
