package com.chillimport.server.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import org.geojson.GeoJsonObject;

import java.io.IOException;


public class LocationBuilder {

    private Location location;

    public LocationBuilder() {
        location = new Location();
    }

    public void withName(String name) {
        location.setName(name);
    }

    public void withDescription(String description) {
        location.setDescription(description);
    }

    public void withEncodingType(String encodingType) {
        location.setEncodingType(encodingType);
    }

    public void withLocation(GeoJsonObject geoLocation) {
        location.setLocation(geoLocation);
    }

    public void withId(long id) {
        location.setId(new IdLong(id));
    }

    public void aDefaultLocation() throws IOException {
        location.setName("defaultName");
        location.setDescription("defaultLocation");
        location.setEncodingType("application/vnd.geo+json");

        String locString = "{\n" +
                "       \"type\": \"Point\",\n" +
                "       \"coordinates\": [123.4, 0.00]" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        GeoJsonObject geolocation = mapper.readValue(locString, GeoJsonObject.class);
        location.setLocation(geolocation);
        location.setId(new IdLong(1l));
    }

    public Location build() {
        Location rLocation = new Location(location.getName(), location.getDescription(), location.getEncodingType(), location.getLocation());
        rLocation.setId(location.getId());
        return rLocation;
    }

}
