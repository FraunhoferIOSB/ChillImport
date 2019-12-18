package com.chillimport.server;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.time.ZonedDateTime;
import java.util.ArrayList;


public class DuplicateFinder {

    /**
     * Searches the given SensorThingsService for an Observation with the specified phenomenon time and result.
     *
     * @param service the SensorThingsService
     * @param time    the phenomenon time
     * @param result  the result
     *
     * @return whether such an observation exists
     *
     * @throws ServiceFailureException if inquiry to the server fails
     */
    public static boolean find(SensorThingsService service, ZonedDateTime time, Object result, ArrayList<TableDataTypes> types) throws ServiceFailureException {
        EntityList<Observation> observations;
        try {
            String filter = (types.get(0) == TableDataTypes.STRING || types.get(0) == TableDataTypes.ANY) ? "result eq '" + result.toString() + "' and phenomenonTime eq " + time.toInstant().toString() : "result eq " + result.toString() + " and phenomenonTime eq " + time.toInstant().toString();
            observations = service.observations().query().filter(filter).list();
            if (observations.size() > 0) {
                return true;
            }
            //observations = service.observations().query().list();
        } catch (ServiceFailureException e) {
            throw new ServiceFailureException("Duplicate check failed: Could not retrieve Observations from server.", e);
        } catch (IllegalArgumentException e) {
            throw new ServiceFailureException(e.getMessage(),e);
        }

        /*
        Iterator<Observation> iterator = observations.fullIterator();

        while (iterator.hasNext()) {
            Observation obs = iterator.next();
            if (obs.getResult().toString().equals(result.toString()) && obs.getPhenomenonTime().toString().equals(time.toInstant().toString())) {
                return true;
            }
        }
        */

        return false;
    }

    /**
     * Searches the Observations of the given Datastream on the FROST-Server for a duplicate with the specified information.
     *
     * @param stream the Datastream
     * @param time   the phenomenon time of the observation
     * @param result the result of the observation
     *
     * @return whether an observation with these parameters exists
     *
     * @throws ServiceFailureException if inquiry to the server fails
     */
    public static boolean find(Datastream stream, ZonedDateTime time, Object result, ArrayList<TableDataTypes> types) throws ServiceFailureException {

        EntityList<Observation> observations;
        try {
            String filter = (types.get(0) == TableDataTypes.STRING || types.get(0) == TableDataTypes.ANY) ? "result eq '" + result.toString() + "' and phenomenonTime eq " + time.toInstant().toString() : "result eq " + result.toString() + " and phenomenonTime eq " + time.toInstant().toString();
            observations = stream.observations().query().filter(filter).list();
            if (observations.size() > 0) {
                return true;
            }
            //observations = stream.observations().query().list();
        } catch (ServiceFailureException e) {
            throw new ServiceFailureException("Duplicate check failed: Could not retrieve Observations of Datastream " + stream.getName() + " (" + stream.getId() + ").",
                                              e);
        } catch (IllegalArgumentException e) {
            throw new ServiceFailureException(e.getMessage(),e);
        }
        /*
        Iterator<Observation> iterator = observations.fullIterator();
        while (iterator.hasNext()) {
            Observation obs = iterator.next();
            System.out.println(obs.getResult().toString() + ',' + obs.getPhenomenonTime().toString());
            if (obs.getResult().toString().equals(result.toString()) && obs.getPhenomenonTime().toString().equals(time.toInstant().toString())) {
                return true;
            }
        }
        */
        return false;
    }

    /**
     * Searches the Observations of the given MultiDatastream on the FROST-Server for a duplicate with the specified information.
     *
     * @param stream the MultiDatastream
     * @param time   the phenomenon time of the observation
     * @param result the result of the observation
     *
     * @return whether an observation with these parameters exists
     *
     * @throws ServiceFailureException if inquiry to the server fails
     */
    public static boolean find(MultiDatastream stream, ZonedDateTime time, Object[] result,ArrayList<TableDataTypes> types) throws ServiceFailureException, InvalidFormatException {
        if (result.length < 2) {
            throw new InvalidFormatException("Result of MultiDatastream has too few entries.");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("phenomenonTime eq ");
        builder.append(time.toInstant().toString());
        builder.append(" and result[0] eq ");
        if (types.get(0) == TableDataTypes.STRING || types.get(0) == TableDataTypes.ANY) {
            builder.append("'");
            builder.append(result[0]);
            builder.append("'");
        } else {
            builder.append(result[0]);
        }
        for (int i = 1; i < result.length; i++) {
            builder.append(" and result[");
            builder.append(i);
            builder.append("] eq ");
            if (types.get(i) == TableDataTypes.STRING || types.get(i) == TableDataTypes.ANY) {
                builder.append("'");
                builder.append(result[i]);
                builder.append("'");
            } else {
                builder.append(result[i]);
            }
        }
        String filterString = builder.toString();

        EntityList<Observation> observations;
        try {
            observations = stream.observations().query().filter(filterString).list();
            if (observations.size() > 0) {
                return true;
            }
            //observations = stream.observations().query().list();
        } catch (ServiceFailureException e) {
            throw new ServiceFailureException("Duplicate check failed: Could not retrieve Observations of MultiDatastream " + stream.getName() + " (" + stream.getId() + ").",
                                              e);
        } catch (IllegalArgumentException e) {
            throw new ServiceFailureException(e.getMessage(),e);
        }

        /*
        Iterator<Observation> iterator = observations.fullIterator();
        while (iterator.hasNext()) {
            Observation obs = iterator.next();
            if (obs.getResult().toString().equals(res) && obs.getPhenomenonTime().toString().equals(time.toInstant().toString())) {
                return true;
            }
        }
        */
        return false;
    }
}
