package com.looper.andremachado.cleanwater;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.looper.andremachado.cleanwater.location.LocationManagerInterface;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by andremachado on 09/06/2017.
 */

public class LocationFetcherService extends Service implements LocationManagerInterface {

    private BaseActivityLocation mBal;
    private Activity mActivity;
    private double lat;
    private double lon;
    private static final long SERVICE_PERIOD = (5) * 1000;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLocationFetching();
        mBal = new BaseActivityLocation();


        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    public class LocationBinder extends Binder {
        public LocationFetcherService getService() {
            return LocationFetcherService.this;
        }
    }

    public void initLocationFetching() {
        try {
            if (mActivity != null) {
                mBal.instatntiate(mActivity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider) {
        lat = mLocation.getLatitude();
        lon = mLocation.getLongitude();
    }

   // private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
