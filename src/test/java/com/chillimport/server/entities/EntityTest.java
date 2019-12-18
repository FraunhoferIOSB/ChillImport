package com.chillimport.server.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;



public class EntityTest {

    private Entity e11;
    private Entity e12;
    private Entity e0;
    private Entity e2;
    private Entity e3;


    @Before
    public void setUp() {
        e0 = new Entity(null, null);
        e11 = new Entity("name1", "description1");
        e12 = new Entity("name1", "description1");
        e2 = new Entity("name1", "description2");
        e3 = new Entity("name2", "description1");
    }

    @Test
    public void getName() {
        assertNull(e0.getName());
        assertEquals(e11.getName(), "name1");
        assertEquals(e12.getName(), "name1");
        assertEquals(e2.getName(), "name1");
        assertEquals(e3.getName(), "name2");
    }

    @Test
    public void setName() {
        Entity entity = new Entity();
        assertNull(entity.getName());
        entity.setName("name");
        assertEquals(entity.getName(), "name");

    }

    @Test
    public void getDescription() {
        assertNull(e0.getDescription());
        assertEquals(e11.getDescription(), "description1");
        assertEquals(e12.getDescription(), "description1");
        assertEquals(e2.getDescription(), "description2");
        assertEquals(e3.getDescription(), "description1");
    }

    @Test
    public void setDescription() {
        Entity entity = new Entity();
        assertNull(entity.getDescription());
        entity.setDescription("description");
        assertEquals(entity.getDescription(), "description");

    }

    @Test
    public void getAndSetFrostId() {
        assertNull(e11.getFrostId());
        e11.setFrostId("1234");
        assertEquals(e11.getFrostId(), "1234");


    }


    @Test
    public void equals() {
        assertEquals(e11, e12);
        assertNotEquals(e11, e0);
        assertNotEquals(e11, e2);
        assertNotEquals(e11, e3);
        assertNotEquals(e11, "");
    }
}