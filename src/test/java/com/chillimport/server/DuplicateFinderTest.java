package com.chillimport.server;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.ObservationDao;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.EntityType;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DuplicateFinderTest {

    @Test
    public void findServiceTest() throws ServiceFailureException {
        Datastream dsMock = mock(Datastream.class);
        ObservationDao obsDaoMock = mock(ObservationDao.class);
        ZonedDateTime time = ZonedDateTime.now();
        Observation obs = new Observation("result", dsMock);
        obs.setPhenomenonTimeFrom(time);


        ArrayList<TableDataTypes> types = new ArrayList<>();
        types.add(TableDataTypes.INT);

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        Query queryMock = mock(Query.class);
        Query filterMock = mock(Query.class);
        Query wrongFilterMock = mock(Query.class);


        EntityList<Observation> list = new EntityList<>(EntityType.OBSERVATION);

        when(sensorThingsServiceMock.observations()).thenReturn(obsDaoMock);
        when(obsDaoMock.query()).thenReturn(queryMock);


        when(queryMock.filter("result eq "+ "other" + " and phenomenonTime eq " + time.toInstant().toString())).thenReturn(wrongFilterMock);
        when(wrongFilterMock.list()).thenReturn(list);
        assertFalse(DuplicateFinder.find(sensorThingsServiceMock,time,"other",types));


        list.add(obs);
        when(queryMock.filter("result eq "+ "result" + " and phenomenonTime eq " + time.toInstant().toString())).thenReturn(filterMock);
        when(filterMock.list()).thenReturn(list);
        assertTrue(DuplicateFinder.find(sensorThingsServiceMock,time,"result",types));
    }


    @Test(expected = ServiceFailureException.class)
    public void findServiceExceptionTest() throws  ServiceFailureException {
        ZonedDateTime time = ZonedDateTime.now();

        ArrayList<TableDataTypes> types = new ArrayList<>();
        types.add(TableDataTypes.INT);

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);


        ObservationDao obsDaoMock = mock(ObservationDao.class);

        Query queryMock = mock(Query.class);
        Query wrongFilterMock = mock(Query.class);

        when(sensorThingsServiceMock.observations()).thenReturn(obsDaoMock);
        when(obsDaoMock.query()).thenReturn(queryMock);


        when(queryMock.filter("result eq "+ "other" + " and phenomenonTime eq " + time.toInstant().toString())).thenReturn(wrongFilterMock);
        when(wrongFilterMock.list()).thenThrow(new ServiceFailureException("Error"));
        assertFalse(DuplicateFinder.find(sensorThingsServiceMock,time,"other",types));

    }

    @Test
    public void findDatastreamTest() throws ServiceFailureException {
        Datastream dsMock = mock(Datastream.class);
        ObservationDao obsDaoMock = mock(ObservationDao.class);
        ZonedDateTime time = ZonedDateTime.now();
        Observation obs = new Observation("result", dsMock);
        obs.setPhenomenonTimeFrom(time);

        ArrayList<TableDataTypes> types = new ArrayList<>();
        types.add(TableDataTypes.INT);

        Query queryMock = mock(Query.class);
        Query filterMock = mock(Query.class);
        Query wrongFilterMock = mock(Query.class);

        EntityList<Observation> list = new EntityList<>(EntityType.OBSERVATION);

        when(dsMock.observations()).thenReturn(obsDaoMock);
        when(obsDaoMock.query()).thenReturn(queryMock);

        when(queryMock.filter("result eq "+ "other" + " and phenomenonTime eq " + time.toInstant().toString())).thenReturn(wrongFilterMock);
        when(wrongFilterMock.list()).thenReturn(list);

        assertFalse(DuplicateFinder.find(dsMock,time,"other",types));


        list.add(obs);
        when(queryMock.filter("result eq "+ "result" + " and phenomenonTime eq " + time.toInstant().toString())).thenReturn(filterMock);
        when(filterMock.list()).thenReturn(list);

        assertTrue(DuplicateFinder.find(dsMock,time,"result", types));
    }

    @Test(expected = ServiceFailureException.class)
    public void findDatastreamExceptionTest() throws  ServiceFailureException {
        Datastream dsMock = mock(Datastream.class);
        ZonedDateTime time = ZonedDateTime.now();

        ArrayList<TableDataTypes> types = new ArrayList<>();
        types.add(TableDataTypes.INT);

        ObservationDao obsDaoMock = mock(ObservationDao.class);

        Query queryMock = mock(Query.class);
        Query wrongFilterMock = mock(Query.class);

        when(dsMock.observations()).thenReturn(obsDaoMock);
        when(obsDaoMock.query()).thenReturn(queryMock);


        when(queryMock.filter("result eq "+ "other" + " and phenomenonTime eq " + time.toInstant().toString())).thenReturn(wrongFilterMock);
        when(wrongFilterMock.list()).thenThrow(new ServiceFailureException("Error"));
        assertFalse(DuplicateFinder.find(dsMock,time,"other",types));

    }

    @Test
    public void findMultiDatastreamTest() throws ServiceFailureException, InvalidFormatException {
        MultiDatastream mdsMock = mock(MultiDatastream.class);
        ObservationDao obsDaoMock = mock(ObservationDao.class);
        ZonedDateTime time = ZonedDateTime.now();

        ArrayList<TableDataTypes> types = new ArrayList<>();
        types.add(TableDataTypes.INT);
        types.add(TableDataTypes.INT);

        Object[] result = {"result", "other"};
        Observation obs = new Observation(result, mdsMock);
        obs.setPhenomenonTimeFrom(time);

        Query queryMock = mock(Query.class);
        Query filterMock = mock(Query.class);
        Query wrongFilterMock = mock(Query.class);

        EntityList<Observation> list = new EntityList<>(EntityType.OBSERVATION);

        when(mdsMock.observations()).thenReturn(obsDaoMock);
        when(obsDaoMock.query()).thenReturn(queryMock);


        result[0] = "other";
        when(queryMock.filter("phenomenonTime eq " + time.toInstant().toString() + " and result[0] eq "+ result[0] + " and " + "result[1] eq " + result[1])).thenReturn(wrongFilterMock);
        when(wrongFilterMock.list()).thenReturn(list);

        assertFalse(DuplicateFinder.find(mdsMock,time,result,types));


        list.add(obs);

        result[0] = "result";

        when(queryMock.filter("phenomenonTime eq " + time.toInstant().toString() + " and result[0] eq "+ result[0] + " and " + "result[1] eq "+ result[1])).thenReturn(filterMock);
        when(filterMock.list()).thenReturn(list);


        assertTrue(DuplicateFinder.find(mdsMock,time,result,types));
    }

    @Test(expected = ServiceFailureException.class)
    public void findMultiDatastreamExceptionTest() throws  ServiceFailureException,InvalidFormatException {
        MultiDatastream mdsMock = mock(MultiDatastream.class);
        ZonedDateTime time = ZonedDateTime.now();


        ArrayList<TableDataTypes> types = new ArrayList<>();
        types.add(TableDataTypes.INT);
        types.add(TableDataTypes.INT);

        Object[] result = {"result", "other"};
        ObservationDao obsDaoMock = mock(ObservationDao.class);

        Query queryMock = mock(Query.class);
        Query wrongFilterMock = mock(Query.class);

        when(mdsMock.observations()).thenReturn(obsDaoMock);
        when(obsDaoMock.query()).thenReturn(queryMock);

        when(queryMock.filter("phenomenonTime eq " + time.toInstant().toString() + " and result[0] eq "+ result[0] + " and " + "result[1] eq " + result[1])).thenReturn(wrongFilterMock);
        when(wrongFilterMock.list()).thenThrow(new ServiceFailureException("Error"));
        assertFalse(DuplicateFinder.find(mdsMock,time,result,types));
    }

    @Test(expected = InvalidFormatException.class)
    public void findMultiDatastreamExceptionObjectNumberTest() throws  ServiceFailureException,InvalidFormatException {
        MultiDatastream mdsMock = mock(MultiDatastream.class);

        ArrayList<TableDataTypes> types = new ArrayList<>();
        types.add(TableDataTypes.INT);
        types.add(TableDataTypes.INT);

        ZonedDateTime time = ZonedDateTime.now();
        Object[] result = {"result"};
        assertFalse(DuplicateFinder.find(mdsMock,time,result,types));
    }
}
