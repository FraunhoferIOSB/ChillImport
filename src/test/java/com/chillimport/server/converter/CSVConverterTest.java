package com.chillimport.server.converter;

import com.chillimport.server.FileManager;
import com.chillimport.server.Table;
import com.chillimport.server.config.Configuration;
import com.chillimport.server.config.ConfigurationManager;
import com.chillimport.server.config.DataType;
import com.chillimport.server.config.MagicNumberMap;
import com.chillimport.server.config.StreamObservation;
import com.chillimport.server.config.StringColumn;
import com.chillimport.server.errors.LogManager;

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
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileManager.class, LogManager.class})
public class CSVConverterTest {


    //http://hamcrest.org/JavaHamcrest/


    private Configuration obj1;
    private Table t;
    private static String testpath;
    private String sep = File.separator;
    

    @Before
    public void setUp() throws Exception {
        String zidc1 = "ECT";
        StringColumn[] dateTime1 = {new StringColumn("TT-HH", 10), new StringColumn("MM-SS", 12)};
        int[] arr1 = {2, 8, 5};
        StreamObservation[] streamData1 = {new StreamObservation(123456, true, arr1)};
        MagicNumberMap[] map1 = new MagicNumberMap[3];

        map1[0] = new MagicNumberMap(1, "true", "wahr");
        map1[1] = new MagicNumberMap(2, "wahr", "true");
        map1[2] = new MagicNumberMap(4, "trueheit", "wahrheit");
        
        URL dummyURL = new URL("https://www.google.de/");

        obj1 = new Configuration(10, "aConfiguration", ";", 2, zidc1, dateTime1, streamData1, map1, DataType.CSV, dummyURL);
        
        
      

        testpath = "src" + sep + "test" + sep + "resources";

        PowerMockito.mockStatic(FileManager.class);
        PowerMockito.when(FileManager.getConfigPath()).thenReturn(Paths.get(testpath + sep + "configurations"));
        PowerMockito.when(FileManager.getLogPath()).thenReturn(Paths.get(testpath + sep + "Log-Error"));
    }

    @Test
    public void convert() {
        File csvfile = new File(testpath + sep + "testcsv.csv");

        try {
            t = CSVConverter.convert(csvfile, obj1);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void convertBack() {
        convert();
        try {
            CSVConverter.convertBack(t, obj1, "testpath" + sep + "exportcsv.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void filePreview() throws IOException {
        FileManager fm = new FileManager();
        ArrayList<ArrayList<String>> ll = CSVConverter.filePreview(new File(fm.getLogPath() + sep +"returnRows" + sep + "2018-08-27T15-18-42--skippedRows.csv"),
                                                                     ConfigurationManager.loadConfig(-1365040327),
                                                                     4);
        String[] content = new String[8];
        content[0] = "Rosen sind rot";
        content[1] = "Veilchen sind Blau";
        content[2] = "Ich hasse Gedichte";
        content[3] = "Klopapier";
        content[4] = "Rosen sind rott";
        content[5] = "Veilchen sind Blauu";
        content[6] = "Ich hasse Gedichtee";
        content[7] = "Klopapierr";
        int curr = 0;
        for (ArrayList<String> s : ll) {
            for (String str : s) {
                assertEquals(content[curr], str);
                curr++;
            }
        }
    }

}
