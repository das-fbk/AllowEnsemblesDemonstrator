package eu.allowensembles;

import java.awt.Color;

/**
 * Allow Ensembles Demonstrator constants
 */
public final class DemonstratorConstant {

    private DemonstratorConstant() {
    }

    // general constants
    public static final String UMS_PREFIX = "UrbanMobilitySystem";

    // storyboard constants for storyboard 1
    public static final String STORYBOARD1_MAIN_XML = "Storyboard1-main.xml";
    public static final String STORYBOARD1_FOLDER = "storyboard1";
    public static final String SCENARIO1 = "scenario1";

    // storyboard constants for final review storyboard
    public static final String SCENARIOREVIEW_MAIN_XML = "Storyboard-main.xml";
    public static final String SCENARIOREVIEW_FOLDER = "ScenarioReview";
    public static final String SCENARIOREVIEW = "scenarioReview";

    // menu name constants
    public static final String OPEN = "open";
    public static final String STEP = "step";

    // color for legs
    public static final Color COLOR_BYCICLE = Color.green;
    public static final Color COLOR_CAR = Color.red;
    public static final Color COLOR_WALK = Color.yellow;
    public static final Color COLOR_BUS = Color.orange;
    public static final Color COLOR_CARSHARING = Color.cyan;
    public static final Color COLOR_TRAIN = Color.black;

    // transportation modes
    public static final String BIKE = "bike";
    public static final String CAR = "car";
    public static final String FLEXIBUS = "flexibus";
    public static final String TRAIN = "train";
    public static final String WALK = "walk";
    public static final String NOTHING = "";

    // settings
    public static final int STEP_TIME_DEFAULT = 250;
    private static int stepTime = STEP_TIME_DEFAULT;
    public static final int STEP_TIME_MAX = 1000000;

    // utility methods
    public static Color getColor(String type) {
	switch (type) {
	case "bicycle":
	    return COLOR_BYCICLE;
	case "car":
	    return COLOR_CAR;
	case "walk":
	    return COLOR_WALK;
	case "bus":
	    return COLOR_BUS;
	case "carSharing":
	    return COLOR_CARSHARING;
	case "train":
	    return COLOR_BYCICLE;

	default:
	    return COLOR_CAR;
	}
    }

    public static int getStepTime() {
	return stepTime;
    }

    public static void setStepTime(int time) {
	stepTime = time;
    }
}
