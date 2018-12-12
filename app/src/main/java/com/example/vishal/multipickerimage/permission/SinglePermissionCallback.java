package com.example.vishal.multipickerimage.permission;

/**
 * Created by Maitry on 12.05.2017.
 */

public interface SinglePermissionCallback {

    void onPermissionResult(boolean permissionGranted, boolean isPermissionDeniedForever);
}
