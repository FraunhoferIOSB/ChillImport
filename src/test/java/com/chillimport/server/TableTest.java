package com.chillimport.server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TableTest {

    private Table table = null;

    @Before
    public void setUp() throws Exception {
        table = new Table();
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));
        list.add(new Cell(true));

        table.appendRow(list);
        table.appendRow(list);
    }

    @Test
    public void getRow() {
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));
        list.add(new Cell(true));


        Assert.assertTrue(list.equals(table.getRow(0)));
    }

    @Test
    public void getColumn() {

        ArrayList<Object> list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell(1));

        Assert.assertTrue(list.equals(table.getColumn(0)));
    }

    @Test
    public void setRow() {
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(2));
        list.add(new Cell("Test2"));
        list.add(new Cell(2.234));
        list.add(new Cell(false));

        table.setRow(1, list);


        //Assert.assertEquals("1;Test;1.234;true;%n" + "2;Test2;2.234;false;%n", table.toString());
    }

    @Test
    public void setColumn() {
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(50));
        list.add(new Cell(50));

        table.setColumn(0, list);

        String text = "50;Test;1.234;true";
        text = text.concat(System.getProperty("line.separator"));
        text = text.concat(text);
        Assert.assertEquals(text, table.toString());
    }

    @Test
    public void appendRow() {
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));

        table.appendRow(list);
        String text = "1;Test;1.234;true";
        text = text.concat(System.getProperty("line.separator"));
        text = text.concat(text);
        text = text.concat("1;Test;1.234;null");
        text = text.concat(System.getProperty("line.separator"));

        assertEquals(text, table.toString());

        list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));
        list.add(new Cell("Test"));
        list.add(new Cell(1));
        table.appendRow(list);

        text = "1;Test;1.234;true;null";
        text = text.concat(System.getProperty("line.separator"));
        text = text.concat(text);
        text = text.concat("1;Test;1.234;null;null");
        text = text.concat(System.getProperty("line.separator"));
        text = text.concat("1;Test;1.234;Test;1");
        text = text.concat(System.getProperty("line.separator"));
        assertEquals(text, table.toString());
    }

    @Test
    public void getColumnCount() {
        assertEquals(4, table.getColumnCount());

        table = new Table();
        assertEquals(0, table.getColumnCount());
    }

    @Test
    public void clear() {
        table.clear();
        Assert.assertTrue(table.isEmpty());
    }

    @Test
    public void equals() {
        Table t = new Table();
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));
        list.add(new Cell(true));

        t.appendRow(list);
        t.appendRow(list);

        Assert.assertTrue(table.equals(t));
    }

    @Test
    public void isEmpty() {
        table = new Table();
        Assert.assertTrue(table.isEmpty());

        table.appendRow(new ArrayList<>());
        assertTrue(table.isEmpty());
    }

    @Test
    public void createTable() {
        Table t = new Table();
        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));
        list.add(new Cell(true));

        t.appendRow(list);
        t.appendRow(list);

        Table t2 = new Table(t);
        assertEquals(t2, table);
    }

    @Test
    public void removeRow() {
        Table t = new Table();

        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));
        list.add(new Cell(true));

        t.appendRow(list);
        t.appendRow(list);
        t.appendRow(list);
        t.removeRow(2);

        assertEquals(t, table);


        t.appendRow(list);
        t.removeRow(1);

        assertEquals(t, table);
    }

    @Test
    public void removeRows() {
        Table t = new Table();

        ArrayList<Cell> list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));
        list.add(new Cell(true));


        t.appendRow(list);

        list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell(1));
        list.add(new Cell(1.234));
        list.add(new Cell(1));

        t.appendRow(list);
        t.appendRow(list);

        list = new ArrayList<>();
        list.add(new Cell(1));
        list.add(new Cell("Test"));
        list.add(new Cell(1.234));
        list.add(new Cell(true));

        t.appendRow(list);


        t.removeRows(new int[]{1, 2});

        assertEquals(t, table);
    }

    @Test
    public void toStringTest() {
        String text = "1;Test;1.234;true";
        text = text.concat(System.getProperty("line.separator"));
        text = text.concat(text);
        assertEquals(text, table.toString());
    }

    @Test
    public void cloneTest() {
        assertEquals(table, table.clone());
    }
}