package com.chillimport.server.controller;

import com.chillimport.server.FrostSetup;
import com.chillimport.server.TestSetup;
import com.chillimport.server.builders.ObservedPropertyBuilder;
import com.chillimport.server.utility.SensorThingsServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.ObservedPropertyDao;
import de.fraunhofer.iosb.ilt.sta.model.EntityType;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

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
public class ObservedPropertyControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    
    private static String url;
    private String opUrlString;

    @Mock
    private SensorThingsServiceFactory sensorThingsServiceFactory;

    @InjectMocks
    private ObservedPropertyController observedPropertyController;
    
    @BeforeClass 
    public static void beforeClass() throws Exception {
    	url = FrostSetup.getFrostURL();

    	TestSetup.setup();
    }
    
    @Before
    public void before() throws JsonProcessingException {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(observedPropertyController).build();

        ObjectMapper mapper = new ObjectMapper();
        
        com.chillimport.server.entities.ObservedProperty op = new com.chillimport.server.entities.ObservedProperty("TestObsProp", "testing ops props", "https://www.test.de");
        
        opUrlString = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.ObservedProperty>(op, url));
    }
    
    @Test
    public void create() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(new SensorThingsService(new URL(url)));

        this.mvc.perform(post("/observedProperty/create").contentType("application/json").content(opUrlString)).andDo(print()).andExpect(
                status().isOk());
    }

    @Test
    public void getSingle() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        ObservedPropertyDao oPropDaoMock = mock(ObservedPropertyDao.class);

        ObservedPropertyBuilder builder = new ObservedPropertyBuilder();
        builder.withName("MockOP");
        builder.withDescription("descr");
        builder.withDefinition("def");
        builder.withId(1l);
        ObservedProperty oPropMock = builder.build();

        when(oPropDaoMock.find(1)).thenReturn(oPropMock);
        when(sensorThingsServiceMock.observedProperties()).thenReturn(oPropDaoMock);

        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/observedProperty/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "{\"name\":\"MockOP\",\"description\":\"descr\",\"frostId\":\"1\",\"definition\":\"def\"}");
    }

    @Test
    public void getAll() throws Exception {
    	
        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        ObservedPropertyDao oPropDao = mock(ObservedPropertyDao.class);
        Query<ObservedProperty> queryMock = mock(Query.class);

        ObservedPropertyBuilder builder = new ObservedPropertyBuilder();
        builder.withName("MockOP");
        builder.withDescription("descr");
        builder.withDefinition("def");
        builder.withId(1l);
        ObservedProperty oPropMock1 = builder.build();
        builder.withId(2l);
        ObservedProperty oPropMock2 = builder.build();

        EntityList<ObservedProperty> oPropMocks = new EntityList<>(EntityType.OBSERVED_PROPERTIES);
        oPropMocks.add(oPropMock1);
        oPropMocks.add(oPropMock2);
        when(queryMock.list()).thenReturn(oPropMocks);
        when(oPropDao.query()).thenReturn(queryMock);
        when(sensorThingsServiceMock.observedProperties()).thenReturn(oPropDao);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/observedProperty/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "[{\"name\":\"MockOP\",\"description\":\"descr\",\"frostId\":\"1\",\"definition\":\"def\"},{\"name\":\"MockOP\",\"description\":\"descr\",\"frostId\":\"2\",\"definition\":\"def\"}]");
    }


    @Test
    public void malformedURLEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(MalformedURLException.class);


        this.mvc.perform(post("/observedProperty/create").contentType("application/json").content(opUrlString)).andDo(print()).andExpect(
                status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/observedProperty/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/observedProperty/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));

    }


    @Test
    public void uRISyntaxEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(URISyntaxException.class);

        this.mvc.perform(post("/observedProperty/create").contentType("application/json").content(opUrlString)).andDo(print()).andExpect(
                status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/observedProperty/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/observedProperty/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));

    }

    @Test
    public void serviceFailureEx() throws Exception {
        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        ObservedPropertyDao observedPropertyDaoMock = mock(ObservedPropertyDao.class);
        Query<ObservedProperty> queryMock = mock(Query.class);

        when(observedPropertyDaoMock.find(1)).thenThrow(ServiceFailureException.class);
        when(queryMock.list()).thenThrow(ServiceFailureException.class);
        when(observedPropertyDaoMock.query()).thenReturn(queryMock);
        when(sensorThingsServiceMock.observedProperties()).thenReturn(observedPropertyDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        this.mvc.perform(get("/observedProperty/single").param("id",
                                                               "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(
                "Failed to find ObservedProperty on server."));
        this.mvc.perform(get("/observedProperty/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(
                "Failed to find ObservedProperties on server."));
    }
}