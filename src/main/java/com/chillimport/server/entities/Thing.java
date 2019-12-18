package com.chillimport.server.entities;

import com.chillimport.server.errors.LogManager;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * describes a thing, an object of the physical or virtual world
 */
public class Thing extends Entity {

    private Map<String, Object> properties;
    private Location location;

    public Thing() {
    }

    /**
     * creates a new thing
     *
     * @param NAME        name of the thing
     * @param DESCRIPTION description of the thing
     * @param PROPERTIES  list of properties of the thing
     * @param LOCATION    location of the thing
     */
    public Thing(String NAME, String DESCRIPTION, Map<String, Object> PROPERTIES, Location LOCATION) {
        super(NAME, DESCRIPTION);
        this.properties = PROPERTIES;
        this.location = LOCATION;
    }


    /**
     * converts an frost standard thing to a chillimport standard thing
     *
     * @param thing frost standard thing to be converted
     */
    public Thing(de.fraunhofer.iosb.ilt.sta.model.Thing thing) {
        super(thing.getName(), thing.getDescription());
        this.setFrostId(thing.getId().getJson());

        this.properties = thing.getProperties();
        this.location = null;

        try {
            this.location = new Location(thing.locations().query().first());
        } catch (NoSuchElementException e) {
            LogManager.getInstance().writeToLog("Thing (" + thing.getId().getUrl() + ") has no Location.", false);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Location of Thing (" + thing.getId().getUrl() + ") could not be retrieved from server.", true);
        } catch (NullPointerException e) { //zum testen
            try {
                this.location = new Location(thing.getLocations().fullIterator().next());
            } catch (NoSuchElementException nsee) {
                LogManager.getInstance().writeToLog("Thing (" + thing.getId().getUrl() + ") has no location and could not be loaded.", true);
            }

        }

    }

    /**
     * returns the properties of the thing
     *
     * @return list of properties of the thing
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * returns the location of the thing
     *
     * @return location of the thing
     */
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * converts the chillimport standard thing to a frost standard thing
     *
     * @return frost standard thing
     *
     * @throws IOException if convertion from String to GeoJson fails (in location)
     */
    public de.fraunhofer.iosb.ilt.sta.model.Thing convertToFrostStandard(URL frostUrl) throws IOException {

        if (!(getFrostId() == null || getFrostId().isEmpty())) {
            SensorThingsService service;
            try {
                service = new SensorThingsService(frostUrl);
                return service.things().find(Long.parseLong(getFrostId()));
            } catch (Exception e) {
            }
        }
        de.fraunhofer.iosb.ilt.sta.model.Thing thing = new de.fraunhofer.iosb.ilt.sta.model.Thing(this.getName(), this.getDescription());

        thing.setProperties(this.properties);

        //check if Loacation already exists on server, otherwise convert Location to frost-standard
        de.fraunhofer.iosb.ilt.sta.model.Location frostLocation = this.location.convertToFrostStandard(frostUrl);
        thing.getLocations().add(frostLocation);

        return thing;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Thing && 
        	super.equals(obj) && 
        	this.location.equals(((Thing) obj).getLocation()) && 
        	this.properties.equals(((Thing) obj).getProperties()));
    }
}
