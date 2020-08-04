package com.leonardorick.olx_clone;

import android.app.Application;
import android.content.Context;

public class CustomApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        CustomApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}