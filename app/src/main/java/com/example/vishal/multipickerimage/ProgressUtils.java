package com.example.vishal.multipickerimage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;


/**
 * CommonUtils class
 * <p/>
 * <p>
 * This is progress dialog utils class which manage show/hide progress dialog
 * </p>
 *
 * @author Sumeet Bhut
 * @version 1.0
 * @since 2016-11-30
 */
public class ProgressUtils {

    private ProgressDialog progressDialog;
    private int processCount;
    private Context context;
    private ProgressDialog mProgressDialog;

    public ProgressUtils(Context context) {
        this.context = context;
    }

    public void showProgressDialog(String message) {
        showProgressDialog(message, 1);
    }

    /***
     * Show progress dialog
     * @param message Message
     * @param processCount Count of total processes.
     *                     Like if you want to do 2 tasks at a time then just call this showProgressDialog with processCount=2 and then call hideProgressDialog() method at every task finish
     */
    public void showProgressDialog(String message, int processCount) {
        this.processCount = processCount;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(true);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = new ProgressBar(context).getIndeterminateDrawable().mutate();
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark),
                        PorterDuff.Mode.SRC_IN);
                progressDialog.setIndeterminateDrawable(drawable);
            }

        }
        progressDialog.setMessage(message);

        if (context != null && !((Activity) context).isFinishing()) {
            progressDialog.show();
        }
    }

    /***
     * Update progress dialog message
     * @param message Message
     */
    public void setMessage(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
        }
    }

    /***
     * If you forcefully want dismiss progress dialog
     * @param isForce
     */
    public void dismissProgressDialog(boolean isForce) {
        if (isForce) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {

            }
        } else {
            hideProgressDialog();
        }
    }

    /***
     * For dismiss progress dialog
     */
    public void dismissProgressDialog() {
        if (processCount > 1) {
            processCount--;
        } else {
            try {
                if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

            } catch (Exception e) {

            }
        }
    }


    public void showProgressDialog() {
        hideProgressDialog();
        mProgressDialog = showLoadingDialog(context);
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
