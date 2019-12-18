package com.chillimport.server.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;


public class MagicNumberMapTest {


    @Test
    public void toMapTest() {
        MagicNumberMap[] map = new MagicNumberMap[3];

        map[0] = new MagicNumberMap(1, "true", "wahr");
        map[1] = new MagicNumberMap(2, "wahr", "true");
        map[2] = new MagicNumberMap(4, "trueheit", "wahrheit");

        HashMap<StringColumn, String> hashmap = MagicNumberMap.arrayToHashMap(map);
        StringColumn sc = new StringColumn("true", 1);
        StringColumn sc1 = new StringColumn("true", 2);

        assertEquals(hashmap.get(sc), "wahr");
        assertNotEquals(hashmap.get(sc), "true");
        assertNull(hashmap.get(sc1));

    }

    @Test
    public void equals() {
        MagicNumberMap map = new MagicNumberMap(1, "true", "wahr");
        assertEquals(map, map);
        assertNotEquals(map, null);
    }
}
