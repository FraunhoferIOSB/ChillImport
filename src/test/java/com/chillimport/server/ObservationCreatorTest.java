package com.chillimport.server;


import org.junit.Test;

import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.model.Observation;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class ObservationCreatorTest {

    @Test
    public void createOnDatastreamTest() {
        Datastream ds = mock(Datastream.class);
        Object result = 5;
        ZonedDateTime time = ZonedDateTime.now();

        Observation obs = new Observation(null, ds);
        obs.setPhenomenonTimeFrom(time);
        assertEquals(obs, ObservationCreator.create(ds, null, time));


        obs = new Observation(result, ds);
        obs.setPhenomenonTimeFrom(time);
        assertEquals(obs, ObservationCreator.create(ds, result, time));

    }

    @Test(expected = NullPointerException.class)
    public void timeIsNullDatastreamTest() throws NullPointerException {
        Datastream ds = mock(Datastream.class);
        Observation obs = new Observation("result", ds);
        assertEquals(obs, ObservationCreator.create(ds, "result", null));
    }

    @Test
    public void createOnMultiDatastreamTest() {
        MultiDatastream mds = mock(MultiDatastream.class);
        Object[] result = {'g', "string", 4};
        ZonedDateTime time = ZonedDateTime.now();

        Observation obs = new Observation(null, mds);
        obs.setPhenomenonTimeFrom(time);
        assertEquals(obs, ObservationCreator.create(mds, null, time));


        obs = new Observation(result, mds);
        obs.setPhenomenonTimeFrom(time);
        assertEquals(obs, ObservationCreator.create(mds, result, time));

    }

    @Test(expected = NullPointerException.class)
    public void timeIsNullMultiDatastreamTest() throws NullPointerException {
        MultiDatastream mds = mock(MultiDatastream.class);
        Observation obs = new Observation("result", mds);
        assertEquals(obs, ObservationCreator.create(mds, "result", null));
    }

    @Test
    public void createOnDatastreamWithResultTimeTest() {
        Datastream ds = mock(Datastream.class);
        Object[] result = {'g', "string", 4};
        ZonedDateTime time = ZonedDateTime.now();
        ZonedDateTime resTime = ZonedDateTime.now();

        Observation obs = new Observation(null, ds);
        obs.setPhenomenonTimeFrom(time);
        assertEquals(obs, ObservationCreator.create(ds, null, time, null));


        obs.setResultTime(resTime);
        assertEquals(obs, ObservationCreator.create(ds, null, time, resTime));


        obs = new Observation(result, ds);
        obs.setPhenomenonTimeFrom(time);
        obs.setResultTime(resTime);
        assertEquals(obs, ObservationCreator.create(ds, result, time, resTime));

    }

    @Test(expected = NullPointerException.class)
    public void timeIsNullDatastreamWithResultTimeTest() throws NullPointerException {
        Datastream ds = mock(Datastream.class);
        ZonedDateTime resTime = ZonedDateTime.now();

        Observation obs = new Observation("result", ds);
        obs.setResultTime(resTime);
        assertEquals(obs, ObservationCreator.create(ds, "result", null, resTime));
    }

    @Test
    public void createOnMultiDatastreamWithResultTimeTest() {
        MultiDatastream mds = mock(MultiDatastream.class);
        Object[] result = {'g', "string", 4};
        ZonedDateTime time = ZonedDateTime.now();
        ZonedDateTime resTime = ZonedDateTime.now();

        Observation obs = new Observation(null, mds);
        obs.setPhenomenonTimeFrom(time);
        assertEquals(obs, ObservationCreator.create(mds, null, time, null));

        obs.setResultTime(resTime);
        assertEquals(obs, ObservationCreator.create(mds, null, time, resTime));


        obs = new Observation(result, mds);
        obs.setPhenomenonTimeFrom(time);
        obs.setResultTime(resTime);
        assertEquals(obs, ObservationCreator.create(mds, result, time, resTime));

    }

    @Test(expected = NullPointerException.class)
    public void timeIsNullMultiDatastreamWithResultTimeTest() throws NullPointerException {
        MultiDatastream mds = mock(MultiDatastream.class);
        ZonedDateTime resTime = ZonedDateTime.now();

        Observation obs = new Observation("result", mds);
        obs.setResultTime(resTime);
        assertEquals(obs, ObservationCreator.create(mds, "result", null, resTime));
    }


    @Test
    public void combineResultsTest() {
        String str = "string";
        Integer i = 34;
        Double d = 0.4;
        boolean b = true;
        Object obj = new Object();

        ArrayList<Object> list = new ArrayList<>();
        list.add(0, str);
        list.add(1, i);
        list.add(2, d);
        list.add(3, b);
        list.add(4, obj);
        Object[] result = {str, i, d, b, obj};

        assertEquals(result, ObservationCreator.combineResults(list));
    }
}
