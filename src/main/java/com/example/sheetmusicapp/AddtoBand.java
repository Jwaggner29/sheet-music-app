package com.example.sheetmusicapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddtoBand extends AppCompatActivity {
    private static final String TAG = "com.example.sheetmusicapp";

    private ArrayList<String> p = new ArrayList<>();
    private ArrayList<String> m = new ArrayList<>();
    private ArrayList<String> temp = new ArrayList<>();

    private  Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_band);

       button = findViewById(R.id.menuButtonViewSet);
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
        p = tent.getStringArrayListExtra("pList");
        m = tent.getStringArrayListExtra("mList");

        for(int i = 0; i < p.size(); i++){
            temp.add(p.get(i));

        }
        for(int i = 0; i < m.size(); i++){
            if(p.contains(m.get(i))){
                temp.remove(p.get(i));
            }
        }

        ListView listView = (ListView) findViewById(R.id.peerlist);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, temp);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                m.add(p.get(position));

                Toast.makeText(AddtoBand.this,p.get(position) + " was added to band", Toast.LENGTH_LONG).show();


            }
        });
    }

}
