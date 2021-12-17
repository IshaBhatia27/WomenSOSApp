package com.learn.sosapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class NewLockService extends Service {

    private static final String TAG = "SOSApp";

    public NewLockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"NewLockService :: onStartCommand called");
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        final BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        Log.d(TAG, "On Start Command called");
        startForeground(1,"background service running");

        return super.onStartCommand(intent, flags, startId);
    }
    void startForeground(int id, String Notification) {
        Log.d(TAG, "Start Foreground called");
        createNotificationChannel();


        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
////        int NOTIF_ID=1;
////        int NOTIF_CHANNEL_ID=R.string.channel_name;
        String NOTIF_CHANNEL_ID = "channel1";
        startForeground(id, new NotificationCompat.Builder(this,
                String.valueOf(NOTIF_CHANNEL_ID)) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
                Toast.makeText(this, "Running in Background", Toast.LENGTH_SHORT).show();

    }


    private void createNotificationChannel() {
        Log.d(TAG, "create notification channel called");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "mychannel";
                String description = "notification channel";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                String CHANNEL_ID="channel1";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
        } catch (Exception e){
            Log.d(TAG, "Ecxception: "+e);
        }

        Log.d(TAG, "Notification Channel Created");
    }

}