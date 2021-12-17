package com.learn.sosapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;
    private static final String TAG = "SOSApp";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG,"onReceive");

        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.d(TAG,"Power Button Pressed");
            Long currentTimeStamp = System.currentTimeMillis();
            Log.d(TAG,"Pressed at -> " + currentTimeStamp);
            PowerButtonStats.timeStamps.add(currentTimeStamp);
            //Log.d(TAG,"Size -> " + PowerButtonStats.timeStamps.size());

            if(PowerButtonStats.timeStamps.size()==4  && (PowerButtonStats.timeStamps.get(3)-PowerButtonStats.timeStamps.get(0)<=6000)){
                Log.d(TAG, "Power Button Pressed 4 times");
                sendSMS(context);
                PowerButtonStats.timeStamps.clear();
            }
            if(PowerButtonStats.timeStamps.size()==4 && PowerButtonStats.timeStamps.get(3)-PowerButtonStats.timeStamps.get(0)>6000){
                PowerButtonStats.timeStamps.remove(0);
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void sendSMS(Context context) {
        Log.d(TAG, "Sending Location");

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    //Log.d(TAG, "location not null");
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String cityName = addresses.get(0).getAddressLine(0);
                    String stateName = addresses.get(0).getAddressLine(1);
                    String countryName = addresses.get(0).getAddressLine(2);

                    final String MyPREFERENCES = "MyPrefs";
                    SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    Log.d(TAG, sharedpreferences.getString("contact1", ""));

                    Log.d(TAG, "I am in Danger, HELP ME!!\n My Current Location is:\n" + " " + cityName + " " + stateName + " " + countryName);
                    try {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(sharedpreferences.getString("contact1", ""), null, "I am in Danger, HELP ME!!\n My Current Location is:\n" + " " + cityName + " " + stateName + " " + countryName, null, null);
                        sms.sendTextMessage(sharedpreferences.getString("contact2","").toString(), null, "I am in Danger, HELP ME!!\n My Current Location is:\n"+cityName+" "+stateName+" "+countryName , null,null);
                        sms.sendTextMessage(sharedpreferences.getString("contact3","").toString(), null, "I am in Danger, HELP ME!!\n My Current Location is:\n"+cityName+" "+stateName+" "+countryName , null,null);
                    } catch (Exception e) {
                        Log.d(TAG, "Exception occured -> " + e, e);
                    }
                    Toast.makeText(fusedLocationProviderClient.getApplicationContext(), "cityname: " + cityName + " stateName: " + stateName + " countryName: " + countryName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(fusedLocationProviderClient.getApplicationContext(), "location is null", Toast.LENGTH_SHORT).show();
                }
            });
            //}
        } catch (Exception E) {
            Log.d(TAG, E + "Exception Occured!!!");
        }
        Log.d(TAG, "Message Sent successfully!");

    }

}
