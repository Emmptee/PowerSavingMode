package com.example.shidongfang.myapplication.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by SDF on 2017/5/24.
 */

public class BaseApplication extends Application {
    private static Context sContext;


    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getApplicationContext();
    }
}
