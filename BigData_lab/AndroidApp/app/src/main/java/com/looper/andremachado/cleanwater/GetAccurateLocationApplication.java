package com.looper.andremachado.cleanwater;

import android.app.Application;
import android.content.Context;
import android.location.Location;

/**
 * Created by andremachado on 09/06/2017.
 */

public class GetAccurateLocationApplication extends Application {

    public static Location mCurrentLocation;
    public static String locationProvider;
    public static Location oldLocation;
    public static String locationTime;

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
    }

    private void initApplication() {
        mContext = getApplicationContext();
    }
}
