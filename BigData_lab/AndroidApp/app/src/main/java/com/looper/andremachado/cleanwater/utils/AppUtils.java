package com.looper.andremachado.cleanwater.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by andremachado on 09/06/2017.
 */

public class AppUtils {

    public static String baseUrl = "http://ec2-34-225-145-95.compute-1.amazonaws.com:8000/";


    public static boolean isInternetAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
