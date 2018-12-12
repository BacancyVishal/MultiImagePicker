package com.example.vishal.multipickerimage;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.example.vishal.multipickerimage.permission.MultiplePermissionCallback;
import com.example.vishal.multipickerimage.permission.Permission;
import com.example.vishal.multipickerimage.permission.PermissionActivity;
import com.example.vishal.multipickerimage.permission.PermissionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sumeet on 5/10/17.
 */

public class BaseActivity extends AppCompatActivity {

    private static String displayName = "";
    public ImageSelectUtils imageSelectUtils;
    public static PermissionActivity permissionActivity;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private boolean internetConnected = true;
    private boolean isMultiplePermissionRequested = false;
    private MultiplePermissionCallback multiplePermissionCallback;
    private List<String> permissionsToRequest = new ArrayList<>();
    static String channelId = "channel-01";
    static String channelName = "Channel Name";
    static int importance = NotificationManager.IMPORTANCE_HIGH;

    private static final int PERMISSION_REQUEST_CODE = 10 << 1;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageSelectUtils = new ImageSelectUtils(BaseActivity.this);
        permissionActivity = new PermissionActivity(BaseActivity.this);

    }


    public void requestPermissions(Permission[] permissions, MultiplePermissionCallback multiplePermissionCallback) {
        if (PermissionUtils.isMarshmallowOrHigher()) {
            isMultiplePermissionRequested = true;
            this.multiplePermissionCallback = multiplePermissionCallback;

            for (Permission permission : permissions) {
                if (!PermissionUtils.isGranted(this, permission)) {
                    permissionsToRequest.add(permission.toString());
                }
            }

            if (!permissionsToRequest.isEmpty()) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                        PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionActivity.onPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        private boolean firstConnect = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            // internet lost alert dialog method call from here...


            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            String status = getConnectivityStatusString(context);
            if (activeNetInfo == null) {
                if (firstConnect) {
                    // do subroutines here
                    //AlertUtils.showSimpleAlert(BaseActivity.this, "Internet stop working");
                    firstConnect = false;
                }
            } else {
                //AlertUtils.showSimpleAlert(BaseActivity.this, "Internet start working");
                firstConnect = true;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public void showLog(String msg) {

        if (BuildConfig.DEBUG) Log.e("showLog", msg);
    }


    public void showProgressDialog() {
        hideProgressDialog();
        mProgressDialog = showLoadingDialog(BaseActivity.this);
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private static ProgressDialog showLoadingDialog(Context context) {
        if (context != null) {
            ProgressDialog progressDialog = new ProgressDialog(context);
            if (!((Activity) context).isFinishing()) {
                //show dialog
                progressDialog.show();
            }

            if (progressDialog.getWindow() != null) {
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            return progressDialog;
        }
        return null;
    }
}
