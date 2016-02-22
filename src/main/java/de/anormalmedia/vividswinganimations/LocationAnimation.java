package de.anormalmedia.vividswinganimations;

public class LocationAnimation extends AbstractAnimation {

    protected Moveable component;
    protected final double targetX;
    protected final double targetY;

    protected double initialX;
    protected double initialY;

    public LocationAnimation(Moveable component, double targetX, double targetY) {
	this.component = component;
	this.targetX = targetX;
	this.targetY = targetY;
    }

    @Override
    public void prepare() {
	this.initialX = targetX;
	this.initialY = targetY;
	super.prepare();
    }

    @Override
    public void animate(long timeProgress) {
	double nextX = targetX;
	double nextY = targetY;
	if (targetX != -1) {
	    double deltaX = targetX - initialX;
	    nextX = initialX
		    + Math.round((float) deltaX / getDuration() * timeProgress);
	}
	if (targetY != -1) {
	    double deltaY = targetY - initialY;
	    nextY = initialY
		    + Math.round((float) deltaY / getDuration() * timeProgress);
	}

	if (targetX == -1) {
	    nextX = targetX;
	}
	if (targetY == -1) {
	    nextY = targetY;
	}
	component.setLocationX(nextX);
	component.setLocationY(nextY);
    }

    public double getTargetX() {
	return targetX;
    }

    public double getTargetY() {
	return targetY;
    }
}
