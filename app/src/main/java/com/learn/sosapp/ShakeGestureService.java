package com.learn.sosapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShakeGestureService extends Service implements SensorEventListener {

    private static final String TAG = "SOSApp";
    private static final float ERROR = (float) 7.0;
    int count = 1;
    private boolean init;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private float x1, x2, x3;

    @Override
    public IBinder onBind(Intent intent) {
        //Log.d(TAG, "ShakeGestureService :: onBind called()");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, "ShakeGestureService :: onStartCommand called");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> listOfSensorsOnDevice = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        for (int i = 0; i < listOfSensorsOnDevice.size(); i++) {
            if (listOfSensorsOnDevice.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {

                Toast.makeText(this, "ACCELEROMETER sensor is available on device", Toast.LENGTH_SHORT).show();


                init = false;

                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            } else {

                Toast.makeText(this, "ACCELEROMETER sensor is NOT available on device" + listOfSensorsOnDevice.get(i).getName(), Toast.LENGTH_SHORT).show();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        //Get x,y and z values
        float x, y, z;
        x = e.values[0];
        y = e.values[1];
        z = e.values[2];
        if (!init) {
            x1 = x;
            x2 = y;
            x3 = z;
            init = true;
        } else {
            float diffX = Math.abs(x1 - x);
            float diffY = Math.abs(x2 - y);
            float diffZ = Math.abs(x3 - z);
            //Handling ACCELEROMETER Noise
            if (diffX < ERROR) {
                diffX = (float) 0.0;
            }
            if (diffY < ERROR) {
                diffY = (float) 0.0;
            }
            if (diffZ < ERROR) {
                diffZ = (float) 0.0;
            }
            x1 = x;
            x2 = y;
            x3 = z;
            //Horizontal Shake Detected!
            if (diffX > diffY) {
                //counter.setText("Shake Count : "+ count);
                count = count + 1;
                Log.d(TAG, "Shake Detected!");
                Toast.makeText(this, "Shake Detected!", Toast.LENGTH_SHORT).show();
                sendSMS();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @SuppressLint("MissingPermission")
    private void sendSMS() {
        Log.d(TAG, "Sending Location");

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        //Log.d(TAG, "location not null");
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
                        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        Log.d(TAG, sharedpreferences.getString("contact1", ""));


//                        Intent intent = new Intent(getApplicationContext(), ShakeGestureService.class);
//                        PendingIntent pi1 = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                        //PendingIntent pi2=PendingIntent.getActivity(getApplicationContext(), 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        //PendingIntent pi3=PendingIntent.getActivity(getApplicationContext(), 2, intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        Log.d(TAG, "I am in Danger, HELP ME!!\n My Current Location is:\n" + " " + cityName + " " + stateName + " " + countryName);
                        try {
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(sharedpreferences.getString("contact1", ""), null, "I am in Danger, HELP ME!!\n My Current Location is:\n" + " " + cityName + " " + stateName + " " + countryName, null, null);
                            sms.sendTextMessage(sharedpreferences.getString("contact2","").toString(), null, "I am in Danger, HELP ME!!\n My Current Location is:\n"+cityName+" "+stateName+" "+countryName , null,null);
                            sms.sendTextMessage(sharedpreferences.getString("contact3","").toString(), null, "I am in Danger, HELP ME!!\n My Current Location is:\n"+cityName+" "+stateName+" "+countryName , null,null);
                        } catch (Exception e) {
                            Log.d(TAG, "Exception occured -> " + e, e);
                        }
                        Toast.makeText(getApplicationContext(), "cityname: " + cityName + " stateName: " + stateName + " countryName: " + countryName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "location is null", Toast.LENGTH_SHORT).show();
                    }
                });
            //}
        } catch (Exception E) {
            Log.d(TAG, E + "Exception Occured!!!");
        }
        Log.d(TAG, "Message Sent successfully!");

    }

}
