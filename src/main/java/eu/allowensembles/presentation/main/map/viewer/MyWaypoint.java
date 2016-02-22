package eu.allowensembles.presentation.main.map.viewer;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import de.anormalmedia.vividswinganimations.Moveable;

/**
 * A waypoint that also has a color and a label
 * 
 */
public class MyWaypoint extends DefaultWaypoint implements Moveable {
    private final String label;
    private final Color color;
    private BufferedImage image;
    private boolean visible = true;

    /**
     * @param label
     *            the text
     * @param color
     *            the color
     * @param coord
     *            the coordinate
     */
    public MyWaypoint(String label, Color color, GeoPosition coord) {
	super(coord);
	this.label = label;
	this.color = color;
    }

    /**
     * @return the label text
     */
    public String getLabel() {
	return label;
    }

    /**
     * @return the color
     */
    public Color getColor() {
	return color;
    }

    @Override
    public double getLocationX() {
	return getPosition().getLatitude();
    }

    @Override
    public void setLocationX(double x) {
	GeoPosition p = getPosition();
	p = new GeoPosition(x, p.getLongitude());
	setPosition(p);
    }

    @Override
    public double getLocationY() {
	return getPosition().getLongitude();
    }

    @Override
    public void setLocationY(double y) {
	GeoPosition p = getPosition();
	p = new GeoPosition(p.getLatitude(), y);
	setPosition(p);
    }

    public void setImage(BufferedImage img) {
	this.image = img;
    }

    public BufferedImage getImage() {
	return image;
    }

    public boolean isVisible() {
	return visible;
    }

    public void setVisible(boolean visible) {
	this.visible = visible;
    }

}
