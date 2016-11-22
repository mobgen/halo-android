package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Tag that collects the screen size of the current device.
 */
public class ScreenSizeCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return HaloSegmentationTag.createDeviceTag("Screen Size", width + "x" + height);
    }
}
