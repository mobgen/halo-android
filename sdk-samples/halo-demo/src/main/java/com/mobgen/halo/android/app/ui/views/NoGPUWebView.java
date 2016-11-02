package com.mobgen.halo.android.app.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by javierdepedrolopez on 9/28/15.
 */
public class NoGPUWebView extends WebView {
    public NoGPUWebView(Context context) {
        super(context);
    }

    public NoGPUWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoGPUWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
