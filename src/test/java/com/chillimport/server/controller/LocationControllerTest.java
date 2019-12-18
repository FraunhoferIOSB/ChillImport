package com.chillimport.server.controller;

import com.chillimport.server.FrostSetup;
import com.chillimport.server.TestSetup;
import com.chillimport.server.builders.LocationBuilder;
import com.chillimport.server.utility.SensorThingsServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.LocationDao;
import de.fraunhofer.iosb.ilt.sta.model.EntityType;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.geojson.GeoJsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private String locUrlString;
    
    private static String url;

    @Mock
    private SensorThingsServiceFactory sensorThingsServiceFactory;

    @InjectMocks
    private LocationController locationController;
    
    @BeforeClass 
    public static void beforeClass() throws Exception {
    	url = FrostSetup.getFrostURL();

    	TestSetup.setup();
    }
    
    @Before
    public void before() throws JsonProcessingException {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(locationController).build();
        
        com.chillimport.server.entities.Location loc = new com.chillimport.server.entities.Location("UofC CCIT","University of Calgary, CCIT building","application/vndgeo+json","{\"type\": \"Point\", \"coordinates\": [-114.133, 51.08]}");
        ObjectMapper mapper = new ObjectMapper();
        locUrlString = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.Location>(loc, url)); 
    }

    @Test
    public void create() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(new SensorThingsService(new URL(url)));

        this.mvc.perform(post("/location/create").contentType("application/json").content(locUrlString)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void getSingle() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        LocationDao locDaoMock = mock(LocationDao.class);
        GeoJsonObject geoJsonMock = mock(GeoJsonObject.class);
        when(geoJsonMock.toString()).thenReturn("{\"type\": \"Point\",\"coordinates\": [11.11, 123.45]}");

        LocationBuilder builder = new LocationBuilder();
        builder.withName("MockLocation");
        builder.withDescription("descr");
        builder.withEncodingType("application/vnd.geo+json");
        builder.withLocation(geoJsonMock);
        builder.withId(1l);
        Location locMock = builder.build();

        when(locDaoMock.find(1)).thenReturn(locMock);
        when(sensorThingsServiceMock.locations()).thenReturn(locDaoMock);

        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/location/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "{\"name\":\"MockLocation\",\"description\":\"descr\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\\"type\\\": \\\"Point\\\",\\\"coordinates\\\": [11.11, 123.45]}\"}");

    }

    @Test
    public void getAll() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        LocationDao locDaoMock = mock(LocationDao.class);
        Query<Location> queryMock = mock(Query.class);
        GeoJsonObject geoJsonMock = mock(GeoJsonObject.class);

        when(geoJsonMock.toString()).thenReturn("{\"type\": \"Point\",\"coordinates\": [11.11, 123.45]}");

        LocationBuilder builder = new LocationBuilder();
        builder.withName("MockLocation");
        builder.withDescription("descr");
        builder.withEncodingType("application/vnd.geo+json");
        builder.withLocation(geoJsonMock);
        builder.withId(1l);
        Location locMock1 = builder.build();
        builder.withId(2l);
        Location locMock2 = builder.build();

        EntityList<Location> locMocks = new EntityList<>(EntityType.LOCATION);
        locMocks.add(locMock1);
        locMocks.add(locMock2);
        when(queryMock.list()).thenReturn(locMocks);
        when(locDaoMock.query()).thenReturn(queryMock);
        when(sensorThingsServiceMock.locations()).thenReturn(locDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/location/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "[{\"name\":\"MockLocation\",\"description\":\"descr\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\\"type\\\": \\\"Point\\\",\\\"coordinates\\\": [11.11, 123.45]}\"},{\"name\":\"MockLocation\",\"description\":\"descr\",\"frostId\":\"2\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\\"type\\\": \\\"Point\\\",\\\"coordinates\\\": [11.11, 123.45]}\"}]");
    }

    @Test
    public void malformedURLEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(MalformedURLException.class);


        this.mvc.perform(post("/location/create").contentType("application/json").content(locUrlString)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/location/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/location/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));

    }

    @Test
    public void uRISyntaxEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(URISyntaxException.class);

        this.mvc.perform(post("/location/create").contentType("application/json").content(locUrlString)).andExpect(status().isNotFound()).andDo(
                print()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/location/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/location/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));

    }

    @Test
    public void serviceFailureEx() throws Exception {
        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        LocationDao locationDaoMock = mock(LocationDao.class);
        Query<Location> queryMock = mock(Query.class);

        when(locationDaoMock.find(1)).thenThrow(ServiceFailureException.class);
        when(queryMock.list()).thenThrow(ServiceFailureException.class);
        when(locationDaoMock.query()).thenReturn(queryMock);
        when(sensorThingsServiceMock.locations()).thenReturn(locationDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        this.mvc.perform(get("/location/single").param("id",
                                                       "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Failed to find Location on server."));
        this.mvc.perform(get("/location/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Failed to find Locations on server."));


    }
}