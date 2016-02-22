package de.dfki.layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Represents a layer partitioning it into a set of areas having special properties.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Layer<T extends Area> {
	// Identifier of this layer.
	protected String identifier;
	
	// Mapping of areas of the layer to nodes within that area.
	protected Map<String, T> areas;

	/**
	 * Constructor.
	 * Creates a new instance of layer given a name and the StreetMap instance
	 * the layer is overlaid to.
	 * 
	 * @param type Name/Identifier of the layer.
	 * @param base StreetMap the layer is added to.
	 */
	public Layer(String identifier) {
		this.identifier = identifier;
		areas = new HashMap<String, T>();
	}
	
	/**
	 * Adds a new area to the layer.
	 * 
	 * @param area Area to add.
	 */
	public void addArea(T area) {
		
		if (areas.containsKey(area.getName()))
			throw new IllegalStateException("Error: There is already an area " 
					+ area.getName() + " added to the layer.");
		
		areas.put(area.getName(), area);
	}
	
	/**
	 * Returns a list of areas which contain the given point (areas may be overlapping).
	 * 
	 * @param point Point to find areas which contain it.
	 * @return List of areas which contain the given point.
	 */
	public List<Area> getAreasContainingPoint(Coordinate point) {
		List<Area> ret = new ArrayList<Area>();
		
		for (Area a : areas.values()) {
			
			if (a.contains(point))
				ret.add(a);
		}
		return ret;
	}
}
