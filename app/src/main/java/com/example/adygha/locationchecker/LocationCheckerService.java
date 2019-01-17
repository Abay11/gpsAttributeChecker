package com.example.adygha.locationchecker;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.File;

public class LocationCheckerService extends IntentService {
    static String SERVICE_NAME="LocationAttributeCheckerService";
    static boolean isProceed=true;
    static int DELAY=5000;
    private String MAIN_CHANNEL_ID = "Main notify channel";
    private String CHANNEL_ID = "Notify channel";
    int MAIN_NOTIFICATION_ID = 37111;
    int NOTIFICATION_ID = 37112;
    private String CHECKING_DIRECTORY;

    public LocationCheckerService() {
        super(SERVICE_NAME);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(SERVICE_NAME, "Running "+SERVICE_NAME);

        CHECKING_DIRECTORY = MainActivity.DIRECTORY;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MAIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(MainActivity.APP_NAME)
                .setContentText("Checking " + CHECKING_DIRECTORY)
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(MainActivity.APP_NAME)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        String prevFileName=new String();
        String modifiedFileName=new String();
        File modifiedFile;
        while(isProceed) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            modifiedFile = MainActivity.lastFileModified(CHECKING_DIRECTORY);
            if (modifiedFile != null) {
                modifiedFileName = modifiedFile.getName();
                if (!(prevFileName.equals(modifiedFileName)) && !MainActivity.hasLocationTag(modifiedFile.getAbsolutePath())) {
                    System.out.println("Modified name: " + modifiedFileName);
                    System.out.println("Priv name: " + prevFileName);

                    builder.setContentText(modifiedFileName + " has not GPS tags");
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
                prevFileName = modifiedFileName;
            }
        }


        notificationManager.cancelAll();

        isProceed=true;
    }
}
