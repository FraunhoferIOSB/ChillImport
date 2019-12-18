package com.chillimport.server.entities;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

import java.io.IOException;
import java.net.URISyntaxException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;


/**
 * describes a (multi-)datastream. A datastream groups a collection of Observations measuring the same observed property and produced by the same
 * sensor. multi-datastreams combine more than one observed property
 */
public class Datastream extends Entity {

    private List<String> observation_types;
    private List<UnitOfMeasurement> units_of_measurement;
    private List<ObservedProperty> observedProperties;
    private Sensor sensor;
    private Thing thing;


    public Datastream() {
    }


    /**
     * creates a new (multi-)datastream
     *
     * @param NAME                 name of the (multi-)datastream
     * @param DESCRIPTION          description of the (multi-)datastream
     * @param OBSERVATION_TYPES    observation type(s) of the (multi-)datastream
     * @param UNITS_OF_MEASUREMENT unit(s) of the (multi-)datastream
     * @param SENSOR               senor related to the (multi-)datastream
     * @param THING                thing related to the (multi-)datastream
     */
    public Datastream(String NAME,
                      String DESCRIPTION,
                      List<String> OBSERVATION_TYPES,
                      List<UnitOfMeasurement> UNITS_OF_MEASUREMENT, List<ObservedProperty> OBSERVED_PROPERTIES, Sensor SENSOR, Thing THING) {
        super(NAME, DESCRIPTION);
        this.observation_types = OBSERVATION_TYPES;
        this.units_of_measurement = UNITS_OF_MEASUREMENT;
        this.observedProperties = OBSERVED_PROPERTIES;
        this.sensor = SENSOR;
        this.thing = THING;
    }

    /**
     * converts a frost standard datastream to a chillimport standard datastream
     *
     * @param ds frost standard datastream
     *
     * @throws ServiceFailureException if sensor or thing of ds could not be found
     */
    public Datastream(de.fraunhofer.iosb.ilt.sta.model.Datastream ds) throws ServiceFailureException {
        super(ds.getName(), ds.getDescription());
        this.setFrostId(ds.getId().getJson());
        this.observation_types = new ArrayList<>();
        this.observation_types.add(ds.getObservationType());
        //convert unit of measurement
        List<UnitOfMeasurement> units = new ArrayList<>();
        UnitOfMeasurement unit = new UnitOfMeasurement(ds.getUnitOfMeasurement());
        units.add(unit);
        this.units_of_measurement = units;
        this.observedProperties = new ArrayList<>();
        this.observedProperties.add(new ObservedProperty(ds.getObservedProperty()));

        this.sensor = new Sensor(ds.getSensor());
        this.thing = new Thing(ds.getThing());

    }

    /**
     * converts a frost standard multidatastream to a chillimport standard datastream
     *
     * @param mds frost standard multidatastream
     *
     * @throws ServiceFailureException if sensor or thing of mds could not be found
     */
    public Datastream(MultiDatastream mds) throws ServiceFailureException {
        super(mds.getName(), mds.getDescription());
        this.setFrostId(mds.getId().getJson());
        this.observation_types = mds.getMultiObservationDataTypes();

        this.units_of_measurement = new ArrayList<>();
        for (de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement unit : mds.getUnitOfMeasurements()) {
            this.units_of_measurement.add(new UnitOfMeasurement(unit));
        }

        this.observedProperties = new ArrayList<>();

        for (de.fraunhofer.iosb.ilt.sta.model.ObservedProperty op : mds.getObservedProperties()) {
            this.observedProperties.add(new ObservedProperty(op));
        }

        this.sensor = new Sensor(mds.getSensor());
        this.thing = new Thing(mds.getThing());


    }

    /**
     * returns the observation type(s) of the (multi-)datastream
     *
     * @return observation type of the (multi-)datastream
     */
    public List<String> getObservation_types() {
        return observation_types;
    }

    public void setObservation_types(List<String> observation_types) {
        this.observation_types = observation_types;
    }

    /**
     * returns the unit(s) of the (multi-)datastream
     *
     * @return list of the units of the (multi-)datastream
     */
    public List<UnitOfMeasurement> getUnits_of_measurement() {
        return units_of_measurement;
    }

    public void setUnits_of_measurement(List<UnitOfMeasurement> units_of_measurement) {
        this.units_of_measurement = units_of_measurement;
    }

    /**
     * returns the observed properties of the (multi-)datastream
     *
     * @return list of the observed properties of the (multi-)datastream
     */
    public List<ObservedProperty> getObservedProperties() {
        return observedProperties;
    }

    public void setObservedProperties(List<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
    }

    /**
     * returns the senor related to the (multi-)datastream
     *
     * @return senor related to the (multi-)datastream
     */
    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * returns the thing related to the (multi-)datastream
     *
     * @return thing related to the (multi-)datastream
     */
    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public boolean isMulti() {
        return this.getUnits_of_measurement() != null && this.getUnits_of_measurement().size() > 1;
    }

    /**
     * converts the chillimport standard datastream to a frost standard datastream
     *
     * @return frost standard datastream
     *
     * @throws IOException if convertion from String to GeoJson fails
     */
    public de.fraunhofer.iosb.ilt.sta.model.Datastream convertToFrostDatastream(URL frostUrl) throws IOException, URISyntaxException {

        if (!(getFrostId() == null || getFrostId().isEmpty())) {
            SensorThingsService service;
            try {
                service = new SensorThingsService(frostUrl);
                return service.datastreams().find(Long.parseLong(getFrostId()));
            } catch (Exception e) {
            }
        }

        de.fraunhofer.iosb.ilt.sta.model.Datastream ds = new de.fraunhofer.iosb.ilt.sta.model.Datastream(this.getName(),
                                                                                                         this.getDescription(),
                                                                                                         this.observation_types.get(0),
                                                                                                         this.units_of_measurement.get(0).convertToFrostStandard());


        ds.setObservedProperty(this.observedProperties.get(0).convertToFrostStandard(frostUrl));

        ds.setSensor(this.sensor.convertToFrostStandard(frostUrl));

        de.fraunhofer.iosb.ilt.sta.model.Thing frostThing = this.thing.convertToFrostStandard(frostUrl);
        ds.setThing(frostThing);

        return ds;
    }

    /**
     * converts the chillimport standard datastream to a frost standard multidatastream
     *
     * @return frost standard multidatastream
     *
     * @throws IOException if convertion from String to GeoJson fails (in location)
     */
    public MultiDatastream convertToFrostMultiDatastream(URL frostUrl) throws IOException, URISyntaxException {
        if (!(getFrostId() == null || getFrostId().isEmpty())) {
            SensorThingsService service;
            try {
                service = new SensorThingsService(frostUrl);
                return service.multiDatastreams().find(Long.parseLong(getFrostId()));
            } catch (Exception e) {
            }
        }

        List<de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement> units = new ArrayList<>();
        for (UnitOfMeasurement unit : this.units_of_measurement) {
            units.add(unit.convertToFrostStandard());
        }

        de.fraunhofer.iosb.ilt.sta.model.ObservedProperty op = new de.fraunhofer.iosb.ilt.sta.model.ObservedProperty();
        List<de.fraunhofer.iosb.ilt.sta.model.ObservedProperty> observedProperties = new ArrayList<>();
        for (ObservedProperty observedProperty : this.observedProperties) {
            observedProperties.add(observedProperty.convertToFrostStandard(frostUrl));

        }

        MultiDatastream mds = new MultiDatastream(this.getName(), this.getDescription(), this.getObservation_types(), units);
        mds.setSensor(this.sensor.convertToFrostStandard(frostUrl));

        de.fraunhofer.iosb.ilt.sta.model.Thing frostThing = this.thing.convertToFrostStandard(frostUrl);
        mds.setThing(frostThing);
        mds.setObservedProperties(observedProperties);

        return mds;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Datastream &&
        	super.equals(obj) && 
            this.observation_types.equals(((Datastream) obj).getObservation_types()) &&
            this.units_of_measurement.equals(((Datastream) obj).getUnits_of_measurement()) && 
            this.observedProperties.equals(((Datastream) obj).getObservedProperties()) && 
            this.thing.equals(((Datastream) obj).getThing()) && 
            this.sensor.equals(((Datastream) obj).getSensor()));
    }

}
