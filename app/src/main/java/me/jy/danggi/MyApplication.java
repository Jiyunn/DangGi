package me.jy.danggi;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by JY on 2018-01-19.
 */

public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
