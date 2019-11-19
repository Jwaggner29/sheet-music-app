package com.example.sheetmusicapp;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author logan
 */
public class SongListTest {

    public SongListTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class SongList.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        SongList instance = new SongList();
        int k = 12;
        String n = "Song";
        String g = "gen";
        String c = "comp";
        int de = 90;
        String d = "doc";

        Song expResult = new Song(k,n,g,c,de,d);
        instance.addToSet(expResult);
        assert instance.getSetList().get(0).key == 12;
        // TODO review the generated test code and remove the default call to fail.

    }

    /**
     * Test of remove method, of class SongList.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        SongList instance = new SongList();
       int k = 12;
        String n = "Song";
        String g = "gen";
        String c = "comp";
        int de = 90;
        String d = "doc";

        Song expResult = new Song(k,n,g,c,de,d);
        instance.addToSet(expResult);
        instance.removeSongFromSet(expResult);
        assert instance.getSetList().isEmpty();
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of sortBy method, of class SongList.
     */
    @Test
    public void testSortBy() {
        System.out.println("sortBy");
        boolean success = false;
        SongList instance = new SongList();
        Song s1 = new Song(1, "a", "country", "mozart", 2000, "1" );
        Song s2 = new Song(2, "b", "rap", "lil pineapple", 1940, "2" );
        Song s3 = new Song(3, "c", "edm", "d3dr4t", 2010, "3" );
        instance.addToSet(s3);
        instance.addToSet(s1);
        instance.addToSet(s2);
        instance.sortBy("name");

        SongList expResult = new SongList();
        expResult.addToSet(s1);
        expResult.addToSet(s2);
        expResult.addToSet(s3);

        assert expResult.getSetList().get(0).name.equals("a");
        assert expResult.getSetList().get(1).name.equals("b");
        assert expResult.getSetList().get(2).name.equals("c");

    }



    /**
     * Test of toString method, of class SongList.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        SongList instance = new SongList();
        int k = 12;
        String n = "Song";
        String g = "gen";
        String c = "comp";
        int de = 90;
        String d = "doc";
        Song eR = new Song(k,n,g,c,de,d);
        String expResult = "12 Song gen comp 90 doc ";
        instance.addToSet(eR);
        String result = instance.toString();
        assertEquals(expResult, result);

    }

}
