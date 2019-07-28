package com.example.knguyen.mywaterdrinkingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Home extends AppCompatActivity {

    ProgressBar waterProgressBar;
    TextView waterAmountDisp;
    int waterAmountNum, hour, minute;
    int calWaterAmt;
    String waterUnit;
    Date currentTime = Calendar.getInstance().getTime() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setIcon(R.drawable.waterdrop);
        waterProgressBar = findViewById(R.id.progressBar);
        waterAmountDisp = findViewById(R.id.textViewWaterAmountDisp);
        waterAmountNum = Integer.valueOf(getIntent().getExtras().getString("waterAmount"));
        waterUnit = getIntent().getExtras().getString("waterUnit");
        waterAmountDisp.setText(getIntent().getExtras().getString("waterAmount") + " " +
                getIntent().getExtras().getString("waterUnit"));

    }

    public void onClickButton(View view) throws JSONException {
    switch(view.getId()){
        case R.id.buttonDrink:
            int newProgress = waterProgressBar.getProgress() - (waterProgressBar.getMax()/10);
            int countGlass = 1;
            if(newProgress < 0) {
                newProgress = 0;

                Toast toast = Toast.makeText(this,R.string.congrat, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_VERTICAL,0,200);
                TextView tv = toast.getView().findViewById(android.R.id.message);
                if( tv != null) {
                    tv.setGravity(Gravity.CENTER);
                }
                toast.show();
            }
            waterProgressBar.setProgress(newProgress);
            calWaterAmt = newProgress * waterAmountNum/100;
            waterAmountDisp.setText(String.valueOf(calWaterAmt)+ " " +
                    getIntent().getExtras().getString("waterUnit"));
            currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
            String currentTimeFormatted = formatter.format(currentTime);
            try
            {

                FileOutputStream fout = openFileOutput("history.txt", MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fout);
                osw.write(currentTimeFormatted + "-" + (waterAmountNum - calWaterAmt) + "\n");

                osw.flush();
                osw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            break;
        case R.id.buttonSetting:
            gotoSetting();
            break;
        case R.id.buttonHistory:
            gotoHistory();
            break;
        case R.id.ButtonUndo:
            int previousProgress = waterProgressBar.getProgress() + (waterProgressBar.getMax()/10);
            waterProgressBar.setProgress(previousProgress);
            calWaterAmt = previousProgress * waterAmountNum/100;
            waterAmountDisp.setText(String.valueOf(calWaterAmt)+ " " +
                    getIntent().getExtras().getString("waterUnit"));
            break;
        case R.id.ButtonReset:
            newProgress = 100;
            waterProgressBar.setProgress(newProgress);
            waterAmountDisp.setText(getIntent().getExtras().getString("waterAmount")+ " " +
                    getIntent().getExtras().getString("waterUnit"));
            break;
    }

    }

    private void gotoHistory() {
        Intent intent = new Intent(this,History.class );
        startActivity(intent);
    }

    private void gotoReminder() {
        Intent intent =  new Intent(this, Calendar.class);
        startActivity(intent);
    }

    public void gotoSetting() {
        Intent intent = new Intent(this,Setting.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences setting = getSharedPreferences("setting", Context.MODE_PRIVATE);
        waterProgressBar = findViewById(R.id.progressBar);
        waterAmountDisp = findViewById(R.id.textViewWaterAmountDisp);
        waterAmountNum = Integer.valueOf(getIntent().getExtras().getString("waterAmount"));
        waterUnit = getIntent().getExtras().getString("waterUnit");
        waterAmountDisp.setText(setting.getString("waterAmountDisplay", ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences setting = getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        String currentProgress = String.valueOf(waterProgressBar.getProgress()*waterAmountNum/100);
        editor.putString("waterAmountDisplay", currentProgress + " " + waterUnit);
        editor.apply();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
