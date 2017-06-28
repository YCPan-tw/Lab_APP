package com.looper.andremachado.cleanwater.location;

import android.location.Location;

/**
 * Created by andremachado on 09/06/2017.
 */

public interface LocationManagerInterface {
    void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider);
}

