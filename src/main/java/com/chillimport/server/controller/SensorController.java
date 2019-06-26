package com.chillimport.server.controller;

import com.chillimport.server.entities.Sensor;
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
 * Controller class receiving requests for creating or getting Sensors
 */
@RestController
public class SensorController extends EntityController<Sensor> {

    @Autowired
    private SensorThingsServiceFactory sensorThingsServiceFactory;


    @Override
    @RequestMapping(value = "/sensor/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody Sensor sensor) {
        de.fraunhofer.iosb.ilt.sta.model.Sensor frostSensor = sensor.convertToFrostStandard();
        try {
            SensorThingsService service = sensorThingsServiceFactory.build();
            service.create(frostSensor);
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to create Sensor on server.", true);
            return new ResponseEntity<>("Failed to create Sensor on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new Sensor(frostSensor), HttpStatus.OK);
    }

    @Override
    @RequestMapping(value = "/sensor/single", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(@RequestParam int id) {
        Sensor sensor;
        try {
            SensorThingsService service = sensorThingsServiceFactory.build();
            sensor = new Sensor(service.sensors().find(id));
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to find Sensor on server.", true);
            return new ResponseEntity<>("Failed to find Sensor on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(sensor, HttpStatus.OK);

    }

    @Override
    @RequestMapping(value = "/sensor/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAll() {
        EntityList<de.fraunhofer.iosb.ilt.sta.model.Sensor> frostSensors;
        List<Sensor> sensors = new LinkedList<>();
        try {
            SensorThingsService service = sensorThingsServiceFactory.build();
            frostSensors = service.sensors().query().list();
        } catch (MalformedURLException e) {
            LogManager.getInstance().writeToLog("Malformed URL for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Malformed URL for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (URISyntaxException e) {
            LogManager.getInstance().writeToLog("Wrong URI for Frost-Server.", true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("Wrong URI for Frost-Server.", HttpStatus.NOT_FOUND);
        } catch (ServiceFailureException e) {
            LogManager.getInstance().writeToLog("Failed to find Sensors on server.", true);
            return new ResponseEntity<>("Failed to find Sensors on server.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        for (de.fraunhofer.iosb.ilt.sta.model.Sensor frostSensor : frostSensors) {
            sensors.add(new Sensor(frostSensor));
        }
        LogManager.getInstance().writeToLog("Retrieved all sensors", false);
        return new ResponseEntity<>(sensors, HttpStatus.OK);
    }
}
