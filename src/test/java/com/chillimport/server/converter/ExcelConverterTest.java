package com.chillimport.server.converter;

import com.chillimport.server.Table;
import com.chillimport.server.config.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class ExcelConverterTest {

    Configuration obj1;
    Table t;

    @Before
    public void setUp() throws Exception {
        ZoneOffset zone = ZoneOffset.of("+1");
        String zidc1 = "ETC";
        StringColumn[] dateTime1 = {new StringColumn("TT-HH", 10), new StringColumn("MM-SS", 12)};
        int[] arr1 = {2, 8, 5};
        StreamObservation[] streamData1 = {new StreamObservation(123456, true, arr1)};
        MagicNumberMap[] map1 = new MagicNumberMap[3];

        map1[0] = new MagicNumberMap(1, "true", "wahr");
        map1[1] = new MagicNumberMap(2, "wahr", "true");
        map1[2] = new MagicNumberMap(4, "trueheit", "wahrheit");
        
        URL dummyURL = new URL("https://www.google.de/");

        obj1 = new Configuration(10, "aConfiguration", ";", 3, zidc1, dateTime1, streamData1, map1, DataType.CSV, dummyURL);
    }
    
    @Ignore
    @Test
    public void convert() {
        File file = new File("src/test/java/com/chillimport/server/converter/files/testexcel.xlsx");
        t = new Table();

        try {
            t = ExcelConverter.convert(file, obj1);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

        System.out.println(t.toString());


        /*


        Assert.assertEquals("123.0;test;1.005;Fri Mar 23 00:00:00 CET 2018;test;1.0" + System.getProperty("line.separator") +
                "245.0;test2;1.005;Sun Mar 25 00:00:00 CET 2018;test2;2.0" + System.getProperty("line.separator") +
                "678.0;test3;1.005;Fri Mar 23 00:00:00 CET 2018;test;3.0" + System.getProperty("line.separator") +
                "3.0;test;1.005;Mon Jul 23 00:00:00 CEST 2018;test;4.0" + System.getProperty("line.separator"), t.toString());
        */
    }
    
    @Ignore
    @Test
    public void convertBack() throws IOException {
        convert();

        ExcelConverter.convertBack(t, obj1, "src/test/java/com/chillimport/server/converter/files/exportexcel--"
                + DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss").format(LocalDateTime.now())
                + ".xlsx");

    }

}
