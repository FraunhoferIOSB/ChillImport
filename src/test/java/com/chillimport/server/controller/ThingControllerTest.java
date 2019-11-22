package com.chillimport.server.controller;

import com.chillimport.server.FileManager;
import com.chillimport.server.FrostSetup;
import com.chillimport.server.TestSetup;
import com.chillimport.server.builders.ThingBuilder;
import com.chillimport.server.utility.SensorThingsServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.ThingDao;
import de.fraunhofer.iosb.ilt.sta.model.EntityType;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;


import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
public class ThingControllerTest {
	
	
	
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private String thingString;
    private String entityString;
    private static String url;
    
    private static String testpath;
    private static String sep = File.separator;

    @Mock
    private SensorThingsServiceFactory sensorThingsServiceFactory;

    @InjectMocks
    private ThingController thingController;
   
    @BeforeClass 
    public static void beforeClass() throws Exception {
    	url = FrostSetup.getFrostURL();
    	
    	testpath = "src" + sep + "test" + sep + "resources";
    	//FileManager.setPathsOnStartup(testpath);
    	TestSetup.setup();
    }
    
    @Before
    public void setup() throws JsonProcessingException {
    	
    	
    	
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(thingController).build();

        Map<String, Object> pmap = new HashMap<>();
        pmap.put("k1", "v1");
        com.chillimport.server.entities.Location loc = new com.chillimport.server.entities.Location("name",
                                                                                                    "desc",
                                                                                                    "application/vnd.geo+json",
                                                                                                    "{\"type\": \"Point\",\"coordinates\": [1.23, 24.68]}");

        com.chillimport.server.entities.Thing thing = new com.chillimport.server.entities.Thing("testThing", "desc", pmap, loc);
        ObjectMapper mapper = new ObjectMapper();
        thingString = mapper.writeValueAsString(thing);
        
        
        entityString = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.Thing>(thing, url));
        
        
    }

    @Test
    public void create() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(new SensorThingsService(new URL(url)));

        this.mvc.perform(post("/thing/create").contentType("application/json").content(entityString)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void getSingle() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        ThingDao thingDaoMock = mock(ThingDao.class);

        ThingBuilder builder = new ThingBuilder();
        builder.aDefaultThing();
        builder.withName("ThingMock");
        builder.withDescription("descr");
        builder.withId(1l);

        Thing thingMock = builder.build();

        when(thingDaoMock.find(1)).thenReturn(thingMock);
        when(sensorThingsServiceMock.things()).thenReturn(thingDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);


        MvcResult result = this.mvc.perform(get("/thing/single").param("thingId", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "{\"name\":\"ThingMock\",\"description\":\"descr\",\"frostId\":\"1\",\"properties\":{\"defaulProperty\":\"defaultValue\"},\"location\":{\"name\":\"defaultName\",\"description\":\"defaultLocation\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\n       \\\"type\\\": \\\"Point\\\",\\n       \\\"coordinates\\\": [123.4, 0.0]}\"}}");
    }

    @Test
    public void getAll() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        ThingDao thingDaoMock = mock(ThingDao.class);
        Query<Thing> queryMock = mock(Query.class);

        ThingBuilder builder = new ThingBuilder();
        builder.aDefaultThing();
        builder.withName("ThingMock");
        builder.withDescription("descr");
        builder.withId(1l);
        Thing thingMock1 = builder.build();
        builder.withId(2l);
        Thing thingMock2 = builder.build();


        EntityList<Thing> thingMocks = new EntityList<>(EntityType.THING);
        thingMocks.add(thingMock1);
        thingMocks.add(thingMock2);

        when(queryMock.list()).thenReturn(thingMocks);
        when(thingDaoMock.query()).thenReturn(queryMock);
        when(sensorThingsServiceMock.things()).thenReturn(thingDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/thing/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "[{\"name\":\"ThingMock\",\"description\":\"descr\",\"frostId\":\"1\",\"properties\":{\"defaulProperty\":\"defaultValue\"},\"location\":{\"name\":\"defaultName\",\"description\":\"defaultLocation\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\n       \\\"type\\\": \\\"Point\\\",\\n       \\\"coordinates\\\": [123.4, 0.0]}\"}},{\"name\":\"ThingMock\",\"description\":\"descr\",\"frostId\":\"2\",\"properties\":{\"defaulProperty\":\"defaultValue\"},\"location\":{\"name\":\"defaultName\",\"description\":\"defaultLocation\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\n       \\\"type\\\": \\\"Point\\\",\\n       \\\"coordinates\\\": [123.4, 0.0]}\"}}]");
    }

    @Test
    public void malformedURLEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(MalformedURLException.class);


        this.mvc.perform(post("/thing/create").contentType("application/json").content(entityString)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/thing/single").param("thingId", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/thing/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));

    }

    @Test
    public void uRISyntaxEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(URISyntaxException.class);

        this.mvc.perform(post("/thing/create").contentType("application/json").content(entityString)).andDo(print()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/thing/single").param("thingId", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/thing/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("Wrong URI for Frost-Server."));

    }

    @Test
    public void serviceFailureEx() throws Exception {
        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        ThingDao thingDaoMock = mock(ThingDao.class);
        Query<Thing> queryMock = mock(Query.class);

        when(thingDaoMock.find(1)).thenThrow(ServiceFailureException.class);
        when(queryMock.list()).thenThrow(ServiceFailureException.class);
        when(thingDaoMock.query()).thenReturn(queryMock);
        when(sensorThingsServiceMock.things()).thenReturn(thingDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        this.mvc.perform(get("/thing/single").param("thingId",
                                                    "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(
                "Failed to find Thing on server."));
        this.mvc.perform(get("/thing/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(
                "Failed to find Things on server."));


    }
}