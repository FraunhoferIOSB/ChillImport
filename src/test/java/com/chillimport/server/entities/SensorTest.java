package com.chillimport.server.entities;

import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chillimport.server.FrostSetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.net.URL;


public class SensorTest {

    private Sensor s0;
    private Sensor s11;
    private Sensor s12;
    private Sensor s2;
    private Sensor s3;
    
    private static String url;
    
    @BeforeClass 
    public static void beforeClass() {
    	url = FrostSetup.getFrostURL();
    }


    @Before
    public void setUp() {

        s0 = new Sensor("name", "desc", null, null);
        s11 = new Sensor("name", "desc", "encType1", "md1");
        s12 = new Sensor("name", "desc", "encType1", "md1");
        s2 = new Sensor("name", "desc", "encType1", "md2");
        s3 = new Sensor("name", "desc", "encType2", "md1");
    }

    @Test
    public void getEncodingType() {
        assertNull(s0.getEncoding_TYPE());
        assertEquals(s11.getEncoding_TYPE(), "encType1");
        assertEquals(s12.getEncoding_TYPE(), "encType1");
        assertEquals(s2.getEncoding_TYPE(), "encType1");
        assertEquals(s3.getEncoding_TYPE(), "encType2");

    }

    @Test
    public void setEncodingType() {
        Sensor sensor = new Sensor();
        assertNull(sensor.getEncoding_TYPE());
        sensor.setEncoding_TYPE("enc");
        assertEquals(sensor.getEncoding_TYPE(), "enc");
    }

    @Test
    public void getMetadata() {
        assertNull(s0.getMetadata());
        assertEquals(s11.getMetadata(), "md1");
        assertEquals(s12.getMetadata(), "md1");
        assertEquals(s2.getMetadata(), "md2");
        assertEquals(s3.getMetadata(), "md1");
    }

    @Test
    public void setMetadata() {
        Sensor sensor = new Sensor();
        assertNull(sensor.getMetadata());
        sensor.setMetadata("meta");
        assertEquals(sensor.getMetadata(), "meta");
    }

    @Test
    public void convertToFrostStandard() throws MalformedURLException {
        de.fraunhofer.iosb.ilt.sta.model.Sensor convertedSensor = s2.convertToFrostStandard(new URL(url));

        assertEquals(convertedSensor.getName(), s2.getName());
        assertEquals(convertedSensor.getDescription(), s2.getDescription());
        assertEquals(convertedSensor.getEncodingType(), s2.getEncoding_TYPE());
        assertEquals(convertedSensor.getMetadata(), s2.getMetadata());

        //set frostId
        IdLong id = new IdLong((long) 42);
        convertedSensor.setId(id);
        Sensor reconvertedSensor = new Sensor(convertedSensor);
        assertEquals(reconvertedSensor, s2);
    }

    @Test
    public void convertBack() {
        de.fraunhofer.iosb.ilt.sta.model.Sensor sensor = new de.fraunhofer.iosb.ilt.sta.model.Sensor("name", "desc", "encType1", "md2");
        //set frostId
        IdLong id = new IdLong((long) 42);
        sensor.setId(id);
        Sensor reconvertedSensor = new Sensor(sensor);
        assertEquals(reconvertedSensor, s2);
    }

    @Test
    public void equals() {
        assertEquals(s11, s12);
        assertNotEquals(s11, s0);
        assertNotEquals(s11, s2);
        assertNotEquals(s11, s3);
        assertNotEquals(s11, "");
    }
}