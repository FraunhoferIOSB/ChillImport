package com.chillimport.server.controller;

import com.chillimport.server.entities.ObservedProperty;
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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


/**
 * Controller class receiving requests for creating or getting ObservedProperties
 */
@RestController
public class ObservedPropertyController extends EntityController<ObservedProperty> {

    @Autowired
    private SensorThingsServiceFactory sensorThingsServiceFactory;

    @Override
    @RequestMapping(value = "/observedProperty/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody ObservedProperty obsProp) {
        de.fraunhofer.iosb.ilt.sta.model.ObservedProperty frostObsProp;

        try {
            frostObsProp = obsProp.convertToFrostStandard();
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Definition of the ObservedProperty is no valid URI.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Definition of the ObservedProperty is no valid URI.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            SensorThingsService service = sensorThingsServiceFactory.build();
            service.create(frostObsProp);
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to create ObservedProperty on server.", true);
            return new ResponseEntity<>("Failed to create ObservedProperty on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(frostObsProp, HttpStatus.OK);
    }


    @Override
    @RequestMapping(value = "/observedProperty/single", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(int id) {
        ObservedProperty observedProperty;
        try {
            SensorThingsService service = sensorThingsServiceFactory.build();
            observedProperty = new ObservedProperty(service.observedProperties().find(id));
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to find ObservedProperty on server.", true);
            return new ResponseEntity<>("Failed to find ObservedProperty on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(observedProperty, HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/observedProperty/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAll() {
        EntityList<de.fraunhofer.iosb.ilt.sta.model.ObservedProperty> frostObsProps;
        List<ObservedProperty> observedProperties = new LinkedList<>();
        try {
            SensorThingsService service = sensorThingsServiceFactory.build();
            frostObsProps = service.observedProperties().query().list();
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to find ObservedProperties on server.", true);
            return new ResponseEntity<>("Failed to find ObservedProperties on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }


        for (de.fraunhofer.iosb.ilt.sta.model.ObservedProperty frostObsProp : frostObsProps) {
            observedProperties.add(new ObservedProperty(frostObsProp));
        }
        LogManager.getInstance().writeToLog("Retrieved all Observed Properties", false);
        return new ResponseEntity<>(observedProperties, HttpStatus.OK);
    }
}
