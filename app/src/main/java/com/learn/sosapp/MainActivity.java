package com.learn.sosapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "SOSApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Switch sw = findViewById(R.id.switch1);
        if (isMyServiceRunning(NewLockService.class)) {
            sw.setChecked(true);
            sw.setText("Disable run in Background:");
        }

        while(isMyServiceRunning(ShakeGestureService.class)){
            //Log.d(TAG, "Service Stopped :: Shake Gesture Service");
            stopService(new Intent(this, ShakeGestureService.class));
        }
        while(isMyServiceRunning(LockService.class)){
            stopService(new Intent(this, LockService.class));
            //Log.d(TAG, "Service Stopped :: LockService");
        }


        if(!isMyServiceRunning(ShakeGestureService.class)){
            //Log.d(TAG, "Service Not running :: Shake Gesture Service");
            startService(new Intent(this, ShakeGestureService.class));
        }
        if(!isMyServiceRunning(LockService.class)){
            //Log.d(TAG, "Service Not running :: LockService");
            startService(new Intent(this, LockService.class));
        }

        Button updateContacts = findViewById(R.id.UpdateContacts);
        updateContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contacts = new Intent(getApplicationContext(), UpdateEmergencyContacts.class);
                startActivity(contacts);
            }
        });
        final String MyPREFERENCES = "MyPrefs";
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("contact1", "+919935679814");
        editor.putString("contact2", "+916387784635");
        editor.putString("contact3", "+919336314008");
        editor.apply();


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    sw.setText("Disable run in Background");
                    Log.d(TAG, "Starting NewLockService");
                    if(!isMyServiceRunning(NewLockService.class)){
                        startService(new Intent(getApplicationContext(), NewLockService.class));
                    }
                    Toast.makeText(getApplicationContext(), "Button On", Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    sw.setText("Enable run in Background");
                    stopService(new Intent(getApplicationContext(), NewLockService.class));
                    Toast.makeText(getApplicationContext(), "Button Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.d(TAG,"ON Create Ended");
    }

    @SuppressWarnings("deprecation")
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}