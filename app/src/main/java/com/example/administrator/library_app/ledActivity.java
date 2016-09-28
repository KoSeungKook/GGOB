package com.example.administrator.library_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Administrator on 2016-08-30.
 */
public class ledActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private String[] list_item = {"Machine Control", "Chart"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledstate);
        // listView 초기화
        ListView list = (ListView) findViewById(R.id.mainList);
        ArrayAdapter Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_item);
        list.setAdapter(Adapter);
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {

        String select_list = list_item[position];
        Intent intent = new Intent();

        if (select_list == "Machine Control") {
            intent = new Intent(this, MainActivity.class);
        }
        else if(select_list == "Chart") {
            intent = new Intent(this, SecondActivity.class);
            intent.putExtra("value", select_list);
        }
        startActivity(intent);
    }
}