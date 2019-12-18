package com.chillimport.server.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;



public class UnitOfMeasurementTest {

    private UnitOfMeasurement u0;
    private UnitOfMeasurement u11;
    private UnitOfMeasurement u12;
    private UnitOfMeasurement u2;
    private UnitOfMeasurement u3;
    private UnitOfMeasurement u4;

    @Before
    public void setUp() {
        u0 = new UnitOfMeasurement(null, null, null);
        u11 = new UnitOfMeasurement("name1", "symbol1", "def1");
        u12 = new UnitOfMeasurement("name1", "symbol1", "def1");
        u2 = new UnitOfMeasurement("name1", "symbol1", "def2");
        u3 = new UnitOfMeasurement("name1", "symbol2", "def1");
        u4 = new UnitOfMeasurement("name2", "symbol1", "def1");
    }


    @Test
    public void getName() {
        assertNull(u0.getName());
        assertEquals(u11.getName(), "name1");
        assertEquals(u12.getName(), "name1");
        assertEquals(u2.getName(), "name1");
        assertEquals(u3.getName(), "name1");
        assertEquals(u4.getName(), "name2");
    }

    @Test
    public void setName() {
        UnitOfMeasurement unit = new UnitOfMeasurement();
        assertNull(unit.getName());
        unit.setName("name");
        assertEquals(unit.getName(), "name");
    }

    @Test
    public void getSymbol() {
        assertNull(u0.getSymbol());
        assertEquals(u11.getSymbol(), "symbol1");
        assertEquals(u12.getSymbol(), "symbol1");
        assertEquals(u2.getSymbol(), "symbol1");
        assertEquals(u3.getSymbol(), "symbol2");
        assertEquals(u4.getSymbol(), "symbol1");
    }

    @Test
    public void setSymbol() {
        UnitOfMeasurement unit = new UnitOfMeasurement();
        assertNull(unit.getSymbol());
        unit.setSymbol("symbol");
        assertEquals(unit.getSymbol(), "symbol");
    }

    @Test
    public void getDefinition() {
        assertNull(u0.getDefinition());
        assertEquals(u11.getDefinition(), "def1");
        assertEquals(u12.getDefinition(), "def1");
        assertEquals(u2.getDefinition(), "def2");
        assertEquals(u3.getDefinition(), "def1");
        assertEquals(u4.getDefinition(), "def1");
    }

    @Test
    public void setDefinition() {
        UnitOfMeasurement unit = new UnitOfMeasurement();
        assertNull(unit.getDefinition());
        unit.setDefinition("def");
        assertEquals(unit.getDefinition(), "def");
    }

    @Test
    public void convertToFrostStandard() {
        de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement convertedUnit = u2.convertToFrostStandard();
        assertEquals(convertedUnit.getName(), u2.getName());
        assertEquals(convertedUnit.getSymbol(), u2.getSymbol());
        assertEquals(convertedUnit.getDefinition(), u2.getDefinition());

        UnitOfMeasurement reconvertedUnit = new UnitOfMeasurement(convertedUnit);
        assertEquals(reconvertedUnit, u2);
    }

    @Test
    public void convertBack() {
        de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement unit = new de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement("name1",
                                                                                                                                 "symbol1",
                                                                                                                                 "def2");
        UnitOfMeasurement reconvertedUnit = new UnitOfMeasurement(unit);
        assertEquals(reconvertedUnit, u2);
    }

    @Test
    public void equals() {
        assertEquals(u11, u12);
        assertNotEquals(u11, u0);
        assertNotEquals(u11, u2);
        assertNotEquals(u11, u3);
        assertNotEquals(u11, u4);
        assertNotEquals(u11, "");
    }
}