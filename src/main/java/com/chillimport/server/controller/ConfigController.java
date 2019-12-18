package com.chillimport.server.controller;

import com.chillimport.server.config.Configuration;
import com.chillimport.server.config.ConfigurationManager;
import com.chillimport.server.errors.ErrorHandler;
import com.chillimport.server.errors.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


/**
 * Controller class receiving requests for creating, saving or loading Configurations
 */
@RestController
public class ConfigController {


    @RequestMapping(value = "config/create", method = RequestMethod.POST)
    public ResponseEntity<?> createAndSaveConfiguration(@RequestBody String formData) {
        if (formData == null) {
            return new ResponseEntity<>("No data was passed, could not create Configuration", HttpStatus.NOT_FOUND);
        }
        Configuration cfg;
        try {
            cfg = Configuration.convertToJava(formData);
        } catch (IOException e) {
            LogManager.getInstance().writeToLog(e.getMessage(), true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("The server encountered an error while creating the Configuration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        cfg.setId(java.util.UUID.randomUUID().hashCode());
        try {
            ConfigurationManager.saveConfig(cfg);
        } catch (IOException e) {
            LogManager.getInstance().writeToLog(e.getMessage(), true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("The server could not save the Configuration due to an I/O Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LogManager.getInstance().writeToLog("Saved Configuration", false);
        return new ResponseEntity<>(cfg, HttpStatus.OK);

    }

    @RequestMapping(value = "/config/single", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getConfiguration(@RequestParam int configId) {
        Configuration configuration;
        try {
            configuration = ConfigurationManager.loadConfig(configId);
        } catch (IOException e) {
            LogManager.getInstance().writeToLog(e.getMessage(), true);
            ErrorHandler.getInstance().addRows(-1, e);
            return new ResponseEntity<>("The server encountered an Error while loading the Configuration", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (configuration == null) {
            LogManager.getInstance().writeToLog("Could not load Configuration", true);
            ErrorHandler.getInstance().addRows(-1, new Exception("Configuration is null"));
            return new ResponseEntity<>("The server could not find the Configuration", HttpStatus.NOT_FOUND);
        }

        LogManager.getInstance().writeToLog("Loaded Configuration", false);
        return new ResponseEntity<>(configuration, HttpStatus.OK);

    }

    @RequestMapping(value = "/config/all", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getConfigurations() {
        List<Configuration> configurations = ConfigurationManager.listAll();
        if (configurations.isEmpty()) {
            return new ResponseEntity<>("No configurations saved on the server.", HttpStatus.NOT_FOUND);
        }
        LogManager.getInstance().writeToLog("Loaded " + configurations.size() + " configurations", false);
        return new ResponseEntity<>(configurations, HttpStatus.OK);
    }
}
