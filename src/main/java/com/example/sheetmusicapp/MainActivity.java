package com.example.sheetmusicapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.widget.Toast;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;
import com.bridgefy.sdk.framework.exceptions.MessageException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button Startbutton;
    private Button CreateButton;
    private Button ViewSetButton;
    private Button CreateSongButton;
    private Button AddBandButton;
    private Button JoinBandButton;
    SongList allList = SongList.getInstance();
    public boolean connected = false;

    String pdfIncoming;

    public static Intent  INTENT_EXTRA_MSG = new Intent();
    public static Intent INTENT_EXTRA_PDF = new Intent();


    private String incomingMessage;


    private final String TAG = "DevicesActivity";


    Context context = this;
    String jsonPath = "";

    // list of userIDs of nearby devices
    private ArrayList<String> pList = new ArrayList<>();
    // list of peers added to the band
    private ArrayList<String> members = new ArrayList<>();

    private ArrayList<Song> sentSet = new ArrayList<>();

    String pdfPath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get path of metadata file
        jsonPath = context.getFilesDir() + "/" + "songData.json";
        pdfPath = context.getFilesDir().toString() + "/document/";
        //Check if the file exist, if it doesnt create it
        File metaData = new File(jsonPath);
        if(!metaData.exists()){
            writeInternalFile();
            writePdfToInternal();
        }


        //Button to go from launch page to start set
        Startbutton = findViewById(R.id.startSetMainMenu);
        Startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),MusicPage.class);
                intent.putExtra("mList", members);
                startActivity(intent);
            }
        });

        //Button to go from launch page to create set
        CreateButton = findViewById(R.id.createSetMainMenu);
        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateSetPage();
            }
        });

        CreateSongButton = findViewById(R.id.createSongMainMenu);
        CreateSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateSongPage();
            }
        });

        ViewSetButton = findViewById(R.id.viewSetMainPage);
        ViewSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),ViewSet.class);
                intent.putExtra("mList", members);
                startActivity(intent);
            }
        });

        AddBandButton = findViewById(R.id.joinBandMainMenu);
        AddBandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(MainActivity.this,AddtoBand.class);
                intent.putExtra("pList", pList);
                intent.putExtra("mList", members);
                startActivityForResult(intent, 1);
            }
        });

        JoinBandButton = findViewById(R.id.startBandMainMenu);
        JoinBandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = new Intent(MainActivity.this,JoinBand.class);
                intent.putExtra("mList", members);
                intent.putExtra("pList", pList);
                startActivityForResult(intent, 1);

        }
        });


        // check that we have Location permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeBridgefy();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }



        // check that we have Location permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeBridgefy();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }


}

@Override
protected  void  onActivityResult(int requestCode, int resultCode,Intent data)
{
    super.onActivityResult(requestCode, resultCode,data);

    if(requestCode == 1)
    {
        if(resultCode == RESULT_OK)
        {
            ArrayList<String> result = data.getStringArrayListExtra("mList");
            members = result;
        }
    }
}


    //Opens create set page
    public void openCreateSetPage(){
        Intent intent = new Intent(this,CreateSetPage.class);
        startActivity(intent);
    }

    public void openCreateSongPage(){
        Intent intent = new Intent(this,CreateSong.class);
        startActivity(intent);
    }

    public void openViewSetPage(){
        Intent intent = new Intent(this,ViewSet.class);
        startActivity(intent);
    }

    private void sendMessageBroadcast(Intent intent){

        boolean test = LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        int help = 0;
    }

    //bridgefy methods

    RegistrationListener registrationListener = new RegistrationListener() {
        @Override
        public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
            Log.i(TAG, "onRegistrationSuccessful: current userId is: " + bridgefyClient.getUserUuid());
            Log.i(TAG, "Device Rating " + bridgefyClient.getDeviceProfile().getRating());
            Log.i(TAG, "Device Evaluation " + bridgefyClient.getDeviceProfile().getDeviceEvaluation());

            // Start the Bridgefy SDK
            Bridgefy.start(messageListener, stateListener);
        }

        @Override
        public void onRegistrationFailed(int errorCode, String message) {
            Log.e(TAG, "onRegistrationFailed: failed with ERROR_CODE: " + errorCode + ", MESSAGE: " + message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // Toast.makeText(DevicesActivity.this, "Bridgefy registration did not succeed.", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private void initializeBridgefy() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (isThingsDevice(this)) {
            // enabling bluetooth automatically
            bluetoothAdapter.enable();
        }

        //Always use steady context objects to avoid leaks
        Bridgefy.initialize(getApplicationContext(), registrationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeBridgefy();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Location permissions needed to start devices discovery.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(Device device, Session session) {
            Log.i(TAG, "Device found: " + device.getUserId());

            // send this device's information to the connected Device
            HashMap<String, Object> map = new HashMap<>();
            map.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
            device.sendMessage(map);
            pList.add(device.getUserId());
            Log.d(TAG, "Message sent!");
        }

        @Override
        public void onDeviceLost(Device device) {
            Log.w(TAG, "Device lost: " + device.getUserId());
            pList.remove(device.getUserId());

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

    MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            String s = message.getContent().get("manufacturer ") + " " + message.getContent().get("model");
            Log.d(TAG, "Message Received: " + message.getSenderId() + ", content: " + message.getContent());
            //if device name is sent (only sent upon initial connection)
            if (message.getContent().get("device_name") != null)
            {
                //get sender ID *id passed when device disconnects*
               String sender = message.getSenderId();
               int help = 0;


            }
            //if song metadata is sent
            if(message.getContent().get("song_pdf") != null) {
                Toast.makeText(MainActivity.this, "Start Recieve", Toast.LENGTH_SHORT).show();

                String jsonData = "";
                //TODO get json
                jsonData = (String) message.getContent().get("song_pdf");
                //Clear the set
                allList.clearSet();
                try {
                    //Create a JSON Section
                    JSONObject jo = new JSONObject(jsonData);
                    JSONArray ja = jo.getJSONArray("songs");
                    for (int i = 0; i < ja.length(); i++) {
                        Song newSong = new Song();
                        JSONObject inside = (JSONObject) ja.get(i);
                        newSong.setKey(inside.getInt("songId"));
                        newSong.setName(inside.getString("songTitle"));
                        newSong.setComposer(inside.getString("songComposer"));
                        newSong.setDecade(inside.getInt("songEra"));
                        newSong.setDoc(inside.getString("songInstrument"));
                        newSong.setGenre(inside.getString("songGenre"));
                        allList.addToSet(newSong);

                        String filename = "";
                        filename = (String) allList.getSetList().get(i).getName();
                        filename = filename.toLowerCase().replace(" ", "");
                        filename = filename + ".pdf";
                        File pdfFile = new File(pdfPath + filename);

                        if(pdfFile.exists()){
                            allList.getSetPDFs().add(filename);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Recieved Set data", Toast.LENGTH_SHORT).show();
            }//else it is a pre-defined message
            if(message.getContent().containsKey("foo"))
            {
                Intent intent = new Intent("INTENT_EXTRA_MSG");
                incomingMessage = (String) message.getContent().get("foo");
                intent.putExtra("message", incomingMessage);
                Log.d(TAG, "Incoming private message: " + incomingMessage);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

                sendMessageBroadcast(INTENT_EXTRA_MSG);
            }

            //If pdf hashmap is sent
            if(message.getContent().containsKey("pdf")){

                Toast.makeText(MainActivity.this, "Recieved Set pdf", Toast.LENGTH_LONG).show();

                    //Read in the pdf data and create the fileoutput
                    for (int i = 0; i < 1; i++) {
                        String filename = "";
                        filename = (String) allList.getSetList().get(i).getName();
                        filename = filename.toLowerCase().replace(" ", "");
                        filename = filename + ".pdf";
                        File pdfFile = new File(pdfPath + filename);

                        //Check to see if the file already exists, if it doesnt create it, if it does do nothing
                        if (!pdfFile.exists()) {
                            ArrayList ar = (ArrayList) message.getContent().get("pdf");
                            //Create a byte array to hold the received
                            byte[] pdfArray = new byte[ar.size()];

                            for(int j = 0; j< ar.size(); j++){
                                Double d = (Double) ar.get(j);
                                pdfArray[j] = Byte.valueOf(String.valueOf(d.intValue()));
                            }


                            try {
                                //Make the file
                                FileUtils.writeByteArrayToFile(pdfFile, pdfArray);
                                allList.addToPDFList(filename);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            }
        }

        @Override
        public void onMessageFailed(Message message, MessageException e) {
            Log.e(TAG, "Message failed", e);
        }

        @Override
        public void onMessageSent(Message message) {
            Log.d(TAG, "Message sent to: " + message.getReceiverId());
        }

        @Override
        public void onMessageReceivedException(String s, MessageException e) {
            Log.e(TAG, e.getMessage());

        }
    };


    /**
     *      ADAPTER
     */

    public boolean isThingsDevice(Context context) {
        final PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature("android.hardware.type.embedded");
    }

    //Allows us to read json from a different file and returns a string
    public String loadJSONFromResources() {
        String json = "";
        InputStream is = getResources().openRawResource(R.raw.songs);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch(IOException e){

        }finally {
            try{
                is.close();
            }catch(IOException e) {
            }
        }

        json = writer.toString();
        return json;
    }

    //Method to write internal file if it does not exist.
    public void writeInternalFile(){

        //Write the file if it doesnt exist
        try {
            //Write default file internal file, using json file from raw
            FileOutputStream fileout = openFileOutput("songData.json", MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(loadJSONFromResources());
            outputWriter.close();

        }catch(FileNotFoundException e){
            Toast.makeText(this,"File Not Found Writing to file", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        }
    }


    //Method to copy assest pdf to local area
    public void writePdfToInternal(){
        AssetManager assetManager = getAssets();
        try {
            String[] assets = assetManager.list("");
            if (assets != null) for(String filename: assets){
                if((!filename.equals("images"))){
                    if(!filename.equals("webkit")){
                        File assetFile = new File(filename);
                        File newFile = new File(pdfPath + filename);

                        InputStream is = assetManager.open(filename);
                        byte[] bytesArray = new byte[is.available()];
                        is.read(bytesArray);
                        FileUtils.writeByteArrayToFile(newFile, bytesArray);
                        is.close();
                    }
                }

            }
        }catch(IOException e){
            System.out.println("IOException with file ");
            e.printStackTrace();
        }


    }
}
