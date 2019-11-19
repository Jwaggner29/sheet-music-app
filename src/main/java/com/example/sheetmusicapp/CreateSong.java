package com.example.sheetmusicapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateSong extends AppCompatActivity {

    private Button menuButton;
    private Button chooseFileButton;
    private Button createSong;

    EditText songName;
    EditText songComp;
    EditText songDec;
    EditText songInst;
    EditText songGenre;

    SongList allLists = SongList.getInstance();

    Context context = this;
    String jsonPath = "";
    String oldJson = "";
    String pdfPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_set_page);

        //File path saved as string
        jsonPath = context.getFilesDir() + "/" + "songData.json";
        pdfPath = context.getFilesDir().toString();


        //Read in data from json
        oldJson = loadJSONFromInternal();

        //Connect Edit text to their respective vars
        songName = findViewById(R.id.songTitleCreateSong);
        songComp = findViewById(R.id.composerCreateSong);
        songGenre = findViewById(R.id.genreCreateSong);
        songDec = findViewById(R.id.decadeCreateSong);
        songInst= findViewById(R.id.instrumentCreateSong);

        //Connect create song button to the var
        createSong = findViewById(R.id.createSongCreateSong);
        createSong.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Create a song element with the data fields the user entered.
                //See if the user left any blank and tell them
                if(songInst.getText().toString().equals("") || songGenre.getText().toString().equals("") || songComp.getText().toString().equals("") || songName.getText().toString().equals("") || songDec.getText().toString().equals("")){
                    Toast.makeText(CreateSong.this, "Please enter all the information", Toast.LENGTH_SHORT).show();
                }else {
                    Song newSong = new Song();
                    newSong.setKey(allLists.getAllSongs().size());
                    newSong.setDoc(songInst.getText().toString());
                    newSong.setGenre(songGenre.getText().toString());
                    newSong.setComposer(songComp.getText().toString());
                    newSong.setName(songName.getText().toString());
                    newSong.setDecade(Integer.valueOf(songDec.getText().toString()));

                    saveJSONToInternal(oldJson, newSong);


                    //Copy the pdf to the files directory


                    finish();
                }
            }
        });


        //returns to main menu
        menuButton = findViewById(R.id.menuButtonViewSet);
        menuButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        chooseFileButton = findViewById(R.id.choosefilebutton);
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,0);
                chooseFileButton.setEnabled(false);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch(requestCode){

            case 0:

                if(resultCode==RESULT_OK){


                    Uri uri = data.getData();

                    String src = uri.getPath();

                    src = src.substring(0, 10);
                    src = src + getFileName(uri);

                    File source = new File(src);


                    Toast.makeText(this,src + " was chosen", Toast.LENGTH_SHORT).show();
                    File destination = new File(pdfPath + "/" + source);

                    if(!destination.exists()){
                        try {
                            destination.createNewFile();
                        }catch(IOException e){
                        }
                    }

                    try {
                        InputStream is = this.getContentResolver().openInputStream(uri);
                        byte[] bytesArray = new byte[is.available()];
                        is.read(bytesArray);
                        FileUtils.writeByteArrayToFile(destination, bytesArray);
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                }
                break;

        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //Allows us to read json from internal file and return a string
    public String loadJSONFromInternal(){
        File file = new File(jsonPath);
        String json = "";
        try {

            FileInputStream fis = new FileInputStream(new File(jsonPath));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            json = sb.toString();
            fis.close();
            return json;
        }catch(FileNotFoundException ew){
            Toast.makeText(this, "FileNotFound in LoadJSONFromInternal",Toast.LENGTH_SHORT);
        } catch(IOException e){
            Toast.makeText(this, "IOException in LoadJSONFromInternal", Toast.LENGTH_SHORT);
        }
        return json;
    }

    //Allow us to add the changes to the file
    public void saveJSONToInternal(String old, Song newSong){
        File file = new File(jsonPath);
        if(file.exists()){
            file.delete();
        }
        String newJson ="";

        try {
            //Use method to return a string of the json data, a json file is one large JSON object so make a JSON object
            JSONObject fileObj = new JSONObject(old);

            //The next thing in the object is the JSON Array of Songs
            JSONArray songJArray = fileObj.getJSONArray("songs");

            //Create a Object to hold all new song data
            JSONObject newObj = new JSONObject();
            newObj.put("songId", newSong.getKey());
            newObj.put("songTitle", newSong.getName());
            newObj.put("songComposer", newSong.getComposer());
            newObj.put("songEra", newSong.getDecade());
            newObj.put("songInstrument", newSong.getDoc());
            newObj.put("songGenre", newSong.getGenre());

            //put the new object in the songarray
            songJArray.put(newObj);

            //Make a new JSON object to put the new Array in
            JSONObject mainObj = new JSONObject();
            mainObj.put("songs",songJArray);

            //Overwrite the file with the new data added to the current data

            writeInternalFile(mainObj.toString());

        }catch(JSONException e){

        }

    }

    //Method to write internal file if it does not exist.
    public void writeInternalFile(String s){
        //Write the file if it doesnt exist
        try {
            //Write default file internal file, using json file from raw
            FileOutputStream fileout = openFileOutput("songData.json", MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(s);
            outputWriter.close();
            fileout.close();


        }catch(FileNotFoundException fe){
            Toast.makeText(this,"File Not Found Writing to file", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        }
    }

}
