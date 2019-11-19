package com.example.sheetmusicapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicPage extends AppCompatActivity {

    private static final String TAG = "com.example.sheetmusicapp";

    private String incomingMessage;
    private ArrayList<String> p = new ArrayList<>();
    private ArrayList<String> m = new ArrayList<>();

    private Button prevPageButton;
    private Button prevButton;
    private Button fileButton;
    private Button menuButton;
    private Button textButton;
    private Button nextButton;
    private Button nextPageButton;

    TextView currentSongText;
    public TextView Message;
    PDFView pdfviewer;
    int pdfCurrentPage;
    SongList allLists = SongList.getInstance();
    String pdfPath = "";
    Context context = this;


    public MusicPage(){
    String conversationId = " ";
        pdfCurrentPage = 0;
        allLists.currentSong = 0;
    }


    private BroadcastReceiver message = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
           String  msge = intent.getStringExtra("message");
           Toast.makeText(MusicPage.this,msge,Toast.LENGTH_SHORT).show();
            Message = (TextView) findViewById(R.id.lastMessageMusicPage);
            Message.setText(msge);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_page);
        //always set these to 0 on create
        allLists.currentSong = 0;
        pdfCurrentPage = 0;
        pdfPath = context.getFilesDir().toString() + "/document/";



        LocalBroadcastManager.getInstance(this).registerReceiver(message , new IntentFilter("INTENT_EXTRA_MSG"));

        //get intent
        Intent tent = getIntent();
        // get items from intent
        m = tent.getStringArrayListExtra("mList");

        //Set up menu fuctionality sends you back to main page
        menuButton = findViewById(R.id.menuMusicPage);
        menuButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                finish();
            }
        });


        fileButton = findViewById(R.id.infoButtonMusicPage);
        fileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openSongInfo();
            }
        });

        //shows popup menu for fields to search by
        textButton = findViewById(R.id.messageMusicPage);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MusicPage.this, textButton);
                popupMenu.getMenuInflater().inflate(R.menu.popupmessages, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String msg = (String) item.getTitle();
                        // Build a HashMap object
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("foo", msg);

                        for(int i = 0; i < m.size(); i++)
                        {
                            Message message =new Message.Builder().setContent(data).setReceiverId(m.get(i)).build();

                            Bridgefy.sendMessage(message, BFEngineProfile.BFConfigProfileLongReach);
                        }

                        Toast.makeText(MusicPage.this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
                        Message = (TextView) findViewById(R.id.lastMessageMusicPage);
                        Message.setText(item.getTitle());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        //bind next page button to next page method
        nextPageButton = findViewById(R.id.nextPageButton);
        nextPageButton.setOnClickListener(new View.OnClickListener(){
            //When the user clicks the button call the next page method
            @Override
            public void onClick(View v){
                nextPage();
            }
        });


        //bind previous page button to previous page method
        prevPageButton = findViewById(R.id.prevPageButton);
        prevPageButton.setOnClickListener(new View.OnClickListener(){
            //When the user clicks on the button call the previous page method
            @Override
            public void onClick(View v){
                previousPage();
            }
        });

        //Bind prevButton to prev page method
        prevButton = findViewById(R.id.prevSongMusicPage);
        prevButton.setOnClickListener(new View.OnClickListener(){
            //When the user clicks the call the prev method
            @Override
            public void onClick(View v){
                prevSong();
            }
        });


        //Bind nextButton to next page method
        nextButton = findViewById(R.id.nextSongMusicPage);
        nextButton.setOnClickListener(new View.OnClickListener(){
            //When the user clicks call the next method
            @Override
            public void onClick(View v){
                nextSong();
            }
        });

        try {
            File pdfFile = new File(pdfPath + allLists.getSetPDFs().get(allLists.currentSong));
            //Open up the pdf, the first song in the setList
            pdfviewer = (PDFView) findViewById(R.id.pdfviewerMusicPage);
            pdfviewer.enableSwipe(false);
            pdfviewer.fromFile(pdfFile).load();

            //set up Current song text
            currentSongText = findViewById(R.id.currentSongMusicPage);
            currentSongText.setText(allLists.getSetList().get(0).getName());
        } catch(Exception e){
            //Tell the user they are dumb and disable all buttons except the menu
            Toast.makeText(this, "You have to create a set before you can start one",Toast.LENGTH_LONG).show();
            prevButton.setEnabled(false);
            prevPageButton.setEnabled(false);
            nextButton.setEnabled(false);
            nextPageButton.setEnabled(false);
            textButton.setEnabled(false);
            fileButton.setEnabled(false);
        }

    }



    //method to turn the pdf to the previous page
    public void previousPage(){

        //if statement to see that page is not less than zero
        if(pdfCurrentPage > 0) {
            //subtract one from page then jump to that page
            pdfCurrentPage--;
            //Surround try catch block
            try {
                pdfviewer.jumpTo(pdfCurrentPage);
                String page = (pdfCurrentPage +1) + "/" + pdfviewer.getPageCount();
                Toast.makeText(MusicPage.this, page, Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                System.out.println("Cound not turn back a page due to null pointer error.");
            }
        }
    }

    //method to turn the pdf to the next page
    public void nextPage(){

        //if statement to check if pdf is in bounds
        if(pdfCurrentPage < pdfviewer.getPageCount() - 1) {
            //add one to the page then jump to that page
            pdfCurrentPage++;
            //surround in a try catch block to catch null pointer
            try {
                pdfviewer.jumpTo(pdfCurrentPage);
                String page = (pdfCurrentPage + 1) + "/" + pdfviewer.getPageCount();
                Toast.makeText(MusicPage.this, page, Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                System.out.println("Could not turn page due to null pointer error.");
            }
        }
    }

    // Opens the information page for file button
    public void openSongInfoPage() {
        Intent intent = new Intent(this, SongInfo.class);
        startActivity(intent);
    }

    //Method to change the song to the next song
    public void nextSong(){

        //As long as the current song is not the last song,iterate the current song
        if(allLists.currentSong < allLists.getSetList().size() - 1){
            allLists.currentSong++;
            File pdfFile = new File(pdfPath + allLists.getSetPDFs().get(allLists.currentSong));
            pdfviewer.recycle();
            //Open up the next pdf
            pdfviewer.fromFile(pdfFile).load();
            pdfviewer.enableSwipe(false);
            currentSongText.setText(allLists.getSetList().get(allLists.currentSong).getName());

            //set the currentpage to 0
            pdfCurrentPage = 0;
        }
    }

    //Method to change the song to the previous song
    public void prevSong() {
        //As long as the currentsong int is greater than zero
        if(allLists.currentSong > 0){
            allLists.currentSong--;
            File pdfFile = new File(pdfPath + allLists.getSetPDFs().get(allLists.currentSong));
            pdfviewer.recycle();
            //open up the next pdf
            pdfviewer.fromFile(pdfFile).load();
            pdfviewer.enableSwipe(false);
            currentSongText.setText(allLists.getSetList().get(allLists.currentSong).getName());

            pdfCurrentPage = 0;
        }
    }

    //Method to open page info
    public void openSongInfo(){
        Intent intent = new Intent(this,SongInfo.class);
        startActivity(intent);
    }


    MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            String s = message.getContent().get("manufacturer ") + " " + message.getContent().get("model");
            Log.d(TAG, "Message Received: " + message.getSenderId() + ", content: " + s);
            //if device name is sent (only sent upon initial connection)
            if (message.getContent().get("device_name") != null) {
                //get sender ID *id passed when device disconnects*
                String sender = message.getSenderId();
                //get user ID
                String user = message.getUuid();
                //get device name
                String dev = (String) message.getContent().get("device_name");
                //add to peer list

                //add to pList
                p.add(sender);

            }
            //TODO add else if for band message
            else {
                incomingMessage = (String) message.getContent().get("text");
                Message = (TextView) findViewById(R.id.lastMessageMusicPage);
                Message.setText(incomingMessage);
                Log.d(TAG, "Incoming private message: " + incomingMessage);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
                        new Intent(message.getSenderId())
                                .putExtra("INTENT_EXTRA_MSG", incomingMessage));

            }

            //TODO add else if for song files


        }

        public void onBroadcastMessageReceived(Message message) {
            // Public message sent to all nearby devices
            incomingMessage = (String) message.getContent().get("text");
            Message = (TextView) findViewById(R.id.lastMessageMusicPage);
            Message.setText(incomingMessage);
            Log.d(TAG, "Incoming private message: " + incomingMessage);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
                    new Intent(message.getSenderId())
                            .putExtra("INTENT_EXTRA_MSG", incomingMessage));

        }

    };


    StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(Device device, Session session) {
            Log.i(TAG, "Device found: " + device.getUserId());

            // send this device's information to the connected Device
            HashMap<String, Object> map = new HashMap<>();
            map.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
            device.sendMessage(map);

            Log.d(TAG, "Message sent!");
        }




        @Override
        public void onStarted() {
            super.onStarted();
            Log.i(TAG, "onStarted: Bridgefy started");
        }

        @Override
        public void onStartError(String s, int i) {
            super.onStartError(s, i);
            Log.e(TAG, "onStartError: " + s + " " + i);
        }

        @Override
        public void onStopped() {
            super.onStopped();
            Log.w(TAG, "onStopped: Bridgefy stopped");
        }
    };
}


