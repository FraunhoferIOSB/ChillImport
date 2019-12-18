package com.chillimport.server.parser;

import com.chillimport.server.Cell;
import com.chillimport.server.config.Configuration;
import com.chillimport.server.config.DataType;
import com.chillimport.server.config.StringColumn;



import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * Basic Tests for TimeParser.java
 */
public class TimeParserTest {

    private Configuration cfg;
    private ArrayList<Cell> row;

    @Before
    public void setUp() {
        row = new ArrayList<>();
    }

    @Test
    public void shortTimeTest() throws MalformedURLException {
        String format = "yyyy:MM";
        StringColumn cfgTime = new StringColumn(format, 0);
        StringColumn[] all = {cfgTime};
        URL dummyURL = new URL("https://www.google.de/");
        cfg = new Configuration(0, "", "", 0, "2", all, null, null, DataType.CSV, dummyURL);

        ZonedDateTime zdt = ZonedDateTime.now();

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern(format)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.YEAR_OF_ERA, 1)
                .toFormatter();

        String time = zdt.toLocalDateTime().format(formatter);
        row.add(new Cell(time));

        LocalDateTime ldt = LocalDateTime.parse(time, formatter);
        zdt = ZonedDateTime.of(ldt, ZoneOffset.ofHours(2));

        assertEquals(zdt, TimeParser.toZonedDateTime(cfg, row));

        format = "HH:mm:ss";
        cfgTime = new StringColumn(format, 0);
        all = new StringColumn[1];
        all[0] = cfgTime;
        cfg = new Configuration(0, "", "", 0, "2", all, null, null, DataType.CSV, dummyURL);

        formatter = new DateTimeFormatterBuilder()
                .appendPattern(format)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.YEAR_OF_ERA, 1)
                .toFormatter();

        zdt = ZonedDateTime.now();
        time = zdt.toLocalDateTime().format(formatter);
        row.clear();
        row.add(new Cell(time));

        ldt = LocalDateTime.parse(time, formatter);
        zdt = ZonedDateTime.of(ldt, ZoneOffset.ofHours(2));

        assertEquals(zdt, TimeParser.toZonedDateTime(cfg, row));
    }

    @Test
    public void externalZoneTest() throws MalformedURLException {
        String format = "yyyy:MM:dd HH:mm:ss";
        StringColumn cfgTime = new StringColumn(format, 0);
        StringColumn[] all = {cfgTime};
        URL dummyURL = new URL("https://www.google.de/");
        cfg = new Configuration(0, "", "", 0, "2", all, null, null, DataType.CSV, dummyURL);

        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        String time = zdt.toLocalDateTime().format(formatter);
        row.add(new Cell(time));

        LocalDateTime ldt = LocalDateTime.parse(time, formatter);
        zdt = ZonedDateTime.of(ldt, ZoneOffset.ofHours(2));

        assertEquals(zdt, TimeParser.toZonedDateTime(cfg, row));
    }

    @After
    public void cleanup() {
        row.clear();
        cfg = null;
    }

    @Test
    public void zdtWithZoneTest() throws MalformedURLException {
        StringColumn cfgTime = new StringColumn("yyyy:MM:dd HH:mm:ss VV", 0);
        StringColumn[] all = {cfgTime};
        URL dummyURL = new URL("https://www.google.de/");
        cfg = new Configuration(0, "", "", 0, "5", all, null, null, DataType.CSV, dummyURL);


        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss VV");

        String time = zdt.format(format);
        row.add(0, new Cell(time));

        zdt = ZonedDateTime.parse(time, format);
        assertEquals(zdt, TimeParser.toZonedDateTime(cfg, row));

    }

    @Test(expected = DateTimeException.class)
    public void zdtErrorTest() throws DateTimeException, MalformedURLException {
        String format = "yyyy:MM:dd HH:mm:ss";
        StringColumn cfgTime = new StringColumn(format, 0);
        StringColumn[] all = {cfgTime};
        URL dummyURL = new URL("https://www.google.de/");
        cfg = new Configuration(0, "", "", 0, "2", all, null, null, DataType.CSV, dummyURL);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter), ZoneId.systemDefault());

        row.add(new Cell("whatever"));
        assertNotEquals(zdt, TimeParser.toZonedDateTime(cfg, row));
    }

    @Test(expected = NullPointerException.class)
    public void configNullTest() throws NullPointerException {
        ZonedDateTime zdt = ZonedDateTime.now();
        row.add(new Cell("whatever"));
        assertNotEquals(zdt, TimeParser.toZonedDateTime(null, row));
    }

    @Test(expected = NullPointerException.class)
    public void rowNullTest() throws NullPointerException, MalformedURLException {

        String format = "yyyy:MM:dd HH:mm:ss";
        StringColumn cfgTime = new StringColumn(format, 0);
        StringColumn[] all = {cfgTime};
        URL dummyURL = new URL("https://www.google.de/");
        cfg = new Configuration(0, "", "", 0, "2", all, null, null, DataType.CSV, dummyURL);

        ZonedDateTime zdt = ZonedDateTime.now();

        assertNotEquals(zdt, TimeParser.toZonedDateTime(cfg, null));
    }
}
