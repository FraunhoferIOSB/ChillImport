package com.chillimport.server.builders;



import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.Thing;


public class ThingBuilder {

    private Thing thing;

    public ThingBuilder() {
        thing = new Thing();
    }

    public void withName(String name) {
        thing.setName(name);
    }

    public void withDescription(String description) {
        thing.setDescription(description);
    }

    public void withProperies(Map<String, Object> properies) {
        thing.setProperties(properies);
    }

    public void withLocation(Location location) {
        List<Location> locations = new ArrayList<>();
        locations.add(location);
        thing.setLocations(locations);
    }

    public void aDefaultThing() throws IOException {
        thing.setName("defaultThing");
        thing.setDescription("defaultDescription");
        Map<String, Object> dmap = new HashMap<>();
        dmap.put("defaulProperty", "defaultValue");
        thing.setProperties(dmap);
        LocationBuilder locationBuilder = new LocationBuilder();
        locationBuilder.aDefaultLocation();
        List<Location> locations = new ArrayList<>();
        locations.add(locationBuilder.build());
        thing.setLocations(locations);
        thing.setId(new IdLong(1l));
    }

    public void withId(long id) {
        thing.setId(new IdLong(id));
    }

    public Thing build() {
        Thing rThing = new Thing(thing.getName(), thing.getDescription(), thing.getProperties());
        List<Location> locationList = new ArrayList<>();
        locationList.addAll(thing.getLocations());
        rThing.setLocations(locationList);
        rThing.setId(thing.getId());
        return rThing;
    }

}
