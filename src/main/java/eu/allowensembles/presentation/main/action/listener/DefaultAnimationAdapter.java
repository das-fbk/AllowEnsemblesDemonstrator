package eu.allowensembles.presentation.main.action.listener;

import org.jxmapviewer.JXMapViewer;

import de.anormalmedia.vividswinganimations.Moveable;
import de.anormalmedia.vividswinganimations.listener.AnimationAdapter;
import eu.allowensembles.controller.executables.AnimationCompleteInterface;
import eu.allowensembles.presentation.main.map.viewer.MyWaypoint;

public class DefaultAnimationAdapter extends AnimationAdapter {

    private JXMapViewer map;
    private boolean last;
    private AnimationCompleteInterface callback;
    private Moveable target;
    private String transportType;

    public DefaultAnimationAdapter(JXMapViewer map, boolean last,
	    AnimationCompleteInterface callback, Moveable target,
	    String transportType) {
	this.map = map;
	this.last = last;
	this.callback = callback;
	this.target = target;
	this.transportType = transportType;
    }

    @Override
    public void animationStarted() {
	if (target instanceof MyWaypoint) {
	    MyWaypoint mw = (MyWaypoint) target;
	    // mw.setImage(ResourceLoader.getImageForTransportType(transportType));
	}
    }

    @Override
    public void animationUpdated() {
	// on animation update, repaint panel with map
	map.repaint();
    }

    @Override
    public void animationFinished() {
	if (last) {
	    callback.setAnimationCompleted();
	}
    }
}
