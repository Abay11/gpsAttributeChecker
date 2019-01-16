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
    static boolean isProceed=true;

    private String MAIN_CHANNEL_ID = "Main notify channel";
    private String CHANNEL_ID = "Notify channel";
    int MAIN_NOTIFICATION_ID = 37111;
    int NOTIFICATION_ID = 37112;

    public LocationCheckerService() {
        super(SERVICE_NAME);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(SERVICE_NAME, "Running "+SERVICE_NAME);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MAIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(MainActivity.APP_NAME)
                .setContentText("The service is run")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(MAIN_NOTIFICATION_ID, mBuilder.build());
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        isProceed=false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null)
        Log.d(SERVICE_NAME, "onHandleIntent called");
        checkAttributes();
    }

    protected void checkAttributes(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(MainActivity.APP_NAME)
                .setContentText("Test content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        while(isProceed)
        {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // NOTIFICATION_ID is a unique int for each notification that you must define
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

        notificationManager.cancelAll();
        isProceed=true;
    }
}
