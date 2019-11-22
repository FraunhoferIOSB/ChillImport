package com.chillimport.server;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;


public class FileManagerTest {

    FileManager fm = new FileManager();
    private static String testpath;
    private static String sep = File.separator;
    
    @BeforeClass 
    public static void beforeClass() throws Exception {
    	//FileManager.setServerURLOnStartup(FrostSetup.getFrostURL());
    	FrostSetup.getFrostURL();
    	
    	testpath = "src" + sep + "test" + sep + "resources";
    	//FileManager.setPathsOnStartup(testpath);
    	TestSetup.setup();
    }
    
    
    @Test
    public void store() {
        byte[] array = "test string".getBytes();

        MockMultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                array);


        this.fm.store(file);
    }

    @Test
    public void load() throws FileNotFoundException {

        byte[] array = "test string".getBytes();

        MockMultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                array);


        String name = this.fm.store(file);

        Path path = fm.load(name);

        Assert.assertEquals(path.toFile().exists(), true);

    }
    @Ignore //invalid url
    @Test
    public void storeFromURL() {
        String url = "https://raw.githubusercontent.com/uzkns/beispielcsv/master/Messergebnisse.xlsx";

        fm.storeFromURL(url);
    }

    @Test
    public void getConfigPath() {
        Path path = FileManager.getConfigPath();

        Assert.assertEquals(path.endsWith("configurations"), true);

    }

    @Test
    public void getLogPath() {
        Path path = FileManager.getLogPath();

        Assert.assertEquals(path.endsWith("Log-Error"), true);

    }

    @Test
    public void getFilesPath() {
        Path path = FileManager.getFilesPath();

        Assert.assertEquals(path.isAbsolute(), true);

    }
    
    /*
    @Test
    public void getServerURL() throws MalformedURLException {

        URL url = FileManager.getServerURL();

        Assert.assertEquals(url.toString(), FrostSetup.getFrostURL());

    }
    */

    @Test
    public void getUsername() {

        String user = FileManager.getUsername();

        Assert.assertEquals(user, null);
    }
}