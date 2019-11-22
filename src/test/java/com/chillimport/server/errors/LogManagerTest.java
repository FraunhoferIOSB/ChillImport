package com.chillimport.server.errors;

import org.junit.Before;
import org.junit.Test;

import com.chillimport.server.FileManager;
import com.chillimport.server.TestSetup;

import javax.script.ScriptException;
import java.io.*;

import static org.junit.Assert.assertTrue;


public class LogManagerTest {

    LogManager log;
    private static String testpath;
    private static String sep = File.separator;

    @Before
    public void setUp() throws Exception {
    	testpath = "src" + sep + "test" + sep + "resources";
    	//FileManager.setPathsOnStartup(testpath);
    	TestSetup.setup();
    	
        log = LogManager.getInstance();
        log.clear();
        log = LogManager.getInstance();
        
        
    }

    @Test
    public void writeToLog() throws NoSuchMethodException, ScriptException, IOException {
        log.writeToLog("test", true);
        log.writeToLog("test2", false);
        File file = new File(log.getLogPath());
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
        String[] split = string.split(" ");
        // There's always 2 "space" between date and severity level, but variable amounts of spaces
        // between severity and message so we need to take a look at the last and the second string
        assertTrue(split[split.length - 1].equals("test") && split[2].equals("SEVERE"));
        try {
            string = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        split = string.split(" ");
        assertTrue(split[split.length - 1].equals("test2") && split[2].equals("INFO"));
    }

}
