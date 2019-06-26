package com.chillimport.server.controller;


import com.chillimport.server.entities.Thing;
import com.chillimport.server.errors.ErrorHandler;
import com.chillimport.server.errors.LogManager;
import com.chillimport.server.utility.SensorThingsServiceFactory;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


/**
 * Controller class receiving requests for creating or getting Things
 */
@RestController
public class ThingController extends EntityController<Thing> {

    @Autowired
    private SensorThingsServiceFactory sensorThingsServiceFactory;

    @Override
    @RequestMapping(value = "/thing/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody Thing thing) {
        de.fraunhofer.iosb.ilt.sta.model.Thing frostThing;
        try {
            frostThing = thing.convertToFrostStandard();
        } catch (IOException e) {
            LogManager.getInstance().writeToLog(e.getMessage(), true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            SensorThingsService service = sensorThingsServiceFactory.build();
            service.create(frostThing);
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to create Thing on server.", true);
            return new ResponseEntity<>("Failed to create Thing on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new Thing(frostThing), HttpStatus.OK);
    }


    @Override
    @RequestMapping(value = "/thing/single", method = RequestMethod.GET)
    public ResponseEntity<?> get(@RequestParam int thingId) {
        Thing thing;
        try {
            SensorThingsService service = sensorThingsServiceFactory.build();
            thing = new Thing(service.things().find(thingId));
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to find Thing on server.", true);
            return new ResponseEntity<>("Failed to find Thing on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(thing, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/thing/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAll() {

        EntityList<de.fraunhofer.iosb.ilt.sta.model.Thing> frostThings;
        List<Thing> things = new LinkedList<>();

        try {
            SensorThingsService service = sensorThingsServiceFactory.build();

            frostThings = service.things().query().list();
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to find Things on server.", true);
            return new ResponseEntity<>("Failed to find Things on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        for (de.fraunhofer.iosb.ilt.sta.model.Thing frostThing : frostThings) {
            things.add(new Thing(frostThing));
        }

        LogManager.getInstance().writeToLog("Retrieved all Things", false);

        return new ResponseEntity<>(things, HttpStatus.OK);
    }
}
