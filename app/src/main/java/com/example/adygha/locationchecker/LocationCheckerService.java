package com.example.adygha.locationchecker;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class LocationCheckerService extends IntentService {
    static String SERVICE_NAME="LocationAttributeCheckerService";

    private String CHANNEL_ID = "Main channel";
    int NOTIFICATION_ID = 37111;

    public LocationCheckerService() {
        super(SERVICE_NAME);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(SERVICE_NAME, "Running "+SERVICE_NAME);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Location attribute checker")
                .setContentText("The service is run")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_ID, mBuilder.build());

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        function();
    }

    protected void function(){
        while(true)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Test title")
                    .setContentText("Test content")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // NOTIFICATION_ID is a unique int for each notification that you must define
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
}
