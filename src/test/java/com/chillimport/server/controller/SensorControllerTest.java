package com.chillimport.server.controller;

import com.chillimport.server.FileManager;
import com.chillimport.server.FrostSetup;
import com.chillimport.server.TestSetup;
import com.chillimport.server.builders.SensorBuilder;
import com.chillimport.server.utility.SensorThingsServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.dao.SensorDao;
import de.fraunhofer.iosb.ilt.sta.model.EntityType;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
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
public class SensorControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private String sensorString;
    private String sensorUrlString;
    private static String url;
    
    private static String testpath;
    private static String sep = File.separator;

    @Mock
    private SensorThingsServiceFactory sensorThingsServiceFactory;

    @InjectMocks
    private SensorController sensorController;
    
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
        mvc = MockMvcBuilders.standaloneSetup(sensorController).build();

        sensorString = "{\"name\":\"KITSensorMax\",\"description\":\"Max Sensor am KIT\",\"encoding_TYPE\":\"application/json\",\"metadata\":\"\"}";
        com.chillimport.server.entities.Sensor sensor = new com.chillimport.server.entities.Sensor("KITSensorMax", "Max Sensor am KIT", "application/json", "");
        ObjectMapper mapper = new ObjectMapper();
        
        sensorUrlString = mapper.writeValueAsString(new EntityStringWrapper<com.chillimport.server.entities.Sensor>(sensor, url));

    }


    @Test
    public void create() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(new SensorThingsService(new URL(url)));

        this.mvc.perform(post("/sensor/create").contentType("application/json").content(sensorUrlString)).andDo(print()).andExpect(status().isOk());
    }


    @Test
    public void getSingle() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        SensorDao sensorDaoMock = mock(SensorDao.class);

        SensorBuilder builder = new SensorBuilder();
        builder.withName("MockSensor");
        builder.withDescription("descr");
        builder.withEncodingType("encType");
        builder.withMetadata("metadata");
        builder.withId(1l);
        Sensor sensorMock = builder.build();

        when(sensorDaoMock.find(1)).thenReturn(sensorMock);
        when(sensorThingsServiceMock.sensors()).thenReturn(sensorDaoMock);

        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);


        MvcResult result = this.mvc.perform(get("/sensor/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "{\"name\":\"MockSensor\",\"description\":\"descr\",\"frostId\":\"1\",\"encoding_TYPE\":\"encType\",\"metadata\":\"metadata\"}");


    }

    @Test
    public void getAll() throws Exception {

        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        SensorDao sensorDaoMock = mock(SensorDao.class);
        Query<Sensor> queryMock = mock(Query.class);

        SensorBuilder builder = new SensorBuilder();
        builder.withName("MockSensor");
        builder.withDescription("descr");
        builder.withEncodingType("encType");
        builder.withMetadata("metadata");
        builder.withId(1l);
        Sensor sensorMock1 = builder.build();
        builder.withId(2l);
        Sensor sensorMock2 = builder.build();

        EntityList<Sensor> sensorMocks = new EntityList<>(EntityType.SENSOR);
        sensorMocks.add(sensorMock1);
        sensorMocks.add(sensorMock2);
        when(queryMock.list()).thenReturn(sensorMocks);
        when(sensorDaoMock.query()).thenReturn(queryMock);

        when(sensorThingsServiceMock.sensors()).thenReturn(sensorDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        MvcResult result = this.mvc.perform(get("/sensor/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isOk()).andReturn();

        Assert.assertEquals(result.getResponse().getContentAsString(),
                            "[{\"name\":\"MockSensor\",\"description\":\"descr\",\"frostId\":\"1\",\"encoding_TYPE\":\"encType\",\"metadata\":\"metadata\"},{\"name\":\"MockSensor\",\"description\":\"descr\",\"frostId\":\"2\",\"encoding_TYPE\":\"encType\",\"metadata\":\"metadata\"}]");
    }

    @Test
    public void malformedURLEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(MalformedURLException.class);


        this.mvc.perform(post("/sensor/create").contentType("application/json").content(sensorUrlString)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/sensor/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));
        this.mvc.perform(get("/sensor/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Malformed URL for Frost-Server."));

    }

    @Test
    public void uRISyntaxEx() throws Exception {

        when(sensorThingsServiceFactory.build(Mockito.any())).thenThrow(URISyntaxException.class);

        this.mvc.perform(post("/sensor/create").contentType("application/json").content(sensorUrlString)).andDo(print()).andExpect(status().isNotFound()).andExpect(
                content().string(
                        "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/sensor/single").param("id", "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(
                "Wrong URI for Frost-Server."));
        this.mvc.perform(get("/sensor/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().string("Wrong URI for Frost-Server."));

    }

    @Test
    public void serviceFailureEx() throws Exception {
        SensorThingsService sensorThingsServiceMock = mock(SensorThingsService.class);
        SensorDao sensorDaoMock = mock(SensorDao.class);
        Query<Sensor> queryMock = mock(Query.class);

        when(sensorDaoMock.find(1)).thenThrow(ServiceFailureException.class);
        when(queryMock.list()).thenThrow(ServiceFailureException.class);
        when(sensorDaoMock.query()).thenReturn(queryMock);
        when(sensorThingsServiceMock.sensors()).thenReturn(sensorDaoMock);
        when(sensorThingsServiceFactory.build(Mockito.any())).thenReturn(sensorThingsServiceMock);

        this.mvc.perform(get("/sensor/single").param("id",
                                                     "1").param("frostUrlString", url)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(
                "Failed to find Sensor on server."));
        this.mvc.perform(get("/sensor/all").param("frostUrlString", url)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(
                "Failed to find Sensors on server."));


    }
}