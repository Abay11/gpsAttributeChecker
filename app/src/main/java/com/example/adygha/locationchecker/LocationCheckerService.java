package com.example.adygha.locationchecker;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
        Log.d(SERVICE_NAME, "Running "+SERVICE_NAME);
        super.onStartCommand(intent, flags, startId);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        CHECKING_DIRECTORY = MainActivity.CURRENT_DIRECTORY;

        NotificationCompat.Builder notificationBuilder;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Log.d(SERVICE_NAME, "OREO VERSION");

            NotificationChannel channel = new NotificationChannel(MAIN_CHANNEL_ID, SERVICE_NAME + " CHANNEL",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(SERVICE_NAME + " channel");

            notificationManager.createNotificationChannel(channel);

            notificationBuilder = new NotificationCompat.Builder(this, MAIN_CHANNEL_ID);

            notificationBuilder
                    .setContentTitle(MainActivity.APP_NAME)
                    .setContentText("Проверяется: " + CHECKING_DIRECTORY)
                    .setSmallIcon(R.mipmap.ic_launcher_round);

            startForeground(MAIN_NOTIFICATION_ID, notificationBuilder.build());
        }
        else
        {
            Log.d(SERVICE_NAME, "NOT OREO VERSION");

            notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(SERVICE_NAME)
                .setContentText("Проверяется: " + CHECKING_DIRECTORY)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            startForeground(MAIN_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        isProceed=false;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null) {
            Log.d(SERVICE_NAME, "onHandleIntent called");
            checkAttributes();
        }
    }

    protected void checkAttributes(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(MainActivity.APP_NAME)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_SOUND);

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
                    builder.setContentText(modifiedFileName + " не имеет GPS меток");
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }
                prevFileName = modifiedFileName;
            }
        }


        notificationManager.cancelAll();

        isProceed=true;
    }
}
