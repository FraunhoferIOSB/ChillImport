package com.chillimport.server.config;

import com.chillimport.server.FileManager;
import com.chillimport.server.TestSetup;
import com.chillimport.server.converter.ConverterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class ConfigurationTest {

    Configuration obj1;
    Configuration obj2;
    Configuration obj3;
    Configuration obj4;
    Configuration obj5;
    Configuration obj6;

    String obj1string;
    String obj2string;
    String obj3string;
    
    private String testpath;
    private String sep = File.separator;

    @Before
    public void create() throws Exception {
    	testpath = "src" + sep + "test" + sep + "resources";
    	//FileManager.setPathsOnStartup(testpath);
    	TestSetup.setup();
    	
        String zidc1 = "EAT";
        StringColumn[] dateTime1 = {new StringColumn("TT-HH", 10), new StringColumn("MM-SS", 12)};
        int[] arr1 = {2, 8, 5};
        StreamObservation[] streamData1 = {new StreamObservation(123456, true, arr1)};
        MagicNumberMap[] map1 = new MagicNumberMap[3];

        map1[0] = new MagicNumberMap(1, "true", "wahr");
        map1[1] = new MagicNumberMap(2, "wahr", "true");
        map1[2] = new MagicNumberMap(4, "trueheit", "wahrheit");
        
        URL dummyURL = new URL("https://www.google.de/");

        obj1 = new Configuration(10, "aConfiguration", ";", 3, zidc1, dateTime1, streamData1, map1, DataType.CSV, dummyURL);

        obj2 = new Configuration(10, "aConfiguration", ";", 3, zidc1, dateTime1, streamData1, map1, DataType.CSV, dummyURL);

        obj3 = new Configuration(100, "aConfiguration", ";", 3, zidc1, dateTime1, streamData1, map1, DataType.CSV, dummyURL);

        obj4 = new Configuration(10, "aConfiguration", ";", 3, zidc1, dateTime1, streamData1, map1, DataType.EXCEL, dummyURL);


        obj1string = Configuration.serialize(obj1);
        obj2string = Configuration.serialize(obj2);
        obj3string = Configuration.serialize(obj3);

        obj5 = Configuration.convertToJava(obj1string);
        obj6 = Configuration.convertToJava(obj3string);
       
    }


    @Test
    public void convert1() {
        assertEquals(obj1string, obj2string);
    }

    @Test
    public void convert2() {
        assertNotEquals(obj1string, obj3string);
    }

    @Test
    public void convert3() {
        assertEquals(obj5, obj1);
    }

    @Test
    public void convert4() {
        assertNotEquals(obj5,obj4);
    }

    @Test
    public void convert5() {
        assertNotEquals(obj5,obj3);
    }

    @Test
    public void convert6() {
        assertEquals(obj5, obj2);
    }

    @Test
    public void convert7() {
        assertEquals(obj6, obj3);
    }

    @Test
    public void convert8() {
        assertNotEquals(obj5, obj6);
    }

    @Test(expected = ConverterException.class)
    public void convert9() throws ConverterException {
        Configuration.convertToJava("Hallo");
    }

    @Test
    public void constructor () throws MalformedURLException {
        Configuration config = new Configuration(10,";", DataType.CSV);
        Configuration config3 = new Configuration(10,";", DataType.CSV);
        Configuration config2 = new Configuration(12,";", DataType.CSV);
        assertEquals(config.getNumberOfHeaderlines(), 10);
        assertEquals(config.getDataType(), DataType.CSV);
        assertEquals(config,config3);
        assertNotEquals(config2, config);
    }



    @Test
    public void equals() {
        assertEquals(obj1, obj1);
        assertEquals(obj1, obj2);
        assertNotEquals(obj1, obj3);
        assertNotEquals(obj1, obj4);
        assertNotEquals(obj1, obj1string);
    }

}
