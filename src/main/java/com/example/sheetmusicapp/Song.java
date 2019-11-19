/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author logan
 */

package com.example.sheetmusicapp;

public class Song
{
    int key;
    String name;
    String genre;
    String composer;
    int decade;
    String doc;

    public Song(){
        this.key = -1;
        this.name = "";
        this.genre = "";
        this.composer = "";
        this.decade = -1;
        this.doc = "";
    }
    
    public Song( int key,String name,String genre,String composer,int decade, String doc)
    {
        this.key = key;
        this.name = name;
        this.genre = genre;
        this.composer = composer;
        this.decade = decade;
        this.doc = doc;
    }

    public Song (Song s){
        this.key = s.key;
        this.name = s.name;
        this.genre = s.genre;
        this.composer = s.composer;
        this.decade = s.decade;
        this.doc = s.doc;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public int getDecade() {
        return decade;
    }

    public void setDecade(int decade) {
        this.decade = decade;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    @Override
    public String toString(){
        String s = "";
        s = getName() + " | " + getComposer() + " | " + getDecade() + " | " + getGenre() + " | " + getDoc();
        return  s;
    }

}
