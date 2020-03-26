package com.chillimport.server;

import static com.chillimport.server.parser.DataTypeParser.convertRow;

import com.chillimport.server.config.Configuration;
import com.chillimport.server.config.MagicNumberMap;
import com.chillimport.server.config.StreamObservation;
import com.chillimport.server.controller.ImportController;
import com.chillimport.server.converter.*;
import com.chillimport.server.errors.ErrorHandler;
import com.chillimport.server.errors.LogManager;
import com.chillimport.server.parser.DataTypeParser;
import com.chillimport.server.parser.TimeParser;
import com.chillimport.server.utility.UnsupportedDataTypeException;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import de.fraunhofer.iosb.ilt.sta.service.TokenManager;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class UploadHandler {

  private ErrorHandler errorHandler;
  private int curr = 0;
  private int size = -1;

  public UploadHandler(ImportController controller) {

    this.errorHandler = ErrorHandler.getInstance();
  }

  /**
   * Creates a textual file preview for the first few lines of a File
   *
   * @param file         the file to create the preview from
   * @param sampleConfig the configuration to use which gives the number of
   *     headerlines
   *
   * @return A 2-dim List of Strings containing all values
   *
   * @throws IOException if the file is not of type excel or CSV
   * @throws InvalidFormatException
   */
  public static ArrayList<ArrayList<String>> preview(File file,
                                                     Configuration sampleConfig)
      throws IOException, InvalidFormatException {
    ArrayList<ArrayList<String>> firstThreeRowsOfTable;

    switch (sampleConfig.getDataType()) {
    case EXCEL:
      firstThreeRowsOfTable = ExcelConverter.filePreview(file, sampleConfig, 3);
      return firstThreeRowsOfTable;
    case CSV:
      firstThreeRowsOfTable = CSVConverter.filePreview(file, sampleConfig, 3);
      return firstThreeRowsOfTable;
    default:
      throw new IOException();
    }
  }

  private Table convertFile(File file, Configuration cfg)
      throws ConverterException {
    Table table;
    switch (cfg.getDataType()) {
    case EXCEL:
      try {
        table = ExcelConverter.convert(file, cfg);
      } catch (IOException | InvalidFormatException e) {
        throw new ConverterException();
      }
      break;
    case CSV:
      try {
        table = CSVConverter.convert(file, cfg);
      } catch (IOException e) {
        throw new ConverterException();
      }
      break;
    default:
      throw new ConverterException();
    }

    return table;
  }

  public void upload(File file, Configuration cfg)
      throws IOException, URISyntaxException, UnsupportedDataTypeException,
             IndexOutOfBoundsException {
    Table table = this.convertFile(file, cfg);

    SensorThingsService service = new SensorThingsService(cfg.getFrostURL());

    if (FileManager.getUsername() != null) {
      this.authenticate(service,
                        cfg.getFrostURL()); // tries to add username and
                                            // password to the Service
    }

    this.size = table.getRowCount();

    Iterator<ArrayList<Cell>> iterator = table.rowIterator();
    int threads = Runtime.getRuntime().availableProcessors();
    Table[] tables = new Table[threads];

    for (int i = 0; i < threads; i++) {
      tables[i] = new Table();
    }

    int threadsize = table.getRowCount() / threads;
    int temp = 0;
    int position = 0;
    while (iterator.hasNext()) {
      if (temp >= threadsize) {
        if (position != threads - 1) {
          position++;
        }
        temp = 0;
      }
      ArrayList<Cell> row = iterator.next();
      tables[position].appendRow(row);
      temp++;
    }
    int threadpos = 0;
    final CountDownLatch executionCompleted = new CountDownLatch(threads);
    for (int i = 0; i < threads; i++) {
      int x = threadpos++;
      Table tempTable = tables[x];
      int disp = x * threadsize;
      Thread thread = new Thread(() -> {
        try {
          processTable(service, cfg, tempTable, disp);
        } catch (ServiceFailureException e) {
          e.printStackTrace();
        } catch (InvalidFormatException e) {
          e.printStackTrace();
        } finally {
          executionCompleted.countDown();
        }
      });
      thread.start();
    }

    try {
      executionCompleted.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    curr = size;
    errorHandler.returnRows(
        table, cfg); // Übersprungene Zeilen und Exceptions schreiben
  }
  /**
   * adds a username and password using HTTP Basic Authentication to the FROST
   * Client <p> The username and password are stored in the servers base
   * directory and loaded from there. Then they are encoded using Base64 and
   * added to a header of a new HTTP Request. The header is then added to the
   * TokenManager of the FROST Client
   *
   * @param service the service to add the password to
   *
   * @throws MalformedURLException when the request method is not GET, PUT, POST
   *     or something else
   */
  private void authenticate(SensorThingsService service, URL frostUrl)
      throws MalformedURLException {
    // to see how this works read the Javadoc ;-)

    TokenManager tm = service.getTokenManager();
    String username = new String(
        Base64.getEncoder().encode(FileManager.getUsername().getBytes()));

    DefaultHttpRequestFactory factory = new DefaultHttpRequestFactory();
    try {

      HttpRequest req = factory.newHttpRequest("GET", frostUrl.toString());
      req.addHeader("Authentication", "Basic " + username);
      tm.addAuthHeader(req);

    } catch (MethodNotSupportedException e) {

      LogManager.getInstance().writeToLog(
          "FROST CLient: HTTP Authentication failed", false);
    }
  }

  /**
   * Processes a table, i.e. evaluates each row of the given table with the data
   * of the configuration. Needs the service for checking the datatypes of the
   * specified datastreams.
   *
   * @param service the SensorThingsService
   * @param cfg     the configuration
   * @param table   the table
   * @throws InvalidFormatException
   */
  private void processTable(SensorThingsService service, Configuration cfg,
                            Table table, int displacement)
      throws ServiceFailureException, InvalidFormatException {

    Iterator<ArrayList<Cell>> iterator = table.rowIterator();

    StreamObservation[] observationColumns = cfg.getStreamData();
    TableDataTypes[] newDataTypes = null;
    ArrayList<String> dsTypes = new ArrayList<>();
    ArrayList<Integer> positions = new ArrayList<>();

    for (StreamObservation stream : observationColumns) {

      if (stream.isMultiStream()) {
        MultiDatastream mds = service.multiDatastreams().find(stream.getDsID());
        dsTypes.addAll(mds.getMultiObservationDataTypes());
        for (int pos : stream.getObservations()) {
          positions.add(pos);
        }
      } else {
        Datastream ds = service.datastreams().find(stream.getDsID());
        dsTypes.add(ds.getObservationType());
        positions.add(stream.getObservations()[0]);
      }
      newDataTypes = DataTypeParser.convertDataTypesToTableDataTypes(
          dsTypes); // Datentypen abrufen und konvertieren
    }

    if (newDataTypes != null && newDataTypes.length != positions.size()) {
      throw new InvalidFormatException(
          "Number of datastreams columns does not match number of actual observation types of all datastreams.");
    }
    final TableDataTypes[] NEWTYPES = newDataTypes;
    int current = 0;
    while (iterator.hasNext()) {
      ArrayList<Cell> row = iterator.next();
      ArrayList<Cell> converted = new ArrayList<>();
      try {
        converted = convertRow(row, newDataTypes, positions);
        evaluateRow(service, cfg, converted, NEWTYPES);
      } catch (ServiceFailureException | DateTimeException |
               ClassCastException | InvalidFormatException |
               IllegalArgumentException e) {
        errorHandler.addRows(current + displacement, e);
      } finally {
        curr++;
        current++;
      }
    }
  }

  public int currentRow() { return this.curr; }

  public int getSize() { return this.size; }

  /**
   * Evaluates a row from a table given as List of Objects and creates the
   * observations for the streams as specified in the configuration if they do
   * not already exist (duplicate on service).
   *
   * @param service the SensorThingsService of the streams
   * @param cfg     the configuration
   * @param row     the row
   *
   * @throws DateTimeException       if parsing of the ZonedDateTime in the row
   *     fails
   * @throws ServiceFailureException if creating an observation on the service
   *     or finding a duplicate fails
   */
  private void evaluateRow(SensorThingsService service, Configuration cfg,
                           ArrayList<Cell> row, TableDataTypes[] types)
      throws DateTimeException, ServiceFailureException, InvalidFormatException,
             NullPointerException, IllegalArgumentException {

    ZonedDateTime zdt = TimeParser.toZonedDateTime(cfg, row);

    int typeCounter = 0;
    ArrayList<TableDataTypes> streamtypes = new ArrayList<>();
    StreamObservation[] streams = cfg.getStreamData();
    MultiDatastream mds;
    Datastream ds;
    ArrayList<Object> resultList = new ArrayList<>();
    Object result;
    Object[] resultArray;
    MagicNumberMap[] allMaps = cfg.getMapOfMagicNumbers();

    for (StreamObservation str : streams) {
      streamtypes.clear();
      resultList.clear();

      for (int col : str.getObservations()) {
        Cell c = row.get(col);

        for (MagicNumberMap map : allMaps) { // Vergleichen mit allen Maps
          if (map.getColumn() == col) {      // Nur wenn die Spalte passt
            if (c.toString().equals(map.getFirst())) { // Wenn der String passt
              c = new Cell(map.getSecond());           // ersetzen
              break;
            }
          }
        }
        streamtypes.add(types[typeCounter++]);
        resultList.add(c.get());
      }

      if (str.isMultiStream()) {
        mds = service.multiDatastreams().find(str.getDsID());
        resultArray = ObservationCreator.combineResults(resultList);

        if (!DuplicateFinder.find(mds, zdt, resultArray, streamtypes)) {
          service.create(ObservationCreator.create(mds, resultArray, zdt));
        }
      } else {
        ds = service.datastreams().find(str.getDsID());
        result = resultList.get(0);
        if (!DuplicateFinder.find(ds, zdt, result, streamtypes)) {
          service.create(ObservationCreator.create(ds, result, zdt));
        }
      }
    }
  }
}
