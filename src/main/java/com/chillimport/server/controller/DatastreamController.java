package com.chillimport.server.controller;

import com.chillimport.server.entities.Datastream;
import com.chillimport.server.errors.ErrorHandler;
import com.chillimport.server.errors.LogManager;
import com.chillimport.server.utility.SensorThingsServiceFactory;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
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
 * Controller class receiving requests for creating or getting Datastreams
 */
@RestController
public class DatastreamController {

    @Autowired
    private SensorThingsServiceFactory sensorThingsServiceFactory;
    
    
    @RequestMapping(value = "/datastream/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody EntityStringWrapper<Datastream> dsWrapper) {
    	Datastream ds = dsWrapper.getEntity();
        if (ds.isMulti()) {
        	
            MultiDatastream frostmds;
            try {
                frostmds = ds.convertToFrostMultiDatastream(new URL(dsWrapper.getString()));
            } catch (IOException e) {
                LogManager.getInstance().writeToLog("Could not convert the Coordinates of the Location to a GeoJSON-Object.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Could not convert the Coordinates of the Location to a GeoJSON-Object",
                                            HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (URISyntaxException e) {
                LogManager.getInstance().writeToLog("Definition of an ObservedProperty of the MultiDatastream is no valid URI.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Definition of an ObservedProperty of the MultiDatastream is no valid URI.", HttpStatus.CONFLICT);
            }
            try {
                SensorThingsService service = sensorThingsServiceFactory.build(new URL(dsWrapper.getString()));
                service.create(frostmds);
                ds = new Datastream(frostmds);
            } catch (ServiceFailureException e) {
                LogManager.getInstance().writeToLog("Could not create MultiDatastream.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Failed to create MultiDatastream on server.", HttpStatus.NOT_FOUND);
            } catch (MalformedURLException e) {
                LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
            } catch (URISyntaxException e) {
                LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
            }
        }
        else {
            de.fraunhofer.iosb.ilt.sta.model.Datastream frostds;
            try {
                frostds = ds.convertToFrostDatastream(new URL(dsWrapper.getString()));
            } catch (IOException e) {
                LogManager.getInstance().writeToLog("Could not convert the Coordinates of the Location to a GeoJSON-Object.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Could not convert the Coordinates of the Location to a GeoJSON-Object.",
                                            HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (URISyntaxException e) {
                LogManager.getInstance().writeToLog("Definition of the ObservedProperty of the Datastream is no valid URI.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Definition of the ObservedProperty of the Datastream is no valid URI.", HttpStatus.CONFLICT);
            }
            try {
                SensorThingsService service = sensorThingsServiceFactory.build(new URL(dsWrapper.getString()));
                service.create(frostds);
                ds = new Datastream(frostds);
            } catch (ServiceFailureException e) {
                LogManager.getInstance().writeToLog("Could not create Datastream.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Failed to create Datastream on server.", HttpStatus.NOT_FOUND);
            } catch (MalformedURLException e) {
                LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
            } catch (URISyntaxException e) {
                LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
            }
        }

        return new ResponseEntity<>(ds, HttpStatus.OK);

    }

    
    @RequestMapping(value = "/datastream/single", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@RequestParam int id, @RequestParam boolean isMulti, @RequestParam String url) throws MalformedURLException {
    	URL frostUrl = new URL(url);
        Datastream ds;
        if (isMulti) {
            MultiDatastream frostMDs;
            try {
                SensorThingsService service = sensorThingsServiceFactory.build(frostUrl);
                frostMDs = service.multiDatastreams().find(id);
            } catch (ServiceFailureException e) {
                LogManager.getInstance().writeToLog("Could not retrieve MultiDatastream.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Failed to find MultiDatastream on server.", HttpStatus.NOT_FOUND);
            } catch (MalformedURLException e) {
                LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
            } catch (URISyntaxException e) {
                LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
            }


            if (frostMDs == null) {
                LogManager.getInstance().writeToLog("Requested MultiDatastream does not exist.", true);
                ErrorHandler.getInstance().addRows(-1, new NullPointerException());
                return new ResponseEntity<>("Requested MultiDatastream does not exist.", HttpStatus.NOT_FOUND);
            }

            try {
                ds = new Datastream(frostMDs);
            } catch (ServiceFailureException e) {
                LogManager.getInstance().writeToLog("Could not convert MultiDatastream to internal representation.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Could not convert MultiDatastream to internal representation.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        else {
            de.fraunhofer.iosb.ilt.sta.model.Datastream frostDs;
            try {
                SensorThingsService service = sensorThingsServiceFactory.build(frostUrl);
                frostDs = service.datastreams().find(id);
            } catch (ServiceFailureException e) {
                LogManager.getInstance().writeToLog("Could not retrieve Datastream.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Failed to find Datastream on server.", HttpStatus.NOT_FOUND);
            } catch (MalformedURLException e) {
                LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
            } catch (URISyntaxException e) {
                LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
            }

            if (frostDs == null) {
                LogManager.getInstance().writeToLog("Requested Datastream does not exist.", true);
                ErrorHandler.getInstance().addRows(-1, new NullPointerException());
                return new ResponseEntity<>("Requested Datastream does not exist.", HttpStatus.NOT_FOUND);
            }

            try {
                ds = new Datastream(frostDs);
            } catch (ServiceFailureException e) {
                LogManager.getInstance().writeToLog("Could not convert Datastream to internal representation.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("Could not convert Datastream to internal representation.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        return new ResponseEntity<>(ds, HttpStatus.OK);
    }

    
    @RequestMapping(value = "/datastream/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(@RequestParam int thingId, @RequestParam String url) throws MalformedURLException {
    	URL frostUrl = new URL(url);
        EntityList<de.fraunhofer.iosb.ilt.sta.model.Datastream> frostDsList;
        EntityList<MultiDatastream> frostMdsList;
        try {
            SensorThingsService service = sensorThingsServiceFactory.build(frostUrl);
            frostDsList = service.things().find(thingId).datastreams().query().list();
            frostMdsList = service.things().find(thingId).multiDatastreams().query().list();
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Could not retrieve Datastreams  of Thing (Id: " + thingId + ").", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Failed to find Datastreams of Thing (Id: " + thingId + ") on server.", HttpStatus.NOT_FOUND);
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        }
        List<Datastream> datastreams = new ArrayList<>();
        for (de.fraunhofer.iosb.ilt.sta.model.Datastream frostDs : frostDsList) {
            try {
                datastreams.add(new Datastream(frostDs));
            } catch (ServiceFailureException e) {
                LogManager.getInstance().writeToLog("A Datastream could not be converted to internal representation.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("A Datastream could not be converted to internal representation.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        for (MultiDatastream frostMds : frostMdsList) {
            try {
                datastreams.add(new Datastream(frostMds));
            } catch (ServiceFailureException e) {
                LogManager.getInstance().writeToLog("A MultiDatastream could not be converted to internal representation.", true);
                ErrorHandler.getInstance().addRows(-1, e);
                return new ResponseEntity<>("A MultiDatastream could not be converted to internal representation.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(datastreams, HttpStatus.OK);
    }

}
