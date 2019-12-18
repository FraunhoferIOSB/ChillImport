package com.chillimport.server.parser;

import com.chillimport.server.TableDataTypes;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class DataTypeParserTest {

    @Test
    public void convertTable() throws URISyntaxException, ServiceFailureException, MalformedURLException {
    	new DataTypeParser();

    }

    @Test
    public void convertDataTypesToTableDataTypes() {
        ArrayList<String> stringList = new ArrayList<>();

        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation (URL)");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation (Integer)");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_DiscreteCoverageObservation");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_DiscretePointCoverageObservation");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_DiscreteTimeSeriesObservation");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_GeometryObservation");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement (Double)");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation (Any)");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TemporalObservation");
        stringList.add("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation (Boolean)");

        TableDataTypes[] tdt = DataTypeParser.convertDataTypesToTableDataTypes(stringList);
    }
}