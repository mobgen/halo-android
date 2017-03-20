package com.mobgen.halo.android.app.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

/**
 * Scan qr activity
 */
public class QRScanActivity extends MobgenHaloActivity implements QRDataListener{
    /**
     * The scan result
     */
    public static final String BUNDLE_SCAN_RESULT = "bundle_scan_result";
    /**
     * The surface view to show the camera
     */
    private SurfaceView mySurfaceView;
    /**
     * The qr reader.
     */
    private QREader qrEader;

    /**
     * Starts the activity.
     *
     * @param activity The activity to start this activity for result
     */
    public static void startActivityForResult(@NonNull Activity activity, int resultCode) {
        Intent intent = new Intent(activity, QRScanActivity.class);
        activity.startActivityForResult(intent,resultCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_qr);
        getSupportActionBar().hide();

        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);
        qrEader = new QREader.Builder(this, mySurfaceView, this)
                .facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrEader.initAndStart(mySurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrEader.releaseAndCleanup();
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }


    @Override
    public void onDetected(String scanResult) {
        Bundle data = new Bundle();
        data.putString(BUNDLE_SCAN_RESULT, scanResult);
        Intent intent = new Intent();
        intent.putExtras(data);
        setResult(RESULT_OK,intent);
        finish();
    }
}
