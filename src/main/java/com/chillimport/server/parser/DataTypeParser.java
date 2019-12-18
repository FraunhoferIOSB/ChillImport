package com.chillimport.server.parser;

import java.util.ArrayList;
import java.util.Iterator;

import com.chillimport.server.Cell;
import com.chillimport.server.TableDataTypes;


/**
 * Adapts the Table's Cells so that they can be uploaded easily and without cast errors to the FROST server. This class changes the Cells DataTypes to
 * the ones needed by the FROST server.
 */
public class DataTypeParser {


    //  private Table t;


    /**
     * Creates a new Parser with a given Table
     *
     * @param t the Table to use
     */
   /* public DataTypeParser(Table t) {
        this.t = t;
    }
*/

    /**
     * Gets and returns a clone of the Table so that the original table is not further modified
     *
     * @return the cloned value of the Table
     */
   /* public Table getTable() {
        return t;
    }
*/

    /**
     * Changes -if possible or necessary- the data types of the Cells of a complete Table that will be uploaded to the FROST into the data type needed
     * by the Datastreams on the server.
     *
     * @param cfg The Configuration to use. The Configuration tells us which Cells will be processed and which datastream(s) to use.
     *
     * @throws MalformedURLException   when the server URL is malformed in the Configuration
     * @throws URISyntaxException      when the server URL is just plain wrong (wrong syntax)
     * @throws ServiceFailureException if the FROST Client fails
     */
    /*
    public void convertTable(Configuration cfg) throws MalformedURLException, URISyntaxException, ServiceFailureException {

        //TODO Fragen welche String Werte möglich sind im observationType von Datastream.java in FROST Client
        //TODO Fragen wieso Observation.java Object entgegennimmt


        StreamObservation[] observationColumns = cfg.getStreamData(); //speichert welche spalte welche observation für einen datastream enthält,
        // für jeden stream

        URL url = FileManager.getServerURL();
        SensorThingsService sts = new SensorThingsService(url);

        for (int i = 0; i < observationColumns.length; i++) {
            TableDataTypes[] newDataTypes;

            if (observationColumns[i].isMultiStream()) {
                MultiDatastream mds = sts.multiDatastreams().find(observationColumns[i].getDsID());
                ArrayList<String> dsTypes = new ArrayList<>(mds.getMultiObservationDataTypes());
                newDataTypes = this.convertDataTypesToTableDataTypes(dsTypes); //Datentypen abrufen und konvertieren
            }
            else {
                Datastream ds = sts.datastreams().find(observationColumns[i].getDsID());
                ArrayList<String> dsTypes = new ArrayList<>();
                dsTypes.add(ds.getObservationType());
                newDataTypes = this.convertDataTypesToTableDataTypes(dsTypes); //selbes für Single-DS
            }

            Iterator<ArrayList<Cell>> rowIterator = t.rowIterator();

            int j = 0;
            while (rowIterator.hasNext()) {
                ArrayList<Cell> row = rowIterator.next();
                ArrayList<TableDataTypes> rowDataTypes = new ArrayList<>();
                for (Cell c : row) {
                    rowDataTypes.add(c.getCellType());
                }
                try {
                    convertRow(row, rowDataTypes, newDataTypes, observationColumns[i].getObservations());
                } catch (ClassCastException e) {
                    ErrorHandler.getInstance().addRows(j, e);
                }
                j++;
                //TODO Testen ob sie die Row tatsächlich ändert
            }
        }
    }
    */


    /**
     * Changes the Cell's Data Types for a single row
     *
     * @param row          the row to convert
     * @param newDataTypes an array containing the needed data types
     * @param positions    the positions of the Cells that will be uploaded and need to be comverted
     *
     * @return a copy of the row with converted cells
     *
     * @throws ClassCastException if a Cell in the row could not be converted
     */
    public static ArrayList<Cell> convertRow(ArrayList<Cell> row, TableDataTypes[] newDataTypes, ArrayList<Integer>
            positions) throws ClassCastException {

        ArrayList<Cell> convertedRow = (ArrayList<Cell>) row.clone();

        for (int i = 0; i < positions.size(); i++) {
            int cellPos = positions.get(i);
            TableDataTypes newType = newDataTypes[i];

            Cell c = row.get(cellPos); //TODO Testen ob die richtigen Cells verändert werden

            switch (newType) {
                case INT:
                    convertedRow.set(cellPos, new Cell(c.toInteger()));
                    break;
                case BOOL:
                    convertedRow.set(cellPos, new Cell(c.toBoolean()));
                    break;
                case DOUBLE:
                    convertedRow.set(cellPos, new Cell(c.toDouble()));
                    break;
                case STRING:
                    convertedRow.set(cellPos, new Cell(c.toString()));
                    break;
                case URI:
                    convertedRow.set(cellPos, new Cell(c.toURI()));
                    break;
                case URL:
                    convertedRow.set(cellPos, new Cell(c.toURL()));
                    break;
                case DATE:
                    convertedRow.set(cellPos, new Cell(c.toDate()));
                    break;
                case FLOAT:
                    convertedRow.set(cellPos, new Cell(c.toFloat()));
                    break;
                case NULL:
                    convertedRow.set(cellPos, new Cell());
                    break;
            }


        }
        return convertedRow;
    }


    /**
     * Matches the FROST server data types to the data types used in a Table
     *
     * @param dsTypes a list containing the FROST data types
     *
     * @return an array containing the corresponding Table data types
     */
    public static TableDataTypes[] convertDataTypesToTableDataTypes(ArrayList<String> dsTypes) {
        Iterator<String> iterator = dsTypes.iterator();
        TableDataTypes[] newDataTypes = new TableDataTypes[dsTypes.size()];
        int pos = 0;

        while (iterator.hasNext()) {
            String observationTypeString = iterator.next();

            switch (observationTypeString) { //TODO cases zusammenfassen hylke fragen
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation (URL)":
                    //URI
                    newDataTypes[pos] = TableDataTypes.URI;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation":
                    newDataTypes[pos] = TableDataTypes.ANY;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation (Integer)":
                    //Integer
                    newDataTypes[pos] = TableDataTypes.INT;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_DiscreteCoverageObservation":
                    newDataTypes[pos] = TableDataTypes.ANY;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_DiscretePointCoverageObservation":
                    newDataTypes[pos] = TableDataTypes.ANY;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_DiscreteTimeSeriesObservation":
                    newDataTypes[pos] = TableDataTypes.ANY;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_GeometryObservation":
                    newDataTypes[pos] = TableDataTypes.ANY;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement (Double)":
                    //Double
                    newDataTypes[pos] = TableDataTypes.DOUBLE;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation (Any)":
                    newDataTypes[pos] = TableDataTypes.ANY;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TemporalObservation":
                    newDataTypes[pos] = TableDataTypes.ANY;
                    break;
                case "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation (Boolean)":
                    //Boolean
                    newDataTypes[pos] = TableDataTypes.BOOL;
                    break;
                default:
                    newDataTypes[pos] = TableDataTypes.ANY;
                    break;
            }
            pos++;
        }

        return newDataTypes;
    }
}