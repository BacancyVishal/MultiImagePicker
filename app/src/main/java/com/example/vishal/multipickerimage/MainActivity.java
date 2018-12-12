package com.example.vishal.multipickerimage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.vishal.multipickerimage.permission.MultiplePermissionCallback;
import com.example.vishal.multipickerimage.permission.Permission;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends BaseActivity {

    private ImageSelectUtils imageSelectUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageSelectUtils = new ImageSelectUtils(this);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPic();
            }
        });


       // String tiemzo = formatted_date("");
        //Log.e("time", tiemzo);
    }

    public static String formatted_date(String timestamp) {

        timestamp = "2013-11-14 13:00";

        SimpleDateFormat sdfformate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        DateFormat sdfoutput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()); //dd MMM yyyy KK:mma
        sdfoutput.setTimeZone(tz);//set time zone.
        String localTime = "";
        try {
            Date date = sdfformate.parse(timestamp);
            localTime = sdfoutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return localTime.toLowerCase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageSelectUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadPic() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            Permission[] permissions = {Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, new MultiplePermissionCallback() {
                    @Override
                    public void onPermissionGranted(boolean allPermissionsGranted, List<Permission> grantedPermissions) {


                        imageSelectUtils.selectImageNew(new ImageSelectUtils.SelectedImage() {
                            @Override
                            public void imagePath(String path) {

                                Log.e("imagePath", path);
                            }

                            @Override
                            public void imageSelectionFailure() {
                            }
                        },MainActivity.this);

                    }

                    @Override
                    public void onPermissionDenied(List<Permission> deniedPermissions, List<Permission> foreverDeniedPermissions) {

                    }
                });
            }
        } else {


            imageSelectUtils.selectImageNew(new ImageSelectUtils.SelectedImage() {
                @Override
                public void imagePath(String path) {
                    Log.e("imagePath", path);
                }

                @Override
                public void imageSelectionFailure() {
                }
            }, MainActivity.this);


        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "asdasdasd====");
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop", "asdasdasd====");

    }

    @Override
    protected void onDestroy() {
        Log.e("onDestroy", "asdasdasd====");
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
