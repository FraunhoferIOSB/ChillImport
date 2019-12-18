package com.chillimport.server;

import java.time.ZonedDateTime;
import java.util.List;

import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.model.Observation;


public class ObservationCreator {

    /**
     * Creates a new observation for the given datastream from a phenomenon time and result.
     *
     * @param stream the datastream
     * @param result the result
     * @param time   the phenomenon time
     *
     * @return the observation
     */
    public static Observation create(Datastream stream, Object result, ZonedDateTime time) throws NullPointerException {
        Observation obs = new Observation(result, stream);
        obs.setPhenomenonTimeFrom(time);
        return obs;
    }

    /**
     * Creates a new observation for the given MultiDatastream from a phenomenon time and result.
     *
     * @param stream the MultiDatastream
     * @param result the result
     * @param time   the phenomenon time
     *
     * @return the observation
     */
    public static Observation create(MultiDatastream stream, Object result, ZonedDateTime time) throws NullPointerException {
        Observation obs = new Observation(result, stream);
        obs.setPhenomenonTimeFrom(time);
        return obs;
    }

    /**
     * Creates a new observation for the given Datastream from a phenomenon time, result time and result.
     *
     * @param stream the Datastream
     * @param result the result
     * @param pTime  the phenomenon time
     * @param rTime  the result time
     *
     * @return the observation
     */
    public static Observation create(Datastream stream, Object result, ZonedDateTime pTime, ZonedDateTime rTime) throws NullPointerException {
        Observation obs = create(stream, result, pTime);
        obs.setResultTime(rTime);
        return obs;
    }

    /**
     * Creates a new observation for the given MultiDatastream from a phenomenon time, result time and result.
     *
     * @param stream the MultiDatastream
     * @param result the result
     * @param pTime  the phenomenon time
     * @param rTime  the result time
     *
     * @return the observation
     */
    public static Observation create(MultiDatastream stream, Object result, ZonedDateTime pTime, ZonedDateTime rTime) throws NullPointerException {
        Observation obs = create(stream, result, pTime);
        obs.setResultTime(rTime);
        return obs;
    }

    /**
     * Combines several given results to one result represented by an array of Objects.
     *
     * @param results the given result
     *
     * @return the combined result
     */
    public static Object[] combineResults(List<Object> results) {
        int size = results.size();
        Object[] res = new Object[size];

        for (int i = 0; i < size; i++) {
            res[i] = results.remove(0);
        }

        return res;
    }
}
