package com.sblee.des.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by minsoo on 2017. 11. 5..
 */

public class Utils {

    public static final String ADDRESS = "http://ec2-18-221-211-205.us-east-2.compute.amazonaws.com:5000/";

    public static void createAlert(Context context, String msg){
        new AlertDialog.Builder(context).setMessage(msg).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static boolean isPermissionsGranted(Context context, String[] permissions){
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean shouldShowPermissionRationale(Activity activity, String[] permissions){
        for (String permission : permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
                return true;
            }
        }
        return false;
    }
}
