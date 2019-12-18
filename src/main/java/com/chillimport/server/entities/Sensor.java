package com.chillimport.server.entities;

import java.net.URL;

import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;


/**
 * describes a sensor, an instrument that observes a property or phenomenon with the goal of producing an estimate of the value of the property.
 */
public class Sensor extends Entity {

    private String encoding_TYPE;
    private String metadata;

    public Sensor() {
    }

    /**
     * creates a new sensor
     *
     * @param NAME          name of the sensor
     * @param DESCRIPTION   description of the sensor
     * @param encoding_TYPE encoding type of the sensor
     * @param metadata      metadata of the sensor
     */
    public Sensor(String NAME, String DESCRIPTION, String encoding_TYPE, String metadata) {
        super(NAME, DESCRIPTION);
        this.encoding_TYPE = encoding_TYPE;
        this.metadata = metadata;
    }

    /**
     * converts a frost standard sensor to a chillimport standard sensor
     *
     * @param sensor frost standard sensor
     */
    public Sensor(de.fraunhofer.iosb.ilt.sta.model.Sensor sensor) {
        super(sensor.getName(), sensor.getDescription());
        this.setFrostId(sensor.getId().getJson());
        this.encoding_TYPE = sensor.getEncodingType();
        this.metadata = sensor.getMetadata().toString();
    }

    /**
     * returns the encoding type of the sensor
     *
     * @return encoding type of the sensor
     */
    public String getEncoding_TYPE() {
        return encoding_TYPE;
    }

    public void setEncoding_TYPE(String encoding_TYPE) {
        this.encoding_TYPE = encoding_TYPE;
    }

    /**
     * returns the metadata of the sensor
     *
     * @return metadata of the sensor
     */
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * converts the chillimport standard sensor to a frost standard sensor
     *
     * @return frost standard sensor
     */
    public de.fraunhofer.iosb.ilt.sta.model.Sensor convertToFrostStandard(URL frostUrl) {
        if (!(getFrostId() == null || getFrostId().isEmpty())) {
            SensorThingsService service;
            try {
                service = new SensorThingsService(frostUrl);
                return service.sensors().find(Long.parseLong(getFrostId()));
            } catch (Exception e) {
            }
        }
        return new de.fraunhofer.iosb.ilt.sta.model.Sensor(this.getName(), this.getDescription(), this.getEncoding_TYPE(), this.getMetadata());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Sensor && 
        	super.equals(obj) && 
        	this.metadata.equals(((Sensor) obj).getMetadata()) && 
        	this.encoding_TYPE.equals(((Sensor) obj).getEncoding_TYPE()));
    }
}
