package com.example.adygha.locationchecker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
}
