package com.example.vishal.multipickerimage.permission;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maitry on 12.05.2017.
 */

public class PermissionFragment {

    private static final int PERMISSION_REQUEST_CODE = 10 << 1;

    private List<String> permissionsToRequest = new ArrayList<>();
    private List<Permission> grantedPermissions = new ArrayList<>();
    private List<Permission> deniedPermissions = new ArrayList<>();
    private List<Permission> foreverDeniedPermissions = new ArrayList<>();
    private MultiplePermissionCallback multiplePermissionCallback;
    private SinglePermissionCallback singlePermissionCallback;
    private boolean isMultiplePermissionRequested = false;
    AppCompatActivity activity;
    public  PermissionFragment(AppCompatActivity activity){
        this.activity = activity;
    }

    public void requestPermissions(Permission[] permissions, MultiplePermissionCallback multiplePermissionCallback) {
        if (PermissionUtils.isMarshmallowOrHigher()) {
            isMultiplePermissionRequested = true;
            this.multiplePermissionCallback = multiplePermissionCallback;

            for (Permission permission : permissions) {
                if (!PermissionUtils.isGranted(activity, permission)) {
                    permissionsToRequest.add(permission.toString());
                }
            }

            if (!permissionsToRequest.isEmpty()) {
                activity.requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    public void requestPermission(Permission permission, SinglePermissionCallback singlePermissionCallback) {
        if (PermissionUtils.isMarshmallowOrHigher()) {
            isMultiplePermissionRequested = false;
            this.singlePermissionCallback = singlePermissionCallback;
            permissionsToRequest.add(permission.toString());
            if (!permissionsToRequest.isEmpty()) {
                activity.requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // TODO: 02.12.2016 maybe in future we would need this
//            if (isMultiplePermissionRequested) {
//                multiplePermissionCallback.onPermissionGranted(true, new ArrayList<>());
//            } else {
//                singlePermissionCallback.onPermissionResult(permission, true, false);
//            }
        }
    }



    public void onPermissionsResult(int requestCode, String[] permissions,
                                     int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            grantedPermissions.clear();
            deniedPermissions.clear();
            foreverDeniedPermissions.clear();

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (permissionsToRequest.contains(permissions[i])) {
                        grantedPermissions.add(Permission.stringToPermission(permissions[i]));
                    }
                } else {
                    boolean permissionsDeniedForever =
                            false;
                    if (PermissionUtils.isMarshmallowOrHigher()) {
                        permissionsDeniedForever = activity.shouldShowRequestPermissionRationale(permissions[i]);
                    }
                    if (permissionsToRequest.contains(permissions[i])) {
                        if (!permissionsDeniedForever) {
                            foreverDeniedPermissions.add(Permission.stringToPermission(permissions[i]));
                        }
                        deniedPermissions.add(Permission.stringToPermission(permissions[i]));
                    }
                }
            }

            boolean allPermissionsGranted = deniedPermissions.isEmpty();
            if (isMultiplePermissionRequested) {
                multiplePermissionCallback.onPermissionGranted(allPermissionsGranted, grantedPermissions);
                multiplePermissionCallback.onPermissionDenied(deniedPermissions, foreverDeniedPermissions);
            } else {
                boolean permissionsDeniedForever = false;
                if (PermissionUtils.isMarshmallowOrHigher()) {
                    permissionsDeniedForever = activity.shouldShowRequestPermissionRationale(
                            permissionsToRequest.get(0));
                }
                if (allPermissionsGranted)
                    permissionsDeniedForever = true;
                singlePermissionCallback.onPermissionResult(allPermissionsGranted, !permissionsDeniedForever);
            }
            permissionsToRequest.clear();
        }
    }
}