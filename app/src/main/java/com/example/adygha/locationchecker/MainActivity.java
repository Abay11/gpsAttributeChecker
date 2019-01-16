package com.example.adygha.locationchecker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    public int notificationId=111;
    public String CHANNEL_ID="Main channel";
    public String SERVICE_NAME="Location Checker Service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void startServiceClicked(View view)
    {
        System.out.println("Start clicked");
//        Intent i= new Intent(this, LocationCheckerService.class);
//        potentially add data to the intent
//        i.putExtra("KEY1", "Value to be used by the service");
//        this.startService(i);
//        Intent intent = new Intent(this, LocationCheckerService.class);
//        startService(intent);
    }

    public void stopServiceClicked(View view)
    {
        System.out.println("Stop clicked");
    }

    public void checkStateClicked(View view)
    {
        System.out.println("Check clicked");
        if(isServiceRunning())
            System.out.println("Service is running");
        else
            System.out.println("Service is not running");
    }

    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SERVICE_NAME.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
