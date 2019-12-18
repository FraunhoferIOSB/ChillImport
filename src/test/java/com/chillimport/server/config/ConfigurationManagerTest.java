package com.chillimport.server.config;

import com.chillimport.server.FileManager;
import com.chillimport.server.errors.LogManager;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FileManager.class, LogManager.class})
public class ConfigurationManagerTest {

    private static File file;
    private Configuration config1;
    private Configuration config2;
    private Configuration config3;
    private String sep = File.separator;

    @Before
    public void before() throws IOException {
        String zidc1 = "EAT";
        StringColumn[] dateTime1 = {new StringColumn("TT-HH", 10), new StringColumn("MM-SS", 12)};
        int[] arr1 = {2, 8, 5};
        StreamObservation[] streamData1 = {new StreamObservation(123456, true, arr1)};
        MagicNumberMap[] map1 = new MagicNumberMap[3];

        map1[0] = new MagicNumberMap(1, "true", "wahr");
        map1[1] = new MagicNumberMap(2, "wahr", "true");
        map1[2] = new MagicNumberMap(4, "trueheit", "wahrheit");
        
        URL dummyURL = new URL("https://www.google.de/");

        config1 = new Configuration(3825918, "aConfiguration", ";", 3, zidc1, dateTime1, streamData1, map1, DataType.CSV, dummyURL);
        config2 = new Configuration(25857854, "aConfiguration", ";", 3, zidc1, dateTime1, streamData1, map1, DataType.CSV, dummyURL);
        config3 = new Configuration(7890002, "aConfiguration", ";", 3, zidc1, dateTime1, streamData1, map1, DataType.CSV, dummyURL);

        //Create temp file for tests and set the config path to it

        file = new File("src" + sep + "test" + sep + "tempTestfolder");
        file.mkdir();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in @Before");
        }

        PowerMockito.mockStatic(FileManager.class);
        PowerMockito.when(FileManager.getConfigPath()).thenReturn(Paths.get(file.getPath()));

        PowerMockito.when(FileManager.getLogPath()).thenReturn(Paths.get(file.getPath()));

        LogManager log = mock(LogManager.class);

        PowerMockito.mockStatic(LogManager.class);
        PowerMockito.when(LogManager.getInstance()).thenReturn(log);

        ConfigurationManager.saveConfig(config1);
        ConfigurationManager.saveConfig(config2);
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(file);
    }


    @Test
    public void load1() throws IOException {
        Configuration tempConfig1 = ConfigurationManager.loadConfig(3825918);
        Configuration tempConfig2 = ConfigurationManager.loadConfig(25857854);

        assertNotEquals(tempConfig2, config1);
        assertEquals(tempConfig1, config1);
    }

    @Test(expected = IOException.class)
    public void load2() throws IOException {
        ConfigurationManager.loadConfig(010101);
    }

    @Test
    public void save1() throws IOException {
        ConfigurationManager.saveConfig(config3);
        List<Configuration> list = ConfigurationManager.listAll();
        assertTrue(list.contains(config3));
    }

    @Test(expected = IOException.class)
    public void save2() throws IOException {
        ConfigurationManager.saveConfig(config1);
    }


    @Test
    public void listAll() {
        List<Configuration> list = ConfigurationManager.listAll();
        assertEquals(list.size(), 2);
        assertTrue(list.contains(config1));
        assertTrue(list.contains(config2));
    }

    @Test(expected = NullPointerException.class)
    public void listAll2() throws IOException {
        File mockConfig = new File("src" + sep + "test" + sep + "tempTestfolder" + sep + "123.json");
        mockConfig.createNewFile();

        ConfigurationManager.listAll();

    }

}
