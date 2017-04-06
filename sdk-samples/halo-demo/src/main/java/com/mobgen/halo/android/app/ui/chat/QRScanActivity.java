package com.mobgen.halo.android.app.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.utils.QReader;


/**
 * Scan qr activity
 */
public class QRScanActivity extends MobgenHaloActivity implements QReader.QRScanListener {
    /**
     * The scan result
     */
    public static final String BUNDLE_SCAN_RESULT = "bundle_scan_result";
    /**
     * The surface view to show the camera
     */
    private SurfaceView mSurfaceView;
    /**
     * The qr reader.
     */
    private QReader mQReader;

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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        mSurfaceView = (SurfaceView) findViewById(R.id.camera_view);
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        mQReader = new QReader.Builder(this, mSurfaceView, this)
                .withBarcodeDetector(barcodeDetector)
                .withAutoFocus(true)
                .withCamera(QReader.BACK_CAMERA)
                .withAutoFocus(true)
                .withHeight(size.y)
                .withWidth(size.x)
                .build();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mQReader.startDetector(mSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQReader.stopDetector();
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

    @Override
    public void onScanError(String errorMsg) {
        Bundle data = new Bundle();
        data.putString(BUNDLE_SCAN_RESULT, errorMsg);
        Intent intent = new Intent();
        intent.putExtras(data);
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}
