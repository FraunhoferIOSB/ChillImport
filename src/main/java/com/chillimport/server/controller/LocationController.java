package com.chillimport.server.controller;


import com.chillimport.server.entities.Location;
import com.chillimport.server.errors.ErrorHandler;
import com.chillimport.server.errors.LogManager;
import com.chillimport.server.utility.SensorThingsServiceFactory;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;


/**
 * Controller class receiving requests for creating or getting Locations
 */
@RestController
public class LocationController extends EntityController<Location> {
	
	
    @Autowired
    private SensorThingsServiceFactory sensorThingsServiceFactory;

    @Override
    @RequestMapping(value = "/location/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody EntityStringWrapper<Location> locstring) {
        de.fraunhofer.iosb.ilt.sta.model.Location frostLocation;
        Location location = locstring.getEntity(); 
        URL frostUrl = null;
		try {
			frostUrl = new URL(locstring.getString());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
            frostLocation = location.convertToFrostStandard(frostUrl);
        } catch (IOException e) {
            LogManager.getInstance().writeToLog(e.getMessage(), true);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SensorThingsService service;
        try {
            service = sensorThingsServiceFactory.build(frostUrl);
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        }
        try {
            service.create(frostLocation);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to create Location on server.", true);
            return new ResponseEntity<>("Failed to create Location on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new Location(frostLocation), HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/location/single", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(int id, String frostUrlString) {
        Location location;
        try {
            SensorThingsService service = sensorThingsServiceFactory.build(new URL(frostUrlString));
            location = new Location(service.locations().find(id));
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Could not retrieve Location.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Failed to find Location on server.", HttpStatus.NOT_FOUND);
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/location/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(@RequestParam String frostUrlString) {
        EntityList<de.fraunhofer.iosb.ilt.sta.model.Location> frostLocations;
        List<Location> locations = new ArrayList<>();
        try {
            SensorThingsService service = sensorThingsServiceFactory.build(new URL(frostUrlString));
            frostLocations = service.locations().query().list();
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Could not retrieve Locations.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Failed to find Locations on server.", HttpStatus.NOT_FOUND);
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        }
        for (de.fraunhofer.iosb.ilt.sta.model.Location frostLocation : frostLocations) {
            locations.add(new Location(frostLocation));
        }
        LogManager.getInstance().writeToLog("Retrieved all Locations", false);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }
}
