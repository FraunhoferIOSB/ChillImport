package com.chillimport.server.errors;

import com.chillimport.server.Cell;
import com.chillimport.server.FileManager;
import com.chillimport.server.Table;
import com.chillimport.server.TestSetup;
import com.chillimport.server.config.ConfigurationManager;
import com.chillimport.server.converter.ExcelConverter;
import com.chillimport.server.utility.UnsupportedDataTypeException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class ErrorHandlerTest {

    ErrorHandler eh;

    @Before
    public void before() throws Exception {
    	TestSetup.setup();
    	
        eh = ErrorHandler.getInstance();
    }

    @After
    public void tearDown() {
        eh.clear();
    }

    
    @Test
    public void write() {
        assertNotNull(eh);
        eh.addRows(17, new IndexOutOfBoundsException());
        eh.addRows(24, new UnsupportedDataTypeException());
        eh.write();
        File file = null;
        file = new File(eh.getPath());
        BufferedReader br = null;
        String string = null;
        assertTrue(file.exists() && !file.isDirectory());
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            string = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(string.equals(
                "Row : 17 Occured Error : java.lang.IndexOutOfBoundsException"));
        try {
            string = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(string.equals(
                "Row : 24 Occured Error : com.chillimport.server.utility.UnsupportedDataTypeException"));
    }
    
    
    @Test
    public void returnRowsCSV() throws IOException {
        Table t = new Table();
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell("Rosen sind rot"));
        list.add(new Cell("Veilchen sind Blau"));
        list.add(new Cell("Ich hasse Gedichte"));
        list.add(new Cell("Klopapier"));
        t.appendRow(list);
        ArrayList<Cell> list2 = new ArrayList<>();
        list2.add(new Cell("Rosen sind rott"));
        list2.add(new Cell("Veilchen sind Blauu"));
        list2.add(new Cell("Ich hasse Gedichtee"));
        list2.add(new Cell("Klopapierr"));
        t.appendRow(list2);
        eh.addRows(0, new IOException());
        eh.addRows(1, new IOException());
        try {
            eh.returnRows(t, ConfigurationManager.loadConfig(-1365040327));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedDataTypeException e) {
            e.printStackTrace();
        }
        File file = new File(FileManager.getLogPath() + "/returnRows/" + LogManager.getInstance().getDate() + "--skippedRows.csv");
        BufferedReader br = null;
        String string = null;
        assertTrue(file.exists() && !file.isDirectory());
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        string = br.readLine();
        assertTrue(string.equals("#There are no easter eggs up here"));
        string = br.readLine();
        assertTrue(string.equals("#"));
        string = br.readLine();
        assertTrue(string.equals("Rosen sind rot;Veilchen sind Blau;Ich hasse Gedichte;Klopapier;null"));
        string = br.readLine();
        assertTrue(string.equals("Rosen sind rott;Veilchen sind Blauu;Ich hasse Gedichtee;Klopapierr;null"));
    }
    
    
    @Test
    public void returnRowsExcel () throws IOException {
        Table t = new Table();
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell("Rosen sind rot"));
        list.add(new Cell("Veilchen sind Blau"));
        list.add(new Cell("Ich hasse Gedichte"));
        list.add(new Cell("Klopapier"));
        t.appendRow(list);
        ArrayList<Cell> list2 = new ArrayList<>();
        list2.add(new Cell("Rosen sind rott"));
        list2.add(new Cell("Veilchen sind Blauu"));
        list2.add(new Cell("Ich hasse Gedichtee"));
        list2.add(new Cell("Klopapierr"));
        t.appendRow(list2);
        eh.addRows(0, new IOException());
        eh.addRows(1, new IOException());
        try {
            eh.returnRows(t, ConfigurationManager.loadConfig(48573894));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedDataTypeException e) {
            e.printStackTrace();
        }
        File file = new File(FileManager.getLogPath() + "/returnRows/" + LogManager.getInstance().getDate() + "--skippedRows.xls");
        Table t2;
		try {
			t2 = ExcelConverter.convert(file,ConfigurationManager.loadConfig(48573894));
		} catch (InvalidFormatException e) {
			throw new IOException();
		}
        assertEquals(t2.getRow(0).toString(),"[Rosen sind rot, Veilchen sind Blau, Ich hasse Gedichte, Klopapier, null, null]");
        assertEquals(t2.getRow(1).toString(),"[Rosen sind rott, Veilchen sind Blauu, Ich hasse Gedichtee, Klopapierr, null, null]");


    }
    
    @Test
    public void returnFiles() {
        String x = eh.returnFiles();
        assertTrue(x.contains("2018-08-21T15;56;45--skippedRows.csv"));
        assertTrue(x.contains("2018-08-21T16;33;11--skippedRows.csv"));
        assertTrue(x.contains("2018-08-21T23-34-25--skippedRows.csv"));
}
}