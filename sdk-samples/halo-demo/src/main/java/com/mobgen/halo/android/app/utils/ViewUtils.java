package com.mobgen.halo.android.app.utils;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Some useful functions for the mock application.
 */
public class ViewUtils {

    /**
     * Small helper to avoid the refreshing stuff with post problem.
     *
     * @param layout     The layout view.
     * @param refreshing True if it should be refreshing, false otherwise.
     */
    public static void refreshing(final SwipeRefreshLayout layout, final boolean refreshing) {
        layout.post(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(refreshing);
            }
        });
    }
}
