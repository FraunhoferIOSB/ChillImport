package com.chillimport.server.errors;


import com.chillimport.server.Cell;
import com.chillimport.server.FileManager;
import com.chillimport.server.Table;
import com.chillimport.server.config.Configuration;
import com.chillimport.server.converter.CSVConverter;
import com.chillimport.server.converter.ExcelConverter;
import com.chillimport.server.utility.SkippedRows;
import com.chillimport.server.utility.UnsupportedDataTypeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * The ErrorHandler Class is responsible for saving all occurred Errors and their respective Errors Additionally, it returns all skipped Rows in a new
 * File so that the user can try another import with only these Files.
 */
public class ErrorHandler {

    private static ErrorHandler errorHandler;
    private Queue<SkippedRows> skippedRows;
    private String path;
    private String sep = File.separator;

    private ErrorHandler() {
        skippedRows = new ConcurrentLinkedQueue<>();
        path = LogManager.getInstance().getLogPath().replace("Logging.txt", "ErrorLog.txt");
    }


    /**
     * Returns the ErrorHandler Instance. Creates it first if none was created yet.
     *
     * @return ErrorHandler Instance
     */
    public static ErrorHandler getInstance() {
        if (errorHandler == null) {
            errorHandler = new ErrorHandler();
        }
        return errorHandler;

    }

    /**
     * Returns the Path for filesaving
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the names of all Files in 'returnRows'
     */
    public String returnFiles() {
        ArrayList<String> fileNames = new ArrayList<>();
        File check = new File(FileManager.getLogPath() + sep + "returnRows" + sep);
        if (!Files.exists(Paths.get(check.getAbsolutePath()))) {
            check.mkdirs();
        }

        File[] files = check.listFiles((dir, name) -> (name.endsWith(".xls") || name.endsWith(
                ".csv") || name.endsWith(".xlsx")));
        for (File file : files) {
            fileNames.add(file.getName());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(fileNames);
        } catch (JsonProcessingException e) {
            LogManager.getInstance().writeToLog("Could not parse filenames to json", true);
        }

        return json;
    }

    /**
     * Adds a row Number and the according error to the List
     *
     * @param row   row Index
     * @param error The Exception that occured
     */
    public void addRows(int row, Exception error) {
        skippedRows.add(new SkippedRows(row, error));
    }

    /**
     * Writes all skipped line indices and their Exceptions into a File
     */
    public void write() {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path);
        } catch (FileNotFoundException e) {
            System.out.println("Error File could not be created");
            e.printStackTrace();
            return;
        }
        for (SkippedRows skip : skippedRows) {
            writer.println("Row : " + Integer.toString(skip.getRow()) + " Occured Error : " + skip.getError().toString());
        }
        writer.close();
    }


    /**
     * Writes the original content of all skipped lines into a new File (So the user can try to import it again)
     *
     * @throws FileNotFoundException        Should not occur, happens when the File cannot be found (e.g because it was deleted)
     * @throws UnsupportedEncodingException Should not occur, happens when the Encoding Type is faulty
     */
    public void returnRows(Table table, Configuration cfg) throws IOException, UnsupportedDataTypeException {
        String date = LogManager.getInstance().getDate();
        Table skipped = new Table();
        if (skippedRows.isEmpty()) {
            LogManager.getInstance().writeToLog("No rows were skipped", false);
            return;
        }
        ArrayList<Cell> currentRow;
        for (SkippedRows skip : skippedRows) {
            if (skip.getRow() != -1) { // -1 kann als genereller Fehlercode genutzt werden
                currentRow = table.getRow(skip.getRow());
                String msg = (skip.getError().getMessage() == null) ? "null" : skip.getError().getMessage();
                currentRow.add(new Cell(msg));
                skipped.appendRow(currentRow);
            }
        }
        switch (cfg.getDataType()) {
            case EXCEL:
                ExcelConverter.convertBack(skipped,
                                           cfg,
                                           FileManager.getLogPath() + sep + "returnRows" + sep + date + "--skippedRows.xls");
                break;
            case CSV:
                CSVConverter.convertBack(skipped,
                                         cfg,
                                         FileManager.getLogPath() + sep + "returnRows" + sep + date + "--skippedRows.csv");
                break;
            default:
                LogManager.getInstance().writeToLog("Filetype " + cfg.getDataType() + " not supported", true);
                ErrorHandler.getInstance().addRows(-1, new UnsupportedDataTypeException());
                throw new UnsupportedDataTypeException();
        }
    }

    public int returnSize() {
        return skippedRows.size();
    }
    
    /**
     * Clean up at the end of Upload
     */
    public void clear() {
        errorHandler = null;
    }
}