package com.example.sheetmusicapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class JoinBand extends AppCompatActivity {
    private static final String TAG = "com.example.sheetmusicapp";

    private ArrayList<String> m = new ArrayList<>();
    private ArrayList<String> p = new ArrayList<>();
    private ArrayList<String> temp = new ArrayList<>();

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_band);


        button = findViewById(R.id.createsongmenu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("mList", m);

                setResult(RESULT_OK, result);
                finish();
            }
        });


        //get intent
        Intent tent = getIntent();
        // get items from intent
        m = tent.getStringArrayListExtra("mList");

        //Populate the members you are currently online and connected
        ListView listView = (ListView) findViewById(R.id.memberlist);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, m);
        listView.setAdapter(adapter);

        p = tent.getStringArrayListExtra("pList");

        for(int i = 0; i < p.size(); i++){
            temp.add(p.get(i));
        }

        for(int i = 0; i < m.size(); i++){
            if(p.contains(m.get(i))){
                temp.remove(p.get(i));
            }
        }


        //Populate the people near by
        ListView discovList = (ListView) findViewById(R.id.discoveribleList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, temp);
        discovList.setAdapter(adapter1);

    }
}
