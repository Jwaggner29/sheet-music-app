package com.example.sheetmusicapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewSet extends AppCompatActivity {

    LinearLayout listv;
    SongList allLists = SongList.getInstance();
    private ArrayList<String> m = new ArrayList<>();
    private Button menuButton;
    private Button shareButton;
    private Button sharePdfs;
    String pdfPath;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_set);

        pdfPath = context.getFilesDir().toString() + "/document/";

        //get intent
        Intent tent = getIntent();
        // get items from intent
        m = tent.getStringArrayListExtra("mList");

        shareButton = findViewById(R.id.shareButtonViewSet);

        if(!allLists.getSetList().isEmpty()) {
            //Display the current set list, the normal toString will return the setList string
            //Display the current songs one by one so scroll can work
            listv = findViewById(R.id.linearLayoutViewSet);
            for(int i = 0; i< allLists.getSetList().size(); i++){
                TextView tx = new TextView(getApplicationContext());
                tx.setText(allLists.getSetList().get(i).toString() + "\n" + "--------------------" + "\n");
                tx.setTextSize(40);
                listv.addView(tx);
            }
        }else{
            //Tell the user they are dumb again
            Toast.makeText(ViewSet.this, "You have to have a set in order to see it", Toast.LENGTH_SHORT).show();
            //Disable the share button
            shareButton.setEnabled(false);
        }

        //Set MenuButton to button on page
        menuButton = findViewById(R.id.menuButtonViewSet);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        shareButton = findViewById(R.id.shareButtonViewSet);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create json string to send to the other users
                    String jsonData = "";
                    JSONObject jo = new JSONObject();
                    JSONArray ja = new JSONArray();
                    JSONObject inside = new JSONObject();

                    //Loop to load in the the data of the song list into the json file
                    try {
                        for (int i = 0; i < allLists.getSetList().size(); i++) {
                            inside.put("songId", allLists.getSetList().get(i).getKey());
                            inside.put("songTitle", allLists.getSetList().get(i).getName());
                            inside.put("songComposer", allLists.getSetList().get(i).getComposer());
                            inside.put("songEra", allLists.getSetList().get(i).getDecade());
                            inside.put("songInstrument", allLists.getSetList().get(i).getDoc());
                            inside.put("songGenre", allLists.getSetList().get(i).getGenre());
                            ja.put(inside);
                            inside = new JSONObject();
                        }
                        jo.put("songs", ja);
                        jsonData = jo.toString();
                    }catch(JSONException e){
                        System.out.println("JSONException");
                        e.printStackTrace();
                    }


                    //Put the json data into the song pdf tag
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("song_pdf", jsonData);

                    //Send the metadata first
                for(int i = 0; i < m.size(); i++)
                {
                    Message message =new Message.Builder().setContent(data).setReceiverId(m.get(i)).build();

                    Bridgefy.sendMessage(message, BFEngineProfile.BFConfigProfileLongReach);

                }
                Toast.makeText(ViewSet.this, "MetaData Sent", Toast.LENGTH_SHORT).show();


                //Create a hashmap for the pdf arrays
                HashMap<String, byte[]> pdfData = new HashMap<>();
                byte[] bytesArray = new byte[20];
                //Send the first songs pdf SHOULD BE THE CREATED SONG
                for(int i = 0; i < 1; i++){
                    //Get the current file and make an inputstream
                    File newFile = new File(pdfPath  + allLists.getSetPDFs().get(i));
                    try {
                        //Set the file input stream to the current song pdf
                        InputStream is = new FileInputStream(newFile);
                        int s = is.available();
                        bytesArray = new byte[s];
                        is.read(bytesArray);
                        is.close();

                        //The data stream for the values will be under the int
                        pdfData.put("pdf", bytesArray);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }


                Toast.makeText(ViewSet.this, "PDF Sent", Toast.LENGTH_LONG).show();


                for(int i = 0; i < m.size(); i++)
                {
                    //Send pdf message holding all byte []
                    Message pdfMsg = new Message.Builder().setContent(pdfData).setReceiverId(m.get(i)).build();

                    Bridgefy.sendMessage(pdfMsg, BFEngineProfile.BFConfigProfileLongReach);
                }

                shareButton.setEnabled(false);

            }
        });


    }

}
