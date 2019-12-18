package com.chillimport.server.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.geojson.GeoJsonObject;

import java.io.IOException;
import java.net.URL;


/**
 * describes a location, the place of one or more things
 */
public class Location extends Entity {

    private String encoding_TYPE;
    private String location;

    public Location() {
    }

    /**
     * creates a new location
     *
     * @param NAME          name of the location
     * @param DESCRIPTION   description of the location
     * @param ENCODING_TYPE encoding type of the location
     * @param LOCATION      textual representation of the location
     */
    public Location(String NAME, String DESCRIPTION, String ENCODING_TYPE, String LOCATION) {
        super(NAME, DESCRIPTION);
        this.encoding_TYPE = ENCODING_TYPE;
        this.location = LOCATION;
    }

    /**
     * converts a frost standard location to a chillimport standard location
     *
     * @param location frost standard location
     */
    public Location(de.fraunhofer.iosb.ilt.sta.model.Location location) {
        super(location.getName(), location.getDescription());
        this.setFrostId(location.getId().getJson());
        this.encoding_TYPE = location.getEncodingType();
        this.location = location.getLocation().toString();
        if (this.location.startsWith("Point{coordinates=LngLatAlt{longitude=")) {
            String longitude = this.location.substring(38).split(",")[0];
            String latitude = this.location.split(",")[1].substring(10);

            this.location = "{\n" +
                    "       \"type\": \"Point\",\n" +
                    "       \"coordinates\": [" + longitude + ", " + latitude + "]" +
                    "}";
        }

    }

    /**
     * returns the encoding type of the location
     *
     * @return encoding type of the location
     */
    public String getEncoding_TYPE() {
        return encoding_TYPE;
    }

    public void setEncoding_TYPE(String encoding_TYPE) {
        this.encoding_TYPE = encoding_TYPE;
    }

    /**
     * returns the textual representation of the location
     *
     * @return textual representation of the location
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    /**
     * converts the chillimport standard location to a frost standard location
     *
     * @return frost standard location
     *
     * @throws IOException if converting from String to GeoJson fails
     */
    public de.fraunhofer.iosb.ilt.sta.model.Location convertToFrostStandard(URL frostUrl) throws IOException {
        de.fraunhofer.iosb.ilt.sta.model.Location frostLocation;

        if (!(getFrostId() == null || getFrostId().isEmpty())) {
            SensorThingsService service;
            try {
                service = new SensorThingsService(frostUrl);
                frostLocation = service.locations().find(Long.parseLong(getFrostId()));
                return frostLocation;
            } catch (Exception e) {
                //Was passiert hier?
            }
        }


        ObjectMapper mapper = new ObjectMapper();
        GeoJsonObject location = null;
        try {
            location = mapper.readValue(this.getLocation(), GeoJsonObject.class);
        } catch (IOException e) {
            throw new IOException("Could not convert the Coordinates of the Location to a GeoJSON-Object");
        }
        return new de.fraunhofer.iosb.ilt.sta.model.Location(this.getName(), this.getDescription(), this.encoding_TYPE, location);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Location && 
        	super.equals(obj) && 
        	this.encoding_TYPE.equals(((Location) obj).getEncoding_TYPE()) && 
        	this.location.equals(((Location) obj).getLocation()));
    }
}


