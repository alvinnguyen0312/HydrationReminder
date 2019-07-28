package com.example.knguyen.mywaterdrinkingapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

public class Setting extends AppCompatActivity {

    public static final String CHANNEL_ID = "channel_01";
    public static final CharSequence CHANNEL_NAME = "KIET NGUYEN";
    public static final int NOTIFICATION_ID = 1;

    ToggleButton toggleButtonGender, toggleButtonWeightUnit, toggleButtonWaterUnit;
    EditText editTextWeightNum, editTextWaterAmount, intervalNotification;
    Switch switchNotification;
    String gender = "male";
    String weightUnit = "lbs";
    String waterUnit = "oz";
    String notification = "";
    int hour, minute, interval;
    TextView tvInterval;
    LinearLayout intervalLayout;
    Button Backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        toggleButtonGender = findViewById(R.id.toggleButtonGender);
        toggleButtonWeightUnit = findViewById(R.id.toggleButtonWeightUnit);
        toggleButtonWaterUnit = findViewById(R.id.toggleButtonWaterUnit);
        editTextWaterAmount = findViewById(R.id.editTextWaterAmount);
        editTextWeightNum = findViewById(R.id.editTextWeightNum);
        switchNotification = findViewById(R.id.switchNotification);
        intervalNotification = findViewById(R.id.editTextInterval);
        tvInterval = findViewById(R.id.textViewInterval);
        intervalLayout = findViewById(R.id.LayoutIntervalNotification);
        findViewById(R.id.LayoutIntervalNotification).setVisibility(View.GONE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setIcon(R.drawable.waterdrop);
    }

    public void onToggleButtonGender(View view) {
        gender = (toggleButtonGender.getText().toString() == "Male") ? "Male" : "Female";
    }

    public void onToggleButtonWeightUnit(View view) {
        weightUnit = (toggleButtonWeightUnit.getText().toString() == "lbs") ? "lbs" : "kgs";
    }

    public void onToggleButtonWaterUnit(View view) {
        waterUnit = (toggleButtonWaterUnit.getText().toString() == "ml") ? "ml" : "oz";
    }

    public void onClickBackHomeButton(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences setting = getSharedPreferences("setting", Context.MODE_PRIVATE);
        editTextWaterAmount.setText(setting.getString("waterAmount", "0"));
        editTextWeightNum.setText(setting.getString("weightNum", "0"));
        toggleButtonGender.setText(setting.getString("gender", ""));
        toggleButtonWaterUnit.setText(setting.getString("waterUnit", ""));
        toggleButtonWeightUnit.setText(setting.getString("weightUnit",""));
        switchNotification.setChecked(setting.getBoolean("notificationAllowed",
                Boolean.parseBoolean("false")));
        intervalNotification.setText(setting.getString("interval","00:00"));
        if(!switchNotification.isChecked()){
        findViewById(R.id.LayoutIntervalNotification).setVisibility(View.GONE);
        intervalNotification.setText("00:00");
        }
        else{
            findViewById(R.id.LayoutIntervalNotification).setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences setting = getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("gender",toggleButtonGender.getText().toString());
        editor.putString("waterUnit",toggleButtonWaterUnit.getText().toString());
        editor.putString("weightUnit",toggleButtonWeightUnit.getText().toString());
        editor.putString("weightNum", editTextWeightNum.getText().toString());
        editor.putString("waterAmount", editTextWaterAmount.getText().toString());
        editor.putBoolean("notificationAllowed",switchNotification.isChecked());
        editor.putString("interval",intervalNotification.getText().toString());
        editor.apply();
    }


    public void onClickSaveButton(View view) {
        try
        {
            FileOutputStream fout = openFileOutput("user_profile.txt", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fout);
            gender = toggleButtonGender.isChecked() ? toggleButtonGender.getTextOn().toString() :
                    toggleButtonGender.getTextOff().toString();
            waterUnit = toggleButtonWaterUnit.isChecked() ? toggleButtonWaterUnit.getTextOn().toString() :
                    toggleButtonWaterUnit.getTextOff().toString();
            weightUnit = (toggleButtonWeightUnit.isChecked()) ? toggleButtonWeightUnit.getTextOn().toString() :
                    toggleButtonWeightUnit.getTextOff().toString();
            notification = switchNotification.isChecked() ? "allowed" : "not allowed";
            if(notification == "allowed")
            {
                String[] time = intervalNotification.getText().toString().split(":");
                if (time.length > 0) {
                    hour = Integer.parseInt(time[0]);
                    minute = Integer.parseInt(time[1]);
                }
                //set up notification interval
                interval = (hour * 60 + minute) * 60 * 1000;
                //Create notification channel
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                    channel.setDescription("Water Drinking App");
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                Intent intent = new Intent(this, Setting.class);
                final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                Timer timer = new Timer();
                //Set the schedule function
                timer.scheduleAtFixedRate(new TimerTask() {
                  @Override
                  public void run() {
                      //send notification
                      builder.setSmallIcon(R.drawable.waterdrop);
                      builder.setContentIntent(pendingIntent);
                      builder.setAutoCancel(true);
                      builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                              R.drawable.waterdrop));
                      builder.setContentTitle("Water Drinking Reminder");
                      builder.setContentText("Time to drink water!");
                      builder.setSubText("Tap to go to the application");
                      NotificationManager notificationManager = (NotificationManager) getSystemService(
                              NOTIFICATION_SERVICE);
                      notificationManager.notify(NOTIFICATION_ID, builder.build());
                  }
              },interval, interval);
            }
            //Write content to file
            osw.write("Your gender is " + gender );
            osw.write("\nYour weight is " + editTextWeightNum.getText().toString() + " ");
            osw.write(weightUnit);
            osw.write("\nYour daily quantity of water is " +
                    editTextWaterAmount.getText().toString() + " ");
            osw.write(waterUnit);
            osw.write("\nThe notification is " + notification);
            osw.write("\nNotification will be sent every " + hour + ":" + minute);
            osw.flush();
            osw.close();
            Intent i = new Intent(this,Home.class);
            i.putExtra("waterAmount",editTextWaterAmount.getText().toString());
            i.putExtra("waterUnit", waterUnit);
            i.putExtra("hour", hour);
            i.putExtra("minute", minute);
            startActivity(i);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClickAboutButton(View view) {
        Toast toast = Toast.makeText(this, R.string.intro,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,80);
        TextView tv = toast.getView().findViewById(android.R.id.message);
        if( tv != null) {
            tv.setGravity(Gravity.CENTER);
        }
        toast.show();
    }

    public void onClickNotificationButton(View view) {
        if(switchNotification.isChecked())
        {
            intervalLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            intervalLayout.setVisibility(View.GONE);
        }
    }

}
