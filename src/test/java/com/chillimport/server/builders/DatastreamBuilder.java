package com.chillimport.server.builders;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;

import java.io.IOException;



public class DatastreamBuilder {

    private Datastream datastream;

    public DatastreamBuilder() {
        datastream = new Datastream();
    }

    public void withName(String name) {
        datastream.setName(name);
    }


    public void withDescription(String description) {
        datastream.setDescription(description);
    }

    public void withObservationType(String observationType) {
        datastream.setObservationType(observationType);
    }

    public void withUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement) {
        datastream.setUnitOfMeasurement(unitOfMeasurement);
    }

    public void withObservedProperty(ObservedProperty observedProperty) {
        datastream.setObservedProperty(observedProperty);
    }

    public void withSensor(Sensor sensor) {
        datastream.setSensor(sensor);
    }

    public void withThing(Thing thing) {
        datastream.setThing(thing);
    }

    public void withId(long id) {
        datastream.setId(new IdLong(id));
    }

    public void aDefaultDatastream() throws IOException {
        datastream.setName("defaultDatastream");
        datastream.setDescription("defaultDescription");
        datastream.setObservationType("defaultObservationType");
        datastream.setUnitOfMeasurement(new UnitOfMeasurement("defaultUnit", "defaultSym", "defaultDef"));
        ObservedPropertyBuilder observedPropertyBuilder = new ObservedPropertyBuilder();
        observedPropertyBuilder.aDefaultObservedProperty();
        datastream.setObservedProperty(observedPropertyBuilder.build());
        SensorBuilder sensorBuilder = new SensorBuilder();
        sensorBuilder.aDefaultSensor();
        datastream.setSensor(sensorBuilder.build());
        ThingBuilder thingBuilder = new ThingBuilder();
        thingBuilder.aDefaultThing();
        datastream.setThing(thingBuilder.build());
        datastream.setId(new IdLong(1l));
    }

    public Datastream build() throws ServiceFailureException {
        Datastream rDatastream = new Datastream(datastream.getName(),
                                                datastream.getDescription(),
                                                datastream.getObservationType(),
                                                datastream.getUnitOfMeasurement());
        rDatastream.setObservedProperty(datastream.getObservedProperty());
        rDatastream.setSensor(datastream.getSensor());
        rDatastream.setThing(datastream.getThing());
        rDatastream.setId(datastream.getId());
        return rDatastream;
    }


}
