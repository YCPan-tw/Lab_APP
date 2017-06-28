package com.looper.andremachado.cleanwater.permission;

import java.io.Serializable;

/**
 * Created by andremachado on 09/06/2017.
 */

public interface PermissionManagerInterface extends Serializable
{
    String TAG = PermissionManagerInterface.class.getSimpleName();

    void onPermissionGranted(String message, int requestCode);

    void onPermissionDenied(String message, int requestCode);

    void isAllGranted(boolean flag);
}
