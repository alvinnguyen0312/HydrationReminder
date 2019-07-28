package com.example.knguyen.mywaterdrinkingapp;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLogTags;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class History extends AppCompatActivity {
     TextView textViewHistory;
     BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        barChart = findViewById(R.id.bargraph);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setIcon(R.drawable.waterdrop);
        try {
            FileInputStream fin = openFileInput("history.txt");
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader buf = new BufferedReader(isr);
            List<String> lines = new ArrayList<String>();
            String[] words = new String[10];
            String line;
            while((line = buf.readLine()) != null)
            {
                lines.add(line);
            }
            ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
            ArrayList<String> theDates = new ArrayList<>();
            for(int i = 0; i < lines.size(); i++)
            {
                words = lines.get(i).split("-");
                barEntries.add(new BarEntry(Integer.valueOf(words[words.length - 1]),i));
                theDates.add(words[1]);
            }
            BarDataSet barDataSet = new BarDataSet(barEntries,"Water Consumption");
            BarData theData = new BarData(theDates,barDataSet);
            barChart.setData(theData);
            barChart.setTouchEnabled(true);
            barChart.setDragEnabled(true);
            barChart.setScaleEnabled(true);
            barChart.setDescription("Water Drinking History");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonClear(View view) {
        try {
            FileOutputStream fout = openFileOutput("history.txt", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fout);
            osw.write("");
            osw.flush();
            osw.close();
            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
