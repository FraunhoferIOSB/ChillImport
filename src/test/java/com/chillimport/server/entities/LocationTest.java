package com.chillimport.server.entities;

import com.chillimport.server.FrostSetup;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import org.geojson.GeoJsonObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URL;



public class LocationTest {

    private Location l0;
    private Location l11;
    private Location l12;
    private Location l2;
    private Location l3;

    private String locString;
    
    private static String url;
    
    @BeforeClass 
    public static void beforeClass() {
    	url = FrostSetup.getFrostURL();
    }



    @Before
    public void setUp() {

        locString = "{\n" +
                "       \"type\": \"Point\",\n" +
                "       \"coordinates\": [170.0, 4.02]" +
                "}";

        l0 = new Location("name", "desc", null, null);
        l11 = new Location("name", "desc", "encType1", "loc1");
        l12 = new Location("name", "desc", "encType1", "loc1");
        l2 = new Location("name", "desc", "application/vnd.geo+json", locString); //valid Location
        l3 = new Location("name", "desc", "encType2", "loc1");


    }

    @Test
    public void getEncodingType() {
        assertNull(l0.getEncoding_TYPE());
        assertEquals(l11.getEncoding_TYPE(), "encType1");
        assertEquals(l12.getEncoding_TYPE(), "encType1");
        assertEquals(l2.getEncoding_TYPE(), "application/vnd.geo+json");
        assertEquals(l3.getEncoding_TYPE(), "encType2");
    }

    @Test
    public void setEncodingType() {
        Location location = new Location();
        assertNull(location.getEncoding_TYPE());
        location.setEncoding_TYPE("enc");
        assertEquals(location.getEncoding_TYPE(), "enc");
    }

    @Test
    public void getLocation() {
        assertNull(l0.getLocation());
        assertEquals(l11.getLocation(), "loc1");
        assertEquals(l12.getLocation(), "loc1");
        assertEquals(l2.getLocation(), locString);
        assertEquals(l3.getLocation(), "loc1");
    }

    @Test
    public void setLocation() {
        Location location = new Location();
        assertNull(location.getLocation());
        location.setLocation("coordinates");
        assertEquals(location.getLocation(), "coordinates");
    }

    @Test
    public void convertToFrostStandard() {
        de.fraunhofer.iosb.ilt.sta.model.Location convertedLoc = new de.fraunhofer.iosb.ilt.sta.model.Location();
        try {
            convertedLoc = l2.convertToFrostStandard(new URL(url));
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }


        assertEquals(convertedLoc.getName(), l2.getName());
        assertEquals(convertedLoc.getDescription(), l2.getDescription());
        assertEquals(convertedLoc.getEncodingType(), l2.getEncoding_TYPE());
        assertEquals(convertedLoc.getLocation().toString().substring(38).split(",")[0], l2.getLocation().split("\\[")[1].split(",")[0]);
        assertEquals(convertedLoc.getLocation().toString().split(",")[1].substring(10), l2.getLocation().split(", ")[1].split("]")[0]);

        //set frostId
        IdLong id = new IdLong((long) 42);
        convertedLoc.setId(id);
        Location reconvertedLoc = new Location(convertedLoc);

        assertEquals(reconvertedLoc, l2);
    }

    @Test(expected = IOException.class)
    public void convertBadArgument() throws IOException {
        l3.convertToFrostStandard(new URL(url));
    }

    @Test
    public void convertBack() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        GeoJsonObject gjo = mapper.readValue(locString, GeoJsonObject.class);

        de.fraunhofer.iosb.ilt.sta.model.Location location = new de.fraunhofer.iosb.ilt.sta.model.Location("name",
                                                                                                           "desc",
                                                                                                           "application/vnd.geo+json",
                                                                                                           gjo);

        //set frostId
        IdLong id = new IdLong((long) 42);
        location.setId(id);
        Location reconvertedLoc = new Location(location);

        assertEquals(reconvertedLoc, l2);
    }

    @Test
    public void equals() {
        assertEquals(l11, l12);
        assertNotEquals(l11, l0);
        assertNotEquals(l11, l2);
        assertNotEquals(l11, l3);
        assertNotEquals(l11, "");
    }
}