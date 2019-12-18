package com.chillimport.server.entities;

import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * describes an observed property, which specifies the phenomenon of an observation
 */
public class ObservedProperty extends Entity {

    private String definition;

    public ObservedProperty() {
    }

    /**
     * creates a new observed property
     *
     * @param name        name of the observed property
     * @param description description of the observed property
     * @param definition  definition of the observed property
     */
    public ObservedProperty(String name, String description, String definition) {
        super(name, description);
        this.definition = definition;

    }

    /**
     * converts a frost standard observed property to a chillimport standard observed property
     *
     * @param obsprop frost standard observed property
     */
    public ObservedProperty(de.fraunhofer.iosb.ilt.sta.model.ObservedProperty obsprop) {
        super(obsprop.getName(), obsprop.getDescription());
        this.setFrostId(obsprop.getId().getJson());
        this.definition = obsprop.getDefinition();
    }

    /**
     * returns the definition of the observed property
     *
     * @return definition of the observed property
     */
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * converts the chillimport standard observed property to a frost standard observed property
     *
     * @return frost standard observed property
     *
     * @throws URISyntaxException if definition string has wrong syntax
     */
    public de.fraunhofer.iosb.ilt.sta.model.ObservedProperty convertToFrostStandard(URL frostUrl) throws URISyntaxException {

        if (!(getFrostId() == null || getFrostId().isEmpty())) {
            SensorThingsService service;
            try {
                service = new SensorThingsService(frostUrl);
                return service.observedProperties().find(Long.parseLong(getFrostId()));
            } catch (Exception e) {
            }
        }

        return new de.fraunhofer.iosb.ilt.sta.model.ObservedProperty(this.getName(), new URI(this.definition), this.getDescription());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ObservedProperty && 
        	super.equals(obj) && this.definition.equals(((ObservedProperty) obj).getDefinition()));
    }
}
