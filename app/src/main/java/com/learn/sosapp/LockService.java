package com.learn.sosapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class LockService extends Service {

    String TAG = "SOSApp";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Lock Service called");
        super.onCreate();
    }


    public class LocalBinder extends Binder {
        LockService getService() {
            return LockService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // do your jobs here
        Log.d(TAG, "LockService :: OnStartCommand called" );
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        final BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        return super.onStartCommand(intent, flags, startId);
    }
}