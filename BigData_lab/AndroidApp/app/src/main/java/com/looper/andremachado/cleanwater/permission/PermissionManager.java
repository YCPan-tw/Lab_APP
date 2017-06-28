package com.looper.andremachado.cleanwater.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static com.looper.andremachado.cleanwater.permission.Enums.REQ_CODE;

/**
 * Created by andremachado on 09/06/2017.
 */

public class PermissionManager implements RequestPermissionsResultInterface{

    public static Activity mActivity;
    public Context mContext;
    public String[] permissionList;
    public boolean flag;
    public static volatile boolean granted = false;
    int cont = 0;

    private static PermissionManagerInterface mManagerInterface;

    public PermissionManager(Activity mActivity, Context mContext) {
        this.mActivity = mActivity;
        this.mContext = mContext;
    }

    String[] myList = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_SETTINGS, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    public void requestPermission(String[] mList){
        permissionList = mList;
        ActivityCompat.requestPermissions(mActivity, mList, REQ_CODE);
    }

    public void requestPermission(){
        permissionList = myList;
        Log.d("TAG", "OnP1");
        ActivityCompat.requestPermissions(mActivity, myList, REQ_CODE);
        Log.d("TAG", "OnP2");
    }

    public void getManagerInterface(PermissionManagerInterface mInterface){
        mManagerInterface = mInterface;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d("TAG", "OnRP");

        if(requestCode == REQ_CODE){

            Log.d("TAG", "OnRQ");
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                String packageStrippedPermissionName =  permissions[i].substring(permissions[0].lastIndexOf(".")+1);
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = mActivity.shouldShowRequestPermissionRationale( permission );
                    if (! showRationale) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle(permission + " Required");
                        builder.setMessage("You have to grant this permission , otherwise app will not be able to continue, Click Yes to go to Settings else app will not resume!");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                                intent.setData(uri);
                                mActivity.startActivityForResult(intent, REQ_CODE);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(mContext, "App will not Start without this Permission", Toast.LENGTH_SHORT).show();
                                mActivity.finish();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    }else{
                        mManagerInterface.onPermissionDenied("\'" + packageStrippedPermissionName + "': permission denied", requestCode);
                        Alert(mActivity, permissions[i]);
                    }
                }else if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    mManagerInterface.onPermissionGranted("\'" + packageStrippedPermissionName + "\': permission granted", requestCode);
                }
            }
            ArrayList<String> list = new ArrayList<>();
            Log.d("TAG",  "Size: "+ grantResults.length);
            for(int k=0; k<grantResults.length; k++){
                Log.d("TAG",  "Elems: "+ grantResults[k]);
                if(grantResults[k] == PackageManager.PERMISSION_GRANTED){
                    list.add(permissions[k]);
                }
            }
            if(list.size() == permissions.length){
                flag = true;
                mManagerInterface.isAllGranted(flag);
                granted = true;
            }else{
                flag = false;
                mManagerInterface.isAllGranted(flag);
            }
        }

    }

    public void Alert(Activity activity, String permissions){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Permission Denied");
        for(int i=0; i<permissionList.length; i++){
            if(permissions.equals(permissionList[i])) {
                builder.setMessage(permissions + " is Neccesarry");
                break;
            }
        }

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                intent.setData(uri);
                mActivity.startActivityForResult(intent, REQ_CODE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mActivity.finish();
            }
        });
        builder.show();
    }
}
