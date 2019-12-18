package com.chillimport.server.utility;

import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


@Service
public class SensorThingsServiceFactory {

    public SensorThingsService build(URL frostUrl) throws MalformedURLException, URISyntaxException {

        return new SensorThingsService(frostUrl);

    }
}
