package com.example.sheetmusicapp;

import android.app.Application;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author logan
 * and Andy
 */
//Only one of these objects should exist within the application so use singleton
public class SongList {
    public static SongList mInstance = null;

    //arraylist to hold the song list that the user creates
    private ArrayList<Song> setList;
    //Arraylist to hold all the songs that are aviable
    private ArrayList<Song> allSongs;
    //Arraylist to hold all pdf file strigns
    private ArrayList<String> setPDFs;
    public int currentSong;


    public SongList() {
        setList = new ArrayList<Song>();
        allSongs = new ArrayList<Song>();
        setPDFs = new ArrayList<String>();
        currentSong = 0;
    }

    public static synchronized SongList getInstance() {
        if (mInstance == null) {
            mInstance = new SongList();
        }
        return mInstance;
    }

    //get and set for song list
    public ArrayList<Song> getSetList() {
        return this.setList;
    }

    //set is actually just add
    public void addToSet(Song song) {
        Song temp = new Song(song);
        setList.add(temp);
    }

    //Method to copy song info over
    public void copyFrom(ArrayList<Song> s){
        setList =  s;
    }

    //Same as remove
    public void removeSongFromSet(Song song) {
        setList.remove(song);
    }

    public ArrayList sortBy(String sb) {
        ArrayList<Song> temp = new ArrayList<>();
        //Sort by : songTitle
        //Sort by : instrument
        //Sort by : genre
        //Sort by : composer
        //Sort by : decade

        //This will need to be replaced by the selection that a user chooses when choosing which
        //category to sort by, but this will temporarily work.
        //String sortSelection = "";
        boolean swapped = false;
        int j, i;
        Song temporary, temporary2;

        if (sb.equals("title")) {
            for (i = 0; i < allSongs.size() - 1; i++) {
                swapped = false;
                for (j = 0; j < allSongs.size() - i - 1; j++) {
                    //If the current String is lexicographically greater than the string after it
                    if (allSongs.get(j).name.compareTo(allSongs.get(j + 1).name) > 0) {
                        //switch their places
                        temporary = allSongs.get(j);
                        temporary2 = allSongs.get(j + 1);
                        allSongs.remove(j);
                        allSongs.add(j, temporary2);
                        allSongs.remove(j + 1);
                        allSongs.add(j + 1, temporary);
                        swapped = true;
                    }
                }
                // IF no two elements were
                // swapped by inner loop, then break
                if (swapped == false) {
                    break;
                }
            }

        } else if (sb.equals("composer")) {
            for (i = 0; i < allSongs.size() - 1; i++) {
                swapped = false;
                for (j = 0; j < allSongs.size() - i - 1; j++) {
                    //If the current String is lexicographically greater than the string after it
                    if (allSongs.get(j).composer.compareTo(allSongs.get(j + 1).composer) > 0) {
                        //switch their places
                        temporary = allSongs.get(j);
                        temporary2 = allSongs.get(j + 1);
                        allSongs.remove(j);
                        allSongs.add(j, temporary2);
                        allSongs.remove(j + 1);
                        allSongs.add(j + 1, temporary);
                        swapped = true;
                    }
                }
                // IF no two elements were
                // swapped by inner loop, then break
                if (swapped == false) {
                    break;
                }
            }

        } else if (sb.equals("genre")) {
            for (i = 0; i < allSongs.size() - 1; i++) {
                swapped = false;
                for (j = 0; j < allSongs.size() - i - 1; j++) {
                    //If the current String is lexicographically greater than the string after it
                    if (allSongs.get(j).genre.compareTo(allSongs.get(j + 1).genre) > 0) {
                        //switch their places
                        temporary = allSongs.get(j);
                        temporary2 = allSongs.get(j + 1);
                        allSongs.remove(j);
                        allSongs.add(j, temporary2);
                        allSongs.remove(j + 1);
                        allSongs.add(j + 1, temporary);
                        swapped = true;
                    }
                }
                // IF no two elements were
                // swapped by inner loop, then break
                if (swapped == false) {
                    break;
                }
            }

        } else if (sb.equals("decade")) {
            for (i = 0; i < allSongs.size() - 1; i++) {
                swapped = false;
                for (j = 0; j < allSongs.size() - i - 1; j++) {
                    //If the current String is lexicographically greater than the string after it
                    if (allSongs.get(j).decade < allSongs.get(j + 1).decade) {
                        //switch their places
                        temporary = allSongs.get(j);
                        temporary2 = allSongs.get(j + 1);
                        allSongs.remove(j);
                        allSongs.add(j, temporary2);
                        allSongs.remove(j + 1);
                        allSongs.add(j + 1, temporary);
                        swapped = true;
                    }
                }
                // IF no two elements were
                // swapped by inner loop, then break
                if (swapped == false) {
                    break;
                }
            }

        }else if (sb.equals("instrument")) {
            for (i = 0; i < allSongs.size() - 1; i++) {
                swapped = false;
                for (j = 0; j < allSongs.size() - i - 1; j++) {
                    //If the current String is lexicographically greater than the string after it
                    if (allSongs.get(j).doc.compareTo(allSongs.get(j + 1).doc) > 0) {
                        //switch their places
                        temporary = allSongs.get(j);
                        temporary2 = allSongs.get(j + 1);
                        allSongs.remove(j);
                        allSongs.add(j, temporary2);
                        allSongs.remove(j + 1);
                        allSongs.add(j + 1, temporary);
                        swapped = true;
                    }
                }
                // IF no two elements were
                // swapped by inner loop, then break
                if (swapped == false) {
                    break;
                }
            }

        }
        return this.allSongs;
    }


    public ArrayList<Song> searchSongs(String key){
        ArrayList<Song> toRet=new ArrayList<>();
        int length =key.length();
        int index=0;
        String sub=allSongs.get(0).getName().substring(index,length); //first LENGTH chars
        for (Song s : allSongs){
            String name=s.getName().toLowerCase();
            index=0;
            sub=name.substring(index,length);
            while(index+length<name.length()){ //careful infinite loop
                if (key.equals(sub)){
                    toRet.add(s);
                    break;
                }
                index++;
                sub=name.substring(index, index+length);
            }
            if (key.equals(sub)) {
                toRet.add(s);
            }
        }
        return toRet;
    }

    public ArrayList displaySetList() {
        ArrayList<String> temp = new ArrayList<String>();

        return temp;
    }

    @Override
    public String toString() {
        String temp = "";
        for (int i = 0; i < setList.size(); i++) {
            temp +=  setList.get(i).name + " | " + setList.get(i).genre + " | " +
                    setList.get(i).composer + " | " + setList.get(i).decade + " | " + setList.get(i).doc + "\n"
            + "--------------------------------------------" + "\n";
        }
        return temp;
    }

    public Song getAtIndex(int index) {
        return setList.get(index);
    }

    //get for Song list
    public ArrayList<Song> getAllSongs() {
        return this.allSongs;
    }

    //add method to add to songlist
    public void addToSongList(Song s) {
        Song temp = new Song(s);
        allSongs.add(temp);
    }

    //To string method for allSongs
    public String toStringAllSongs() {
        String temp = "";
        for (int i = 0; i < allSongs.size(); i++) {
            temp += allSongs.get(i).key + " " + allSongs.get(i).name + " " + allSongs.get(i).genre + " " +
                    allSongs.get(i).composer + " " + allSongs.get(i).decade + " " + allSongs.get(i).doc + " ";
        }
        return temp;
    }


    //Get for song list
    public ArrayList<String> getSetPDFs() {
        return this.setPDFs;
    }

    //add method to add to pdflist
    public void addToPDFList(String filename) {
        String s = new String(filename);
        setPDFs.add(s);
    }

    //Method to clear setlist therefor pdf list too
    public void clearSet(){
        setList.clear();
        setPDFs.clear();
    }

    //Method to return the set size
    public int setSize(){
        return setList.size();
    }

    public void clearSongList(){
            allSongs.clear();
    }
}
