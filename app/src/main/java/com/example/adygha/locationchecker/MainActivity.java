package com.example.adygha.locationchecker;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.widget.DirectedAcyclicGraph;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static String APP_NAME="Location attribute checker";
    static String DIRECTORY="sdcard/DCIM/Camera MX/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonsState(isServiceRunning());

        EditText directory=(EditText)findViewById(R.id.editText);
        directory.setText(DIRECTORY);
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

        String dirPath=((EditText)findViewById(R.id.editText)).getText().toString();
        if(isCorrectDir(dirPath)) {
//            intent.putExtra("DIR_PATH", dirPath);
            DIRECTORY=dirPath;
            startService(intent);
            boolean isRunningState=true;
            setButtonsState(isRunningState);

            Toast.makeText(this, "The service run", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "The input name is not directory or is not exists", Toast.LENGTH_SHORT).show();
        }


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
        String dirPath=((EditText) findViewById(R.id.editText)).getText().toString();

        System.out.println("dir path: "+dirPath);


        if(!isCorrectDir(dirPath))
        {
            Toast.makeText(this, "The input name is not directory or is not exists", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> notTagged=new ArrayList<>();
        File[] files=getFileList(dirPath);

        String res=new String();
        for(File f : files)
        {
            if(!hasLocationTag(f.getAbsolutePath()))
            {
                if(!res.isEmpty()) res+="\n";
                res += f.getName();
            }
        }

        if(res.isEmpty())
            res+="There are no untagged photos";

        AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
        popupBuilder.setMessage(res);
        popupBuilder.show();
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

    public static boolean hasLocationTag(String fullFileName)
    {
        try {
            ExifInterface exifInterface = new ExifInterface(fullFileName);
            String latitude=exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            if (latitude==null)
                return false;
            else
                return true;
        }catch (IOException e)
        {
            return false;
        }
    }

    public static boolean isCorrectDir(String path)
    {
        File selectedDir=new File(path);
        return selectedDir.exists() && selectedDir.isDirectory();
    }
}
