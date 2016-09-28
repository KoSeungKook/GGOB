package com.example.administrator.library_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

/**
 * Created by Administrator on 2016-09-27.
 */

public class SecondActivity extends Activity{

    private ArrayList<Entry> entries; // value
    private ArrayList<String> labels; // value count
    private LineDataSet dataset; // Chart line Data
    private LineData data; // Chart line
    private LineChart lineChart; // Chart

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        String text = intent.getStringExtra("value");
        TextView sc_t = (TextView)findViewById(R.id.Text);
        sc_t.setText(text);

        lineChart = (LineChart) findViewById(R.id.chart);
        entries = new ArrayList<>();

        //entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(2f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(1009f, 5));

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");

        labels = new ArrayList<String>();
        for(int i = 0 ; i<100 ; i++) {
            String value = String.valueOf(i);
            labels.add(value);

        }

        data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        //부드럽게/각지게 할지 선택
        dataset.setDrawCubic(true);
        //선 아래부분 채우기
        dataset.setDrawFilled(false);
        dataset.setCircleColor(Color.BLACK);

        lineChart.setData(data);
        //Chart 나타나는 시간
        lineChart.animateY(500);

    }

}
