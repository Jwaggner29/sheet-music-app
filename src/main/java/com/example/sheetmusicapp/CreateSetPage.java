package com.example.sheetmusicapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;


public class CreateSetPage extends AppCompatActivity {

    private Button menuButton;
    private Button SortButton;
    private Button makeSet;
    private SearchView songSearchView;
    SongList allLists = SongList.getInstance();
    private ArrayList<Song> temp = new ArrayList<>();
    private ArrayList<Song>  searchTemp=new ArrayList<>();
    Context context = this;
    String jsonPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_set_page);
        jsonPath = context.getFilesDir() + "/" + "songData.json";

        String json = "";

        json = loadJSONFromInternal();


        allLists.clearSongList();
        //try catch for reading in data for default song data
        try {
            //Use method to return a string of the json data, a json file is one large JSON object so make a JSON object
            JSONObject fileObj = new JSONObject(loadJSONFromInternal());

            //The next thing in the object is the JSON Array of Songs
            JSONArray songJArray = fileObj.getJSONArray("songs");


            //Iterate through songs and add each songs to the Array of AllSongs
            for (int i = 0; i < songJArray.length(); i++) {
                Song readInSong = new Song();
                JSONObject songJsonObject = (JSONObject) songJArray.get(i);
                readInSong.setKey(songJsonObject.getInt("songId"));
                readInSong.setName(songJsonObject.getString("songTitle"));
                readInSong.setComposer(songJsonObject.getString("songComposer"));
                readInSong.setDecade(songJsonObject.getInt("songEra"));
                readInSong.setDoc(songJsonObject.getString("songInstrument"));
                readInSong.setGenre(songJsonObject.getString("songGenre"));

                //Add the song from the json object to the array
                allLists.addToSongList(readInSong);

            }
        } catch (JSONException e) {
            //Have a toast pop up with an error message
            Toast errorToast = Toast.makeText(getApplicationContext(),
                    e.getMessage(),
                    Toast.LENGTH_LONG);

            errorToast.show();
        }


        //Look up to dynamically create checkboxes and apply text to them
        //Create a text box per song
        LinearLayout linearLayoutCreateSet = findViewById(R.id.linearLayoutCreateSet);
        for (int i = 0; i < allLists.getAllSongs().size(); i++) {
            //Make a new checkbox for each song in the allsongs
            final CheckBox cb = new CheckBox(getApplicationContext());
            //set the id for the current loop of the new checkbox
            cb.setId(i);
            //set the text of the new checkbox of song string
            cb.setText(allLists.getAllSongs().get(i).toString());
            //On check new listener for if the check box is checked
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //if checked add it to the temp list, the song
                    if (isChecked) {

                        if (!temp.isEmpty()) {
                            if (!(temp.contains(allLists.getAllSongs().get(cb.getId())))) {
                                temp.add(allLists.getAllSongs().get(cb.getId()));
                            }
                        } else {
                            temp.add(allLists.getAllSongs().get(cb.getId()));
                        }


                        //If unchecked remove the song from the temp list if it exist in it
                    } else {

                        if (!temp.isEmpty()) {
                            if (temp.contains(allLists.getAllSongs().get(cb.getId()))) {
                                temp.remove(allLists.getAllSongs().get(cb.getId()));
                            }
                        }
                    }
                }
            });
            linearLayoutCreateSet.addView(cb);
        }


        //Returns to main menu and saves the currently selected songs to the set
        makeSet = findViewById(R.id.searchBarCreateSet);
        makeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clear the current set if its not empty
                if (!(allLists.getSetList().isEmpty())) {
                    //Clears both pdf set and setlist
                    allLists.clearSet();
                }
                //Copy the temp array into the actual set array
                if (allLists.getSetList().isEmpty()) {
                    for (int i = 0; i < temp.size(); i++) {
                        allLists.addToSet(temp.get(i));

                        //get the pdf name and add to pdflist
                        String songName = temp.get(i).getName().replace(" ", "").toLowerCase();
                        songName = songName + ".pdf";
                        allLists.addToPDFList(songName);

                    }
                }

                //Exit and go back to main menu
                finish();

            }
        });


        //returns to main menu
        menuButton = findViewById(R.id.menuButtonViewSet);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //shows popup menu for fields to search by
        SortButton = findViewById(R.id.sortCreateSet);
        SortButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PopupMenu popupMenu = new PopupMenu(CreateSetPage.this, SortButton);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(CreateSetPage.this,"" + item.getTitle(),Toast.LENGTH_SHORT).show();
                        linearLayoutCreateSet.removeAllViews();
                        allLists.sortBy(item.getTitle().toString().toLowerCase());
                        for(int i = 0; i < allLists.getAllSongs().size(); i++){
                            //Make a new checkbox for each song in the allsongs
                            final CheckBox cb = new CheckBox(getApplicationContext());
                            //set the id for the current loop of the new checkbox
                            cb.setId(i);
                            //set the text of the new checkbox of song string
                            cb.setText(allLists.getAllSongs().get(i).toString());
                            if (temp.contains(allLists.getAllSongs().get(i)))
                                cb.setChecked(true);
                            //On check new listener for if the check box is checked
                            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                                    //if checked add it to the temp list, the song
                                    if(isChecked){

                                        if(!temp.isEmpty()){
                                            if(!(temp.contains(allLists.getAllSongs().get(cb.getId())))){
                                                temp.add(allLists.getAllSongs().get(cb.getId()));
                                            }
                                        } else{
                                            temp.add(allLists.getAllSongs().get(cb.getId()));
                                        }


                                        //If unchecked remove the song from the temp list if it exist in it
                                    }else{

                                        if(!temp.isEmpty()){
                                            if(temp.contains(allLists.getAllSongs().get(cb.getId()))){
                                                temp.remove(allLists.getAllSongs().get(cb.getId()));
                                            }
                                        }
                                    }
                                }
                            });
                            linearLayoutCreateSet.addView(cb);
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        //sorting
        songSearchView = findViewById(R.id.songSearchView);
        songSearchView.setOnCloseListener(new SearchView.OnCloseListener(){
            public boolean onClose(){
                linearLayoutCreateSet.removeAllViews();
                for (int i = 0; i < allLists.getAllSongs().size(); i++) {
                    //Make a new checkbox for each song in the allsongs
                    final CheckBox cb = new CheckBox(getApplicationContext());
                    //set the id for the current loop of the new checkbox
                    cb.setId(i);
                    //set the text of the new checkbox of song string
                    cb.setText(allLists.getAllSongs().get(i).toString());
                    if (temp.contains(allLists.getAllSongs().get(i))){
                        cb.setChecked(true);
                    }
                    //On check new listener for if the check box is checked
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            //if checked add it to the temp list, the song
                            if (isChecked) {

                                if (!temp.isEmpty()) {
                                    if (!(temp.contains(allLists.getAllSongs().get(cb.getId())))) {
                                        temp.add(allLists.getAllSongs().get(cb.getId()));
                                    }
                                } else {
                                    temp.add(allLists.getAllSongs().get(cb.getId()));
                                }


                                //If unchecked remove the song from the temp list if it exist in it
                            } else {

                                if (!temp.isEmpty()) {
                                    if (temp.contains(allLists.getAllSongs().get(cb.getId()))) {
                                        temp.remove(allLists.getAllSongs().get(cb.getId()));
                                    }
                                }
                            }
                        }
                    });
                    linearLayoutCreateSet.addView(cb);
                }
                return true;
            }
        });
        songSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                linearLayoutCreateSet.removeAllViews();
                searchTemp=allLists.searchSongs(query);
                for (int i = 0; i < allLists.getAllSongs().size(); i++) {
                    if (searchTemp.contains(allLists.getAllSongs().get(i))) { //if it's a searched-positive song
                        //Make a new checkbox for each song in search temp
                        final CheckBox cb = new CheckBox(getApplicationContext());
                        //set the id for the current loop of the new checkbox
                        cb.setId(i);
                        //set the text of the new checkbox of song string
                        cb.setText(allLists.getAllSongs().get(i).toString());
                        //On check new listener for if the check box is checked
                        if (temp.contains(allLists.getAllSongs().get(i))){
                            cb.setChecked(true);
                        }
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                //if checked add it to the temp list, the song
                                if (isChecked) {

                                    if (!temp.isEmpty()) {
                                        if (!(temp.contains(allLists.getAllSongs().get(cb.getId())))) {
                                            temp.add(allLists.getAllSongs().get(cb.getId()));
                                        }
                                    } else {
                                        temp.add(allLists.getAllSongs().get(cb.getId()));
                                    }


                                    //If unchecked remove the song from the temp list if it exist in it
                                } else {

                                    if (!temp.isEmpty()) {
                                        if (temp.contains(allLists.getAllSongs().get(cb.getId()))) {
                                            temp.remove(allLists.getAllSongs().get(cb.getId()));
                                        }
                                    }
                                }
                            }
                        });
                        linearLayoutCreateSet.addView(cb);
                    }
                }
                return true;
            }
            public boolean onQueryTextChange(String newText){
                linearLayoutCreateSet.removeAllViews();
                searchTemp=allLists.searchSongs(newText);
                for (int i = 0; i < allLists.getAllSongs().size(); i++) {
                    if (searchTemp.contains(allLists.getAllSongs().get(i))) { //if it's a searched-positive song
                        //Make a new checkbox for each song in search temp
                        final CheckBox cb = new CheckBox(getApplicationContext());
                        //set the id for the current loop of the new checkbox
                        cb.setId(i);
                        //set the text of the new checkbox of song string
                        cb.setText(allLists.getAllSongs().get(i).toString());
                        //On check new listener for if the check box is checked
                        if (temp.contains(allLists.getAllSongs().get(i))){
                            cb.setChecked(true);
                        }
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                //if checked add it to the temp list, the song
                                if (isChecked) {

                                    if (!temp.isEmpty()) {
                                        if (!(temp.contains(allLists.getAllSongs().get(cb.getId())))) {
                                            temp.add(allLists.getAllSongs().get(cb.getId()));
                                        }
                                    } else {
                                        temp.add(allLists.getAllSongs().get(cb.getId()));
                                    }


                                    //If unchecked remove the song from the temp list if it exist in it
                                } else {

                                    if (!temp.isEmpty()) {
                                        if (temp.contains(allLists.getAllSongs().get(cb.getId()))) {
                                            temp.remove(allLists.getAllSongs().get(cb.getId()));
                                        }
                                    }
                                }
                            }
                        });
                        linearLayoutCreateSet.addView(cb);
                    }
                }
                return true;
            }
        });

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
            return json;
        } catch(IOException e){
            return json;
        }
    }


}
