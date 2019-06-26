package com.chillimport.server.builders;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.*;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class MultiDatastreamBuilder {

    private MultiDatastream multiDatastream;

    public MultiDatastreamBuilder() {
        multiDatastream = new MultiDatastream();
    }

    public void withName(String name) {
        multiDatastream.setName(name);
    }


    public void withDescription(String description) {
        multiDatastream.setDescription(description);
    }

    public void withObservationTypes(List<String> observationTypes) {
        multiDatastream.setMultiObservationDataTypes(observationTypes);
    }

    public void withUnitOfMeasurements(List<UnitOfMeasurement> unitOfMeasurements) {
        multiDatastream.setUnitOfMeasurements(unitOfMeasurements);
    }

    public void withObservedProperties(EntityList<ObservedProperty> observedProperties) {
        multiDatastream.setObservedProperties(observedProperties);
    }

    public void withSensor(Sensor sensor) {
        multiDatastream.setSensor(sensor);
    }

    public void withThing(Thing thing) {
        multiDatastream.setThing(thing);
    }

    public void withId(long id) {
        multiDatastream.setId(new IdLong(id));
    }

    public void aDefaultMultiDatastream() throws IOException {
        multiDatastream.setName("defaultDatastream");
        multiDatastream.setDescription("defaultDescription");
        List<String> observationTypes = new LinkedList<>();
        observationTypes.add("defaultObservationType1");
        observationTypes.add("defaultObservationType2");
        multiDatastream.setMultiObservationDataTypes(observationTypes);
        List<UnitOfMeasurement> unitOfMeasurements = new LinkedList<>();
        unitOfMeasurements.add(new UnitOfMeasurement("defaultUnit1", "defaultSym", "defaultDef"));
        unitOfMeasurements.add(new UnitOfMeasurement("defaultUnit2", "defaultSym", "defaultDef"));
        multiDatastream.setUnitOfMeasurements(unitOfMeasurements);
        ObservedPropertyBuilder observedPropertyBuilder = new ObservedPropertyBuilder();
        observedPropertyBuilder.aDefaultObservedProperty();
        EntityList<ObservedProperty> observedProperties = new EntityList<>(EntityType.OBSERVED_PROPERTIES);
        observedProperties.add(observedPropertyBuilder.build());
        observedPropertyBuilder.withId(2l);
        observedProperties.add(observedPropertyBuilder.build());
        multiDatastream.setObservedProperties(observedProperties);
        SensorBuilder sensorBuilder = new SensorBuilder();
        sensorBuilder.aDefaultSensor();
        multiDatastream.setSensor(sensorBuilder.build());
        ThingBuilder thingBuilder = new ThingBuilder();
        thingBuilder.aDefaultThing();
        multiDatastream.setThing(thingBuilder.build());
        multiDatastream.setId(new IdLong(1l));
    }

    public MultiDatastream build() throws ServiceFailureException {
        MultiDatastream rMultiDatastream = new MultiDatastream(multiDatastream.getName(),
                                                               multiDatastream.getDescription(),
                                                               multiDatastream.getMultiObservationDataTypes(),
                                                               multiDatastream.getUnitOfMeasurements());
        rMultiDatastream.setObservedProperties(multiDatastream.getObservedProperties());
        rMultiDatastream.setSensor(multiDatastream.getSensor());
        rMultiDatastream.setThing(multiDatastream.getThing());
        rMultiDatastream.setId(multiDatastream.getId());
        return rMultiDatastream;
    }

}
