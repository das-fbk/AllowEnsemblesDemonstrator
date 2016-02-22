package eu.allowensembles.utils;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * A pojo for demonstrator, {@link DomainObjectInstance} display information
 */
public class DoiBean {

    private String id;
    private String name;
    private ProcessDiagram model;
    private String status;
    private String lon;
    private String lat;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setModel(ProcessDiagram process) {
	this.model = process;

    }

    public ProcessDiagram getModel() {
	return model;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getStatus() {
	return status;
    }

    public void setLat(String lat) {
	this.lat = lat;
    }

    public void setLon(String lon) {
	this.lon = lon;
    }

    public String getLon() {
	return lon;
    }

    public String getLat() {
	return lat;
    }

}
