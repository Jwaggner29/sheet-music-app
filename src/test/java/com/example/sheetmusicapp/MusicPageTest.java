package com.example.sheetmusicapp;

import com.github.barteksc.pdfviewer.PDFView;

import org.junit.Test;
import static org.junit.Assert.*;

public class MusicPageTest {
    MusicPage mp = new MusicPage();


    //Test to tsee that the page is actually turned.
    @Test
    public void nextPageTest(){
        mp.pdfCurrentPage = 0;
        mp.nextPage();
        assertEquals(1, mp.pdfCurrentPage);
    }

    //Test to see that the page is turned back
    @Test
    public void previousPageTest(){
        mp.pdfCurrentPage = 1;
        mp.previousPage();
        assertEquals(0, mp.pdfCurrentPage);
    }

    //Test to see that the page does not get turned back passed zero
    @Test
    public void previousPageFirstPageTest(){
        mp.pdfCurrentPage = 0;
        mp.previousPage();
        assertEquals(0, mp.pdfCurrentPage);
    }

    //Test to see that the page does not get turned passed the final page
    //This test should pass however it will not set up to the xml currectly so a nullpointererror occurs
    @Test
    public void nextPageLastPageTest(){
        mp.pdfviewer.getPageCount();
        mp.nextPage();
        assertEquals(mp.pdfviewer.getPageCount(), mp.pdfCurrentPage);
    }
}
