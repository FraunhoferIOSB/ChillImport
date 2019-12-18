package com.chillimport.server.entities;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chillimport.server.FrostSetup;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class DatastreamTest {

    private Datastream ds0;
    private Datastream ds11;
    private Datastream ds12;
    private Datastream mds11;
    private Datastream mds12;

    private List<String> obsTypes1;
    private List<String> obsTypes2;

    private List<UnitOfMeasurement> units1;
    private List<UnitOfMeasurement> units2;

    private List<ObservedProperty> obsProps1;
    private List<ObservedProperty> obsProps2;

    private Sensor s1;

    private Thing t1;
    
    private static String url;
    
    @BeforeClass 
    public static void beforeClass() {
    	url = FrostSetup.getFrostURL();
    }

    @Before
    public void setUp() {
        obsTypes1 = new ArrayList<>();
        obsTypes1.add("obsType1");

        units1 = new ArrayList<>();
        units1.add(new UnitOfMeasurement("N", "S", "D"));

        obsProps1 = new ArrayList<>();
        obsProps1.add(new ObservedProperty("name", "desc", "def"));

        s1 = new Sensor("name", "desc", "enc", "meta");

        String locString = "{\n" +
                "       \"type\": \"Point\",\n" +
                "       \"coordinates\": [170.0, 45.0]" +
                "}";

        Location loc = new Location("name2", "desc", "application/vnd.geo+json", locString); //valid location

        t1 = new Thing("name", "desc", new HashMap<>(), loc);

        ds0 = new Datastream("name", "desc", null, null, null, null, null);
        ds11 = new Datastream("name", "desc", obsTypes1, units1, obsProps1, s1, t1);
        ds12 = new Datastream("name", "desc", obsTypes1, units1, obsProps1, s1, t1);

        obsTypes2 = new ArrayList<>();
        obsTypes2.add("obsType2");
        obsTypes2.add("obsType3");

        units2 = new ArrayList<>();
        units2.add(new UnitOfMeasurement("N2", "S2", "D2"));
        units2.add(new UnitOfMeasurement("N3", "S3", "D3"));

        obsProps2 = new ArrayList<>();
        obsProps2.add(new ObservedProperty("name", "desc", "def"));
        obsProps2.add(new ObservedProperty("name", "desc", "def"));

        mds11 = new Datastream("name", "desc", obsTypes2, units2, obsProps2, s1, t1);
        mds12 = new Datastream("name", "desc", obsTypes2, units2, obsProps2, s1, t1);
    }

    @Test
    public void getObservationTypes() {
        assertNull(ds0.getObservation_types());
        assertEquals(ds11.getObservation_types(), obsTypes1);
        assertEquals(ds11.getObservation_types(), obsTypes1);
        assertEquals(mds11.getObservation_types(), obsTypes2);
        assertEquals(mds12.getObservation_types(), obsTypes2);
    }

    @Test
    public void setObservationTypes() {
        Datastream ds = new Datastream();
        assertNull(ds.getObservation_types());
        ds.setObservation_types(obsTypes1);
        assertEquals(ds.getObservation_types(), obsTypes1);
    }

    @Test
    public void getUnitsOfMeasurement() {
        assertNull(ds0.getUnits_of_measurement());
        assertEquals(ds11.getUnits_of_measurement(), units1);
        assertEquals(ds12.getUnits_of_measurement(), units1);
        assertEquals(mds11.getUnits_of_measurement(), units2);
        assertEquals(mds12.getUnits_of_measurement(), units2);
    }

    @Test
    public void setUnitsOfMeasurement() {
        Datastream ds = new Datastream();
        assertNull(ds.getUnits_of_measurement());
        ds.setUnits_of_measurement(units1);
        assertEquals(ds.getUnits_of_measurement(), units1);
    }

    @Test
    public void getObservedProperties() {
        assertNull(ds0.getObservedProperties());
        assertEquals(ds11.getObservedProperties(), obsProps1);
        assertEquals(ds12.getObservedProperties(), obsProps1);
        assertEquals(mds11.getObservedProperties(), obsProps2);
        assertEquals(mds12.getObservedProperties(), obsProps2);
    }

    @Test
    public void setObservedProperties() {
        Datastream ds = new Datastream();
        assertNull(ds.getObservedProperties());
        ds.setObservedProperties(obsProps1);
        assertEquals(ds.getObservedProperties(), obsProps1);
    }

    @Test
    public void getSensor() {
        assertNull(ds0.getSensor());
        assertEquals(ds11.getSensor(), s1);
        assertEquals(ds12.getSensor(), s1);
        assertEquals(mds11.getSensor(), s1);
        assertEquals(mds12.getSensor(), s1);
    }

    @Test
    public void setSensor() {
        Datastream ds = new Datastream();
        assertNull(ds.getSensor());
        ds.setSensor(s1);
        assertEquals(ds.getSensor(), s1);
    }

    @Test
    public void getThing() {
        assertNull(ds0.getThing());
        assertEquals(ds11.getThing(), t1);
        assertEquals(ds12.getThing(), t1);
        assertEquals(mds11.getThing(), t1);
        assertEquals(mds12.getThing(), t1);
    }

    @Test
    public void setThing() {
        Datastream ds = new Datastream();
        assertNull(ds.getThing());
        ds.setThing(t1);
        assertEquals(ds.getThing(), t1);
    }

    @Test
    public void isMulti() {
        assertFalse(ds0.isMulti());
        assertFalse(ds11.isMulti());
        assertFalse(ds12.isMulti());
        assertTrue(mds11.isMulti());
        assertTrue(mds12.isMulti());
    }

    @Test
    public void convertToFrostDatastream() throws IOException, URISyntaxException, ServiceFailureException {
        de.fraunhofer.iosb.ilt.sta.model.Datastream convertedDS = ds11.convertToFrostDatastream(new URL(url));

        assertEquals(convertedDS.getName(), ds11.getName());
        assertEquals(convertedDS.getDescription(), ds11.getDescription());
        assertEquals(convertedDS.getObservationType(), ds11.getObservation_types().get(0));
        assertEquals(convertedDS.getUnitOfMeasurement(), ds11.getUnits_of_measurement().get(0).convertToFrostStandard());
        assertEquals(convertedDS.getObservedProperty(), ds11.getObservedProperties().get(0).convertToFrostStandard(new URL(url)));
        assertEquals(convertedDS.getSensor(), ds11.getSensor().convertToFrostStandard(new URL(url)));
        assertEquals(convertedDS.getThing(), ds11.getThing().convertToFrostStandard(new URL(url)));

        //set FrostIds
        convertedDS.setId(new IdLong((long) 42));

        convertedDS.getThing().setId(new IdLong((long) 42));
        for (de.fraunhofer.iosb.ilt.sta.model.Location l : convertedDS.getThing().getLocations()) {
            l.setId(new IdLong((long) 42));
        }

        convertedDS.getSensor().setId(new IdLong((long) 42));
        convertedDS.getObservedProperty().setId(new IdLong((long) 42));

        Datastream reconvertedDS = new Datastream(convertedDS);

        assertEquals(reconvertedDS, ds11);

    }

    @Test
    public void convertBackDS() throws IOException, ServiceFailureException, URISyntaxException {
        de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement unit = new de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement("N", "S", "D");
        de.fraunhofer.iosb.ilt.sta.model.Datastream ds = new de.fraunhofer.iosb.ilt.sta.model.Datastream("name", "desc", "obsType1", unit);
        de.fraunhofer.iosb.ilt.sta.model.ObservedProperty op = new de.fraunhofer.iosb.ilt.sta.model.ObservedProperty("name", new URI("def"), "desc");
        ds.setObservedProperty(op);
        ds.setSensor(s1.convertToFrostStandard(new URL(url)));
        ds.setThing(t1.convertToFrostStandard(new URL(url)));

        ds.setId(new IdLong((long) 42));

        ds.getThing().setId(new IdLong((long) 42));
        for (de.fraunhofer.iosb.ilt.sta.model.Location l : ds.getThing().getLocations()) {
            l.setId(new IdLong((long) 42));
        }

        ds.getSensor().setId(new IdLong((long) 42));
        ds.getObservedProperty().setId(new IdLong((long) 42));


        Datastream reconvertedDS = new Datastream(ds);

        assertEquals(reconvertedDS, ds11);
    }

    @Test
    public void convertToFrostMultiDatastream() throws IOException, URISyntaxException, ServiceFailureException {
        MultiDatastream convertedMDS = mds11.convertToFrostMultiDatastream(new URL(url));

        assertEquals(convertedMDS.getName(), mds11.getName());
        assertEquals(convertedMDS.getDescription(), mds11.getDescription());
        assertEquals(convertedMDS.getMultiObservationDataTypes(), mds11.getObservation_types());
        List<de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement> compareUnits = new ArrayList<>();
        for (UnitOfMeasurement u : mds11.getUnits_of_measurement()) {
            compareUnits.add(u.convertToFrostStandard());
        }
        assertEquals(convertedMDS.getUnitOfMeasurements(), compareUnits);
        List<ObservedProperty> compareObsProps = new ArrayList<>();
        for (de.fraunhofer.iosb.ilt.sta.model.ObservedProperty o : convertedMDS.getObservedProperties()) {
            o.setId(new IdLong((long) 1));
            compareObsProps.add(new ObservedProperty(o));
        }
        assertEquals(compareObsProps, mds11.getObservedProperties());
        assertEquals(convertedMDS.getSensor(), mds11.getSensor().convertToFrostStandard(new URL(url)));
        assertEquals(convertedMDS.getThing(), mds11.getThing().convertToFrostStandard(new URL(url)));


        //set frostIds
        convertedMDS.setId(new IdLong((long) 42));

        convertedMDS.getSensor().setId(new IdLong((long) 42));
        convertedMDS.getThing().setId(new IdLong((long) 42));
        for (de.fraunhofer.iosb.ilt.sta.model.Location l : convertedMDS.getThing().getLocations()) {
            l.setId(new IdLong((long) 42));
        }
        for (de.fraunhofer.iosb.ilt.sta.model.ObservedProperty op : convertedMDS.getObservedProperties()) {
            op.setId(new IdLong((long) 42));
        }

        Datastream reconvertedMDS = new Datastream(convertedMDS);

        assertEquals(reconvertedMDS, mds11);
    }

    @Test
    public void convertBackMDS() throws URISyntaxException, IOException, ServiceFailureException {
        List<de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement> units = new ArrayList<>();
        units.add(new de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement("N2", "S2", "D2"));
        units.add(new de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement("N3", "S3", "D3"));
        MultiDatastream mds = new MultiDatastream("name", "desc", obsTypes2, units);
        de.fraunhofer.iosb.ilt.sta.model.ObservedProperty op = new de.fraunhofer.iosb.ilt.sta.model.ObservedProperty("name", new URI("def"), "desc");
        mds.setObservedProperties(new ArrayList<>());
        mds.getObservedProperties().add(op);
        mds.getObservedProperties().add(op);
        mds.setSensor(s1.convertToFrostStandard(new URL(url)));
        mds.setThing(t1.convertToFrostStandard(new URL(url)));

        //set frostIds
        mds.setId(new IdLong((long) 42));

        mds.getSensor().setId(new IdLong((long) 42));
        mds.getThing().setId(new IdLong((long) 42));
        for (de.fraunhofer.iosb.ilt.sta.model.Location l : mds.getThing().getLocations()) {
            l.setId(new IdLong((long) 42));
        }
        for (de.fraunhofer.iosb.ilt.sta.model.ObservedProperty o : mds.getObservedProperties()) {
            o.setId(new IdLong((long) 42));
        }

        Datastream reconvertedMDS = new Datastream(mds);

        assertEquals(reconvertedMDS, mds11);
    }

    @Test
    public void equals() {
        assertEquals(ds11, ds12);
        assertEquals(mds11, mds12);
        assertNotEquals(ds11, ds0);
        assertNotEquals(ds11, ds0);
        assertNotEquals(mds11, ds0);
        assertNotEquals(ds11, "");
    }
}