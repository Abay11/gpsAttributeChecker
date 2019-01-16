package com.example.adygha.locationchecker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static String APP_NAME="Location attribute checker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonsState(isServiceRunning());
    }

    @Override
    protected void onResume()
    {
        setButtonsState(isServiceRunning());
        super.onResume();
    }

    public void startServiceClicked(View view)
    {
        Log.d(APP_NAME, "Start clicked");
        Intent intent = new Intent(this, LocationCheckerService.class);
        startService(intent);

        boolean isRunningState=true;
        setButtonsState(isRunningState);

        Toast.makeText(this, "The service run", Toast.LENGTH_SHORT).show();
    }

    public void stopServiceClicked(View view)
    {
        Log.d(APP_NAME, "Stop clicked");
        Intent intent = new Intent(this, LocationCheckerService.class);
        stopService(intent);

        boolean isRunningState=false;
        setButtonsState(isRunningState);

        Toast.makeText(this, "The service stopped", Toast.LENGTH_SHORT).show();
    }

    public void checkPhotosClicked(View view)
    {
        Log.d(APP_NAME, "Check clicked");
        String dir="sdcard/PhotoFolder/";

//        File file = lastFileModified(path);
        ArrayList<String> notTagged=new ArrayList<>();
        File[] files=getFileList(dir);


        for(File f : files)        {
            {
                if(!hasLocationTag(f.getAbsolutePath()))
                    notTagged.add(f.getName());
            }
        }

        System.out.println("Not tagged\n");
        for (String f: notTagged)
            System.out.println(f);
    }

    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationCheckerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void setButtonsState(boolean state)
    {
        Button start=findViewById(R.id.startButton);
        Button stop=findViewById(R.id.stopButton);
        start.setEnabled(!state);
        stop.setEnabled(state);
    }

    public static File lastFileModified(String dir) {
        File[] files = getFileList(dir);

        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }

    public static File[] getFileList(String dir)
    {
        File fl = new File(dir);
        File[] files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        });

        return files;
    }

    public boolean hasLocationTag(String fileName)
    {
        try {
            ExifInterface exifInterface = new ExifInterface(fileName);
            String latitude=exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            if (latitude==null)
                return false;
            else return true;
        }catch (IOException e)
        {
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
