package com.example.vishal.multipickerimage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

/**
 * Created by Sumeet Bhut on 1/6/2016.
 */
public class ImageSelectUtils {
    private static File filename;
    private final int ACTION_REQUEST_CAMERA = 1001;
    private final int ACTION_REQUEST_CROP = 1002;
    private final int ACTION_REQUEST_GALLERY = 1003;

    private Uri mImageCaptureUri, mImageCropUri;

    private Activity mActivity;
    private OnImageSelectListener mListener;
    private SelectedImage selectedImagemListener;
    private String filePath;
    private Uri picUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int PICK_IMAGE_REQUEST = 1;
    private File actualImage;
    private File compress_image_path;
    public static final String IMAGE_DIRECTORY_NAME = ".CoinasonChat";
    private static String timeStamp = "";
    public static int rotation;
    private Context context;
    private ProgressDialog dialog;
    ProgressUtils progressUtils;

    public interface OnImageSelectListener {
        void onSelect(Bitmap bitmap, byte[] bytes, String filePath);

        void onError();
    }

    public interface SelectedImage {
        void imagePath(String path);

        void imageSelectionFailure();
    }

    public ImageSelectUtils(Activity activity) {
        mActivity = activity;
        progressUtils = new ProgressUtils(mActivity);
    }

    public void selectImage(OnImageSelectListener listener) {
        mListener = listener;
        showSelectOptionDialog();
    }


    public void selectImageNew(SelectedImage listener, MainActivity mainActivity) {
        this.context = mainActivity;
        selectedImagemListener = listener;
        showSelectOptionDialognew();


    }


    public void selectImageCamera(OnImageSelectListener listener) {
        mListener = listener;
        captureImageFromCamera();
    }

    public void selectImageGallery(OnImageSelectListener listener) {
        mListener = listener;
        selectImageFromGallery();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivity.startActivityForResult(Intent.createChooser(intent, "Complete action using"), ACTION_REQUEST_GALLERY);

    }

    private void captureImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp_" + String.valueOf(System.currentTimeMillis()) + ".png"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        try {
            intent.putExtra("return-data", true);

            mActivity.startActivityForResult(intent, ACTION_REQUEST_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onError();
            }
        }
    }


    // Image capture module
    protected void CaptureImage() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        File file = getOutputMediaFile();
        filePath = file.getAbsolutePath();
//        <!--nougat-->
        if (Build.VERSION.SDK_INT >= 24) {
            picUri = FileProvider.getUriForFile(mActivity,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file);
        } else {

            picUri = Uri.fromFile(file); // create
        }
        i.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the take_photo file

        mActivity.startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);


    }

    protected void PickImage() {


        Intent intent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        } else {

            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        // <!--nougat--add two permisssion>
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        //                <!--nougat--show image>
        intent.setType("image/*");
        mActivity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    private void showSelectOptionDialog() {

        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setIcon(0);
        builder.setTitle("Select Image");
        builder.setNegativeButton("Cancel", null);

        CharSequence[] items = {"Take from camera", "Select from gallery"};
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (which == 0) {
                    captureImageFromCamera();
                } else if (which == 1) {
                    selectImageFromGallery();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
//        AlertUtils.changeDefaultColor(dialog);

    }

    private void showSelectOptionDialognew() {

        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setIcon(0);
        builder.setTitle("Select Image");
        builder.setNegativeButton("Cancel", null);

        CharSequence[] items = {"Take from camera", "Select from gallery"};
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (which == 0) {
                    CaptureImage();
//                    captureImageFromCamera();

                } else if (which == 1) {
//                    selectImageFromGallery();
                    PickImage();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {

            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                try {
                    actualImage = FileUtil.from(mActivity, picUri);

                    compress_image_path = compressImage(actualImage.getAbsolutePath(), mActivity);

                    if (compress_image_path != null && compress_image_path.length() > 0) {

                        if (selectedImagemListener != null)
                            selectedImagemListener.imagePath(compress_image_path.getAbsolutePath());
                    } else {
                        if (selectedImagemListener != null)
                            selectedImagemListener.imageSelectionFailure();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    if (selectedImagemListener != null)
                        selectedImagemListener.imageSelectionFailure();
                }
                break;

            case PICK_IMAGE_REQUEST:


                if (data.getClipData() != null) {

                    final ClipData clipData = data.getClipData();


//                        getImages(clipData.getItemCount(), clipData);


                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();

                            progressUtils.showProgressDialog();
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {

                            for (int i = 0; i < clipData.getItemCount(); i++) {

                                ClipData.Item item = clipData.getItemAt(i);

                                try {
                                    actualImage = FileUtil.from(mActivity, item.getUri());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                compress_image_path = compressImage(actualImage.getAbsolutePath(), mActivity);

                                if (compress_image_path != null && compress_image_path.length() > 0) {
                                    if (selectedImagemListener != null)
                                        selectedImagemListener.imagePath(compress_image_path.getAbsolutePath());
                                    Log.e("ImagePath", compress_image_path.getAbsolutePath());
                                } else {
                                    if (selectedImagemListener != null)
                                        selectedImagemListener.imageSelectionFailure();
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            progressUtils.hideProgressDialog();

                        }
                    }.execute();


                } else {
                    Log.e("PICK_IMAGE_REQUEST", "getClipData null");

                    try {
                        actualImage = FileUtil.from(mActivity, data.getData());
                        compress_image_path = compressImage(actualImage.getAbsolutePath(), mActivity);

                        if (compress_image_path != null && compress_image_path.length() > 0) {

                            if (selectedImagemListener != null)
                                selectedImagemListener.imagePath(compress_image_path.getAbsolutePath());
                        } else {
                            if (selectedImagemListener != null)
                                selectedImagemListener.imageSelectionFailure();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        if (selectedImagemListener != null)
                            selectedImagemListener.imageSelectionFailure();
                    }


                }


                break;

        }

    }

    private void getImages(int itemCount, ClipData clipData) throws IOException {

        if (itemCount > 0) {

            actualImage = FileUtil.from(mActivity, clipData.getItemAt(itemCount - 1).getUri());
            compress_image_path = compressImage(actualImage.getAbsolutePath(), mActivity);

            if (compress_image_path != null && compress_image_path.length() > 0) {
                if (selectedImagemListener != null)
                    selectedImagemListener.imagePath(compress_image_path.getAbsolutePath());
                Log.e("ImagePath", compress_image_path.getAbsolutePath());
            } else {
                if (selectedImagemListener != null)
                    selectedImagemListener.imageSelectionFailure();
            }

            itemCount--;
            Log.e("getImages", "getImages");
            getImages(itemCount, clipData);

        }

    }


    public static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStorageDirectory().getAbsolutePath(),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG" + "_" + UUID.randomUUID().toString() + ".jpg");

        return mediaFile;
    }

    public static File compressImage(String filePath, Context context) {

        // String filePath = getRealPathFromURI(imageUri,context);
        try {
            Bitmap scaledBitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed take_photo is taken as 816x612
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the take_photo
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original take_photo
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its preview_image_path
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }

            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the take_photo and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);

                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 0) {
                    matrix.postRotate(0);
                } else if (orientation == 1) {
                    matrix.postRotate(0);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;


            filename = getOutputMediaFile();

            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;
        } catch (Exception e) {

            Toast.makeText(context, "Please choose other file some file are corrupted", Toast.LENGTH_SHORT).show();
            return filename;

        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

}
