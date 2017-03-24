package com.mobgen.halo.android.app.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;

import java.io.IOException;

/**
 * QReader Singleton.
 */
@Keep
public class QReader {
    /**
     * The constant FRONT_CAMERA.
     */
    public static final int FRONT_CAMERA = CameraSource.CAMERA_FACING_FRONT;
    /**
     * The constant BACK_CAMERA.
     */
    public static final int BACK_CAMERA = CameraSource.CAMERA_FACING_BACK;
    /**
     * The camera source
     */
    private CameraSource mCameraSource = null;
    /**
     * The barcode detector
     */
    private BarcodeDetector mBarcodeDetector = null;
    /**
     * The surface mWidth
     */
    private int mWidth;
    /**
     * The surfeace mHeight
     */
    private int mHeight;
    /**
     * The camera type
     */
    private int mCameraType;
    /**
     * The context
     */
    private Context mContext;
    /**
     * The scan listener
     */
    private QRScanListener mQRScanListener;
    /**
     * The surface view
     */
    private SurfaceView mSurfaceView;
    /**
     * Autofocuse enable
     */
    private boolean mAutoFocus;
    /**
     * Camera run state
     */
    private boolean mCameraRunning = false;
    /**
     * SurfaceView creation state
     */
    private boolean mSurfaceCreated = false;

    /**
     * Surfaceview holder callback
     */
    private final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            //we can start barcode after after creating
            mSurfaceCreated = true;
            startCameraView(mContext, mCameraSource, mSurfaceView);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mSurfaceCreated = false;
            releaseCamera();
            surfaceHolder.removeCallback(this);
        }
    };

    /**
     * Instantiates the QReader from builder
     *
     * @param builder The builder
     */
    private QReader(final Builder builder) {
        this.mContext = builder.mContext;
        this.mAutoFocus = builder.mAutofocus;
        this.mWidth = builder.mWidth;
        this.mHeight = builder.mHeight;
        this.mCameraType = builder.mCamera;
        this.mQRScanListener = builder.mQRScanListener;
        this.mSurfaceView = builder.mSurfaceView;
        this.mBarcodeDetector = builder.mBarcodeDetector;
    }

    /**
     * Init the barcode detector and the surfaceview with the camera
     *
     * @param surfaceView
     */
    public void startDetector(final SurfaceView surfaceView) {

        surfaceView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        startBarcodeDetector();
                        startSurfaceView();
                        removeOnGlobalLayoutListener(surfaceView, this);
                    }
                });
    }

    /**
     *
     * @param v
     * @param listener
     */
    private static void removeOnGlobalLayoutListener(View v,
                                                     ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
        else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    /**
     *  Start the barcode detector
     *
     */
    private void startBarcodeDetector() {
        if (mBarcodeDetector.isOperational()) {
            mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() != 0 && mQRScanListener != null) {
                        mQRScanListener.onDetected(barcodes.valueAt(0).displayValue);
                    }
                }
            });

            mCameraSource = new CameraSource.Builder(mContext, mBarcodeDetector).setAutoFocusEnabled(mAutoFocus)
                    .setFacing(mCameraType)
                    .setRequestedPreviewSize(mWidth, mHeight)
                    .build();
        }
    }

    /**
     * Start scanning qr codes.
     */
    private void startSurfaceView() {
        if (mSurfaceView != null && mSurfaceHolderCallback != null) {
            //if surface already created, we can start camera
            if (mSurfaceCreated) {
                startCameraView(mContext, mCameraSource, mSurfaceView);
            }
            else {
                //startCameraView will be invoke in void mSurfaceCreated
                mSurfaceView.getHolder().addCallback(mSurfaceHolderCallback);
            }
        }
    }

    /**
     * Start the camera view to scan codes
     *
     * @param context The context
     * @param cameraSource The camera source
     * @param surfaceView The surfaceview
     */
    private void startCameraView(Context context, CameraSource cameraSource,
                                 SurfaceView surfaceView) {
        if (mCameraRunning) {
            mQRScanListener.onScanError("Camera already started!");
            throw new IllegalStateException("Camera already started!");
        }
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && (!mCameraRunning && cameraSource != null && surfaceView != null)) {
                cameraSource.start(surfaceView.getHolder());
                mCameraRunning = true;
            }
        } catch (IOException ie) {
        }
    }

    /**
     * Release camera and cleanup QReader.
     *
     */
    public void stopDetector() {
        releaseCamera();
        if (mCameraSource != null) {
            //stopDetector camera and barcode detector(will invoke inside) resources
            mCameraSource.release();
            mCameraSource = null;
        }
    }


    /**
     * Release camera.
     *
     */
    private void releaseCamera() {
        try {
            if (mCameraRunning && mCameraSource != null) {
                mCameraSource.stop();
                mCameraRunning = false;
            }
        } catch (Exception ie) {
        }
    }


    /**
     * The builder for the options item.
     */
    @Keep
    public static class Builder implements IBuilder<QReader> {
        /**
         * The autofocus
         */
        private boolean mAutofocus;
        /**
         * Surface mWidth
         */
        private int mWidth;
        /**
         * Surface mHeight
         */
        private int mHeight;
        /**
         * Camera type
         */
        private int mCamera;
        /**
         * qr scan listener
         */
        private final QRScanListener mQRScanListener;
        /**
         * The context
         */
        private final Context mContext;
        /**
         * The surface view
         */
        private final SurfaceView mSurfaceView;
        /**
         * The barcode detector.
         */
        private BarcodeDetector mBarcodeDetector;

        /**
         * Instantiates a new Builder.
         *
         * @param context The mContext
         * @param surfaceView The surface view
         * @param qrScanListener The qr data listener
         */
        public Builder(Context context, SurfaceView surfaceView, QRScanListener qrScanListener) {
            this.mQRScanListener = qrScanListener;
            this.mContext = context;
            this.mSurfaceView = surfaceView;
        }

        /**
         * Enable autofocus to scan.
         *
         * @param autofocusEnabled The autofocus enabled
         * @return The builder
         */
        public Builder withAutoFocus(boolean autofocusEnabled) {
            this.mAutofocus = autofocusEnabled;
            return this;
        }

        /**
         * Width of surface to scan.
         *
         * @param width The Width
         * @return The builder
         */
        public Builder withWidth(int width) {
            this.mWidth = width;
            return this;
        }

        /**
         * Height of surface to scan.
         *
         * @param height The mHeight
         * @return The builder
         */
        public Builder withHeight(int height) {
            this.mHeight = height;
            return this;
        }

        /**
         * Select the camera to scan.
         *
         * @param facing The camera
         * @return the builder
         */
        public Builder withCamera(int facing) {
            this.mCamera = facing;
            return this;
        }

        /**
         * Barcode detector.
         *
         * @param barcodeDetector The barcode detector
         */
        public Builder withBarcodeDetector(BarcodeDetector barcodeDetector) {
            this.mBarcodeDetector = barcodeDetector;
            return this;
        }

        /**
         * Build QREader
         *
         * @return The QREader
         */
        public QReader build() {
            return new QReader(this);
        }
    }

    /**
     * The interface Qr data listener with scan result.
     */
    public interface QRScanListener {

        /**
         * On detected a barcode.
         *
         * @param data The data scanned
         */
        void onDetected(final String data);

        /**
         * On error
         *
         * @param errorMsg Error
         */
        void onScanError(final String errorMsg);
    }
}

