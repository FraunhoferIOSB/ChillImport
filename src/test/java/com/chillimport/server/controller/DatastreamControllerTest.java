package com.chillimport.server.controller;

import com.chillimport.server.FileManager;
import com.chillimport.server.FrostSetup;
import com.chillimport.server.TestSetup;
import com.chillimport.server.builders.DatastreamBuilder;
import com.chillimport.server.builders.MultiDatastreamBuilder;
import com.chillimport.server.utility.SensorThingsServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.*;
import de.fraunhofer.iosb.ilt.sta.model.*;
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

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
public class DatastreamControllerTest {

    @Autowired
    private WebApplicationContext context;
    
    private static String testpath;
    private static String sep = File.separator;

    private MockMvc mvc;
    
    private MockMvc mvcOPC; 
    
    private MockMvc mvcSensor;
    
    private MockMvc mvcThing;

    private String dsString;
    private String mdsString;
    
    private static String url;

    @Mock
    private SensorThingsServiceFactory sensorThingsServiceFactory;

    @InjectMocks
    private DatastreamController datastreamController;
    
    @InjectMocks
    private SensorController sensorController;
    
    @InjectMocks
    private ObservedPropertyController opC;
    
    @InjectMocks
    private ThingController thingController;
    
    @BeforeClass 
    public static void beforeClass() throws Exception {
    	url = FrostSetup.getFrostURL();
    	TestSetup.setup();
    }

    @Before
    public void setup() throws Exception {
    	testpath = "src" + sep + "test" + sep + "resources";
    	//FileManager.setPathsOnStartup(testpath);
    	
    	
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(datastreamController).build();
        
        mvcOPC = MockMvcBuilders.standaloneSetup(opC).build(); 
        mvcSensor = MockMvcBuilders.standaloneSetup(sensorController).build();
        mvcThing = MockMvcBuilders.standaloneSetup(thingController).build();

        List<String> obsTypes = new ArrayList<>();
        obsTypes.add("oT1");

        List<com.chillimport.server.entities.UnitOfMeasurement> units = new ArrayList<>();
        units.add(new com.chillimport.server.entities.UnitOfMeasurement("testUnit", "sym", "def"));

        com.chillimport.server.entities.ObservedProperty oP = new com.chillimport.server.entities.ObservedProperty("TestOP", "desc", "def");
        oP.setFrostId("1");
        List<com.chillimport.server.entities.ObservedProperty> oPs = new ArrayList<>();
        oPs.add(oP);

        Map<String, Object> pmap = new HashMap<>();
        pmap.put("k1", "v1");
        com.chillimport.server.entities.Location loc = new com.chillimport.server.entities.Location("testLoc",
                                                                                                    "desc",
                                                                                                    "application/vnd.geo+json",
                                                                                                    "{\"type\": \"Point\",\"coordinates\": [1.23, 24.68]}");
        loc.setFrostId("1");
        com.chillimport.server.entities.Thing thing = new com.chillimport.server.entities.Thing("testThing", "desc", pmap, loc);
        thing.setFrostId("1");


        com.chillimport.server.entities.Sensor sensor = new com.chillimport.server.entities.Sensor("testSensor", "desc", "enc", "meta");
        sensor.setFrostId("1");

        com.chillimport.server.entities.Datastream ds = new com.chillimport.server.entities.Datastream("testDS",
                                                                                                       "desc",
                                                                                                       obsTypes,
                                                                                                       units,
                                                                                                       oPs,
                                                                                                       sensor,
                                                                                                       thing);

        obsTypes.add("oT2");
        units.add(new com.chillimport.server.entities.UnitOfMeasurement("testUnit2", "sym", "def"));
        com.chillimport.server.entities.ObservedProperty oP2 = new com.chillimport.server.entities.ObservedProperty("TestOP", "desc", "def");
        oP2.setFrostId("2");
        oPs.add(oP2);

        com.chillimport.server.entities.Datastream mds = new com.chillimport.server.entities.Datastream("testDS",
                                                                                                        "desc",
                                                                                                        obsTypes,
                                                                                                        units,
                                                                                                        oPs,
                                                                                                        sensor,
                                                                                                        thing);

        ObjectMapper mapper = new ObjectMapper();
        dsString = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.Datastream>(ds,url));
        mdsString = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.Datastream>(mds,url));
        
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(new SensorThingsService(new URL(url)));
        
        String op1String = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.ObservedProperty>(oP,url));
        String op2String = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.ObservedProperty>(oP2,url));
        
        this.mvcOPC.perform(post("/observedProperty/create").contentType("application/json").content(op1String)).andDo(print()).andExpect(status().isOk());
        this.mvcOPC.perform(post("/observedProperty/create").contentType("application/json").content(op2String)).andDo(print()).andExpect(status().isOk());
        
        String sensorString = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.Sensor>(sensor, url));
        
        this.mvcSensor.perform(post("/sensor/create").contentType("application/json").content(sensorString)).andDo(print()).andExpect(status().isOk());
        
        String thingString = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.Thing>(thing, url));
        
        this.mvcThing.perform(post("/thing/create").contentType("application/json").content(thingString)).andDo(print()).andExpect(status().isOk());
        
        
    }
     
    @Test
    public void create() throws Exception {
    	
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(new SensorThingsService(new URL(url)));
        
        
        this.mvc.perform(post("/datastream/create").contentType("application/json").content(dsString)).andDo(print()).andExpect(status().isOk());
        this.mvc.perform(post("/datastream/create").contentType("application/json").content(mdsString)).andDo(print()).andExpect(status().isOk());

    }
    
	
    
    @Test
    public void getSingle() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        DatastreamDao dsDaoMock = mock(DatastreamDao.class);

        DatastreamBuilder dsBuilder = new DatastreamBuilder();
        dsBuilder.aDefaultDatastream();
        dsBuilder.withName("Mock DS");
        dsBuilder.withDescription("descr");
        dsBuilder.withId(1l);

        Datastream dsMock = dsBuilder.build();

        when(dsDaoMock.find(1)).thenReturn(dsMock);
        when(sensorThingsServiceMock.datastreams()).thenReturn(dsDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/datastream/single").param("id", "1").param("isMulti",
                                                                                             "false").param("url", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "{\"name\":\"Mock DS\",\"description\":\"descr\",\"frostId\":\"1\",\"observation_types\":[\"defaultObservationType\"],\"units_of_measurement\":[{\"name\":\"defaultUnit\",\"symbol\":\"defaultSym\",\"definition\":\"defaultDef\"}],\"observedProperties\":[{\"name\":\"defaultObservedProperty\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"definition\":\"default.uri\"}],\"sensor\":{\"name\":\"defaultSensor\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"encoding_TYPE\":\"default\",\"metadata\":\"defaultMetadata\"},\"thing\":{\"name\":\"defaultThing\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"properties\":{\"defaulProperty\":\"defaultValue\"},\"location\":{\"name\":\"defaultName\",\"description\":\"defaultLocation\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\n       \\\"type\\\": \\\"Point\\\",\\n       \\\"coordinates\\\": [123.4, 0.0]}\"}},\"multi\":false}");
    }
	
    
    
    @Test
    public void getSingleMulti() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        MultiDatastreamDao mdsDaoMock = mock(MultiDatastreamDao.class);

        MultiDatastreamBuilder mdsBuilder = new MultiDatastreamBuilder();
        mdsBuilder.aDefaultMultiDatastream();
        mdsBuilder.withName("MDS Mock");
        mdsBuilder.withDescription("descr");
        mdsBuilder.withId(1l);

        MultiDatastream mdsMock = mdsBuilder.build();

        when(mdsDaoMock.find(1)).thenReturn(mdsMock);
        when(sensorThingsServiceMock.multiDatastreams()).thenReturn(mdsDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/datastream/single").param("id", "1").param("isMulti",
                                                                                             "true").param("url", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "{\"name\":\"MDS Mock\",\"description\":\"descr\",\"frostId\":\"1\",\"observation_types\":[\"defaultObservationType1\",\"defaultObservationType2\"],\"units_of_measurement\":[{\"name\":\"defaultUnit1\",\"symbol\":\"defaultSym\",\"definition\":\"defaultDef\"},{\"name\":\"defaultUnit2\",\"symbol\":\"defaultSym\",\"definition\":\"defaultDef\"}],\"observedProperties\":[{\"name\":\"defaultObservedProperty\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"definition\":\"default.uri\"},{\"name\":\"defaultObservedProperty\",\"description\":\"defaultDescription\",\"frostId\":\"2\",\"definition\":\"default.uri\"}],\"sensor\":{\"name\":\"defaultSensor\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"encoding_TYPE\":\"default\",\"metadata\":\"defaultMetadata\"},\"thing\":{\"name\":\"defaultThing\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"properties\":{\"defaulProperty\":\"defaultValue\"},\"location\":{\"name\":\"defaultName\",\"description\":\"defaultLocation\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\n       \\\"type\\\": \\\"Point\\\",\\n       \\\"coordinates\\\": [123.4, 0.0]}\"}},\"multi\":true}");
    }
    

    @Test
    public void getAll() throws Exception {
    	
    	

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        ThingDao thingDaoMock = mock(ThingDao.class);
        DatastreamDao dsDaoMock = mock(DatastreamDao.class);
        MultiDatastreamDao mdsDaoMock = mock(MultiDatastreamDao.class);
        Query<Datastream> dsQueryMock = mock(Query.class);
        Query<MultiDatastream> mdsQueryMock = mock(Query.class);
        Thing thingMock = mock(Thing.class);

        DatastreamBuilder dsBuilder = new DatastreamBuilder();
        MultiDatastreamBuilder mdsBuilder = new MultiDatastreamBuilder();
        dsBuilder.aDefaultDatastream();
        mdsBuilder.aDefaultMultiDatastream();
        dsBuilder.withName("Mock DS");
        mdsBuilder.withName("MDS Mock");
        dsBuilder.withDescription("descr");
        mdsBuilder.withDescription("descr");
        dsBuilder.withId(1l);
        mdsBuilder.withId(1l);

        Datastream dsMock = dsBuilder.build();
        MultiDatastream mdsMock = mdsBuilder.build();

        EntityList<Datastream> dsMocks = new EntityList<>(EntityType.DATASTREAM);
        dsMocks.add(dsMock);
        EntityList<MultiDatastream> mdsMocks = new EntityList<>(EntityType.MULTIDATASTREAM);
        mdsMocks.add(mdsMock);

        when(dsQueryMock.list()).thenReturn(dsMocks);
        when(mdsQueryMock.list()).thenReturn(mdsMocks);
        when(dsDaoMock.query()).thenReturn(dsQueryMock);
        when(mdsDaoMock.query()).thenReturn(mdsQueryMock);

        when(thingMock.datastreams()).thenReturn(dsDaoMock);
        when(thingMock.multiDatastreams()).thenReturn(mdsDaoMock);
        when(thingDaoMock.find(1)).thenReturn(thingMock);
        when(sensorThingsServiceMock.things()).thenReturn(thingDaoMock);

        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/datastream/all").param("thingId", "1").param("url", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "[{\"name\":\"Mock DS\",\"description\":\"descr\",\"frostId\":\"1\",\"observation_types\":[\"defaultObservationType\"],\"units_of_measurement\":[{\"name\":\"defaultUnit\",\"symbol\":\"defaultSym\",\"definition\":\"defaultDef\"}],\"observedProperties\":[{\"name\":\"defaultObservedProperty\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"definition\":\"default.uri\"}],\"sensor\":{\"name\":\"defaultSensor\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"encoding_TYPE\":\"default\",\"metadata\":\"defaultMetadata\"},\"thing\":{\"name\":\"defaultThing\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"properties\":{\"defaulProperty\":\"defaultValue\"},\"location\":{\"name\":\"defaultName\",\"description\":\"defaultLocation\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\n       \\\"type\\\": \\\"Point\\\",\\n       \\\"coordinates\\\": [123.4, 0.0]}\"}},\"multi\":false},{\"name\":\"MDS Mock\",\"description\":\"descr\",\"frostId\":\"1\",\"observation_types\":[\"defaultObservationType1\",\"defaultObservationType2\"],\"units_of_measurement\":[{\"name\":\"defaultUnit1\",\"symbol\":\"defaultSym\",\"definition\":\"defaultDef\"},{\"name\":\"defaultUnit2\",\"symbol\":\"defaultSym\",\"definition\":\"defaultDef\"}],\"observedProperties\":[{\"name\":\"defaultObservedProperty\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"definition\":\"default.uri\"},{\"name\":\"defaultObservedProperty\",\"description\":\"defaultDescription\",\"frostId\":\"2\",\"definition\":\"default.uri\"}],\"sensor\":{\"name\":\"defaultSensor\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"encoding_TYPE\":\"default\",\"metadata\":\"defaultMetadata\"},\"thing\":{\"name\":\"defaultThing\",\"description\":\"defaultDescription\",\"frostId\":\"1\",\"properties\":{\"defaulProperty\":\"defaultValue\"},\"location\":{\"name\":\"defaultName\",\"description\":\"defaultLocation\",\"frostId\":\"1\",\"encoding_TYPE\":\"application/vnd.geo+json\",\"location\":\"{\\n       \\\"type\\\": \\\"Point\\\",\\n       \\\"coordinates\\\": [123.4, 0.0]}\"}},\"multi\":true}]");
    }

    @Test
    public void malformedURLEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(MalformedURLException.class);


        this.mvc.perform(post("/datastream/create").contentType("application/json").content(mdsString)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Malformed URL for Frost-Server."));
        this.mvc.perform(post("/datastream/create").contentType("application/json").content(dsString)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/datastream/single").param("id", "1").param("isMulti",
                                                                          "true").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/datastream/single").param("id", "1").param("isMulti",
                                                                          "false").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/datastream/all").param("thingId", "1").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));

    }

    @Test
    public void uRISyntaxEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(URISyntaxException.class);


        this.mvc.perform(post("/datastream/create").contentType("application/json").content(mdsString)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Wrong URI for Frost-Server."));
        this.mvc.perform(post("/datastream/create").contentType("application/json").content(dsString)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/datastream/single").param("id", "1").param("isMulti",
                                                                          "true").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/datastream/single").param("id", "1").param("isMulti",
                                                                          "false").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/datastream/all").param("thingId", "1").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));

    }

    @Test
    public void serviceFailureEx() throws Exception {
        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        ThingDao thingDaoMock = mock(ThingDao.class);
        DatastreamDao dsDaoMock = mock(DatastreamDao.class);
        MultiDatastreamDao mdsDaoMock = mock(MultiDatastreamDao.class);

        when(mdsDaoMock.find(1)).thenThrow(ServiceFailureException.class);
        when(dsDaoMock.find(1)).thenThrow(ServiceFailureException.class);

        when(sensorThingsServiceMock.multiDatastreams()).thenReturn(mdsDaoMock);
        when(sensorThingsServiceMock.datastreams()).thenReturn(dsDaoMock);
        when(thingDaoMock.find(1)).thenThrow(ServiceFailureException.class);
        when(sensorThingsServiceMock.things()).thenReturn(thingDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        this.mvc.perform(get("/datastream/single").param("id",
                                                         "1").param("isMulti", "true").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Failed to find MultiDatastream on server."));
        this.mvc.perform(get("/datastream/single").param("id",
                                                         "1").param("isMulti", "false").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Failed to find Datastream on server."));
        this.mvc.perform(get("/datastream/all").param("thingId", "1").param("url", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Failed to find Datastreams of Thing (Id: 1) on server."));


    }
}