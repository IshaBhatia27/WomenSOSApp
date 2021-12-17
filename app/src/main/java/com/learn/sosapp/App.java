package com.learn.sosapp;

import android.app.Application;
import android.content.Intent;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, ShakeGestureService.class));
    }
}
