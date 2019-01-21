package com.example.adygha.locationchecker;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import yogesh.firzen.filelister.FileListerDialog;
import yogesh.firzen.filelister.OnFileSelectedListener;

public class MainActivity extends AppCompatActivity {
    static String APP_NAME="Location attribute checker";
    static final String DEFAULT_DIRECTORY = "sdcard/DCIM/Camera MX/";
    static String CURRENT_DIRECTORY = DEFAULT_DIRECTORY;

    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonsState(isServiceRunning());

        EditText directory=(EditText)findViewById(R.id.editText);
        directory.setText(DEFAULT_DIRECTORY);
        directory.setFocusable(false);
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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(APP_NAME, "READING the external storage is not granted");
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            String dirPath = ((EditText) findViewById(R.id.editText)).getText().toString();
            if (isCorrectDir(dirPath)) {
                CURRENT_DIRECTORY = dirPath;

                Intent serviceIntent = new Intent(this, LocationCheckerService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }

                boolean isRunningState = true;
                setButtonsState(isRunningState);

                Toast.makeText(this, "Служба запущена", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Указанное имя не является папкой или не существует", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void stopServiceClicked(View view)
    {
        Log.d(APP_NAME, "Stop clicked");
        Intent intent = new Intent(this, LocationCheckerService.class);
        stopService(intent);

        boolean isRunningState=false;
        setButtonsState(isRunningState);

        Toast.makeText(this, "Служба остановлена", Toast.LENGTH_SHORT).show();
    }

    public void checkPhotosClicked(View view)
    {
        Log.d(APP_NAME, "Check clicked");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(APP_NAME, "READING the external storage is not granted");
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {

            String dirPath = ((EditText) findViewById(R.id.editText)).getText().toString();

            if (!isCorrectDir(dirPath)) {
                Toast.makeText(this, "Указанное имя не является папкой или не существует", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> notTagged = new ArrayList<>();

            boolean isSubdirsIncluded = ((CheckBox)findViewById(R.id.checkBox)).isChecked();
            File[] files = getFileList(dirPath, isSubdirsIncluded);

            int untaggedCount = 0;
            String res = new String();
            for (File f : files) {
                if (!hasLocationTag(f.getAbsolutePath())) {
                    if (!res.isEmpty()) res += "\n";
                    res += f.getName();

                    ++untaggedCount;
                }
            }

            if (res.isEmpty())
                res += "Не найдено неотмеченных фото";
            else
                res = "У " + Integer.toString(untaggedCount) + " из " + Integer.toString(files.length) + " фото нет GPS меток:\n" + res;

            AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
            popupBuilder.setMessage(res);
            popupBuilder.show();
        }

    }

    public ArrayList<String> getUntaggedPhotos(String dir)
    {

        return null;
    }

    public void selectDirectoryClicked(View view)
    {
        Log.d(APP_NAME, "Select directory clicked");


        FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(this);
        fileListerDialog.setDefaultDir(DEFAULT_DIRECTORY);
        fileListerDialog.setFileFilter(FileListerDialog.FILE_FILTER.DIRECTORY_ONLY);
        fileListerDialog.setOnFileSelectedListener(new OnFileSelectedListener() {
            @Override
            public void onFileSelected(File file, String path) {
                //your code here
                CURRENT_DIRECTORY = file.getPath();
                EditText editor=(EditText)findViewById(R.id.editText);
                editor.setText(CURRENT_DIRECTORY);
                Log.d(APP_NAME, "Dir picked");
            }
        });

        fileListerDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(APP_NAME, "Permission granted");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.d(APP_NAME, "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
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

    public static File lastFileModified(String dir, boolean isDirsIncluded) {
        File[] files = getFileList(dir, isDirsIncluded);

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

    public static File[] getFileList(String dir, final boolean isDirsIncluded)
    {
        File fl = new File(dir);
        File[] files;

        files = fl.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && (file.getName().endsWith("jpg")
                        || file.getName().endsWith("jpeg")));
            }
        });

        if(isDirsIncluded) {
            File[] dirs;
            dirs = fl.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

            for (File f : dirs) {
                File[] subDirFiles = getFileList(f.getAbsolutePath(), isDirsIncluded);
                File[] appendLists = new File[subDirFiles.length + files.length];

                for (int i = 0; i < files.length; ++i) {
                    appendLists[i] = files[i];
                }

                for (int i = 0; i < subDirFiles.length; ++i) {
                    appendLists[files.length + i] = subDirFiles[i];
                }

                files = appendLists;
            }
        }

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
