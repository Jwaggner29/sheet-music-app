package com.example.sheetmusicapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SongInfo extends AppCompatActivity {

    private Button menuButton;
    private TextView songName;
    private TextView songComp;
    private TextView songDecade;
    private TextView songInstrument;
    private TextView songGenre;
    SongList allLists = SongList.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);

        songName = findViewById(R.id.songTitleSongInfo);
        songName.setText(allLists.getSetList().get(allLists.currentSong).getName());

        songComp = findViewById(R.id.songComposerSongInfo);
        songComp.setText(allLists.getSetList().get(allLists.currentSong).getComposer());

        songDecade = findViewById(R.id.songDecadeSongInfo);
        songDecade.setText(String.valueOf(allLists.getSetList().get(allLists.currentSong).getDecade()));

        songInstrument = findViewById(R.id.songInstrumentSongInfo);
        songInstrument.setText(allLists.getSetList().get(allLists.currentSong).getDoc());

        songGenre = findViewById(R.id.songGenreSongInfo);
        songGenre.setText(allLists.getSetList().get(allLists.currentSong).getGenre());

        menuButton = findViewById(R.id.menuButtonSongInfo);
        menuButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }
}
