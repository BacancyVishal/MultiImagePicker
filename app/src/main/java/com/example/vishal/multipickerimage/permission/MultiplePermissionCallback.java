package com.example.vishal.multipickerimage.permission;

import java.util.List;

/**
 * Created by Maitry on 12.05.2017.
 */

public interface MultiplePermissionCallback {

    void onPermissionGranted(boolean allPermissionsGranted, List<Permission> grantedPermissions);

    void onPermissionDenied(List<Permission> deniedPermissions, List<Permission> foreverDeniedPermissions);
}
