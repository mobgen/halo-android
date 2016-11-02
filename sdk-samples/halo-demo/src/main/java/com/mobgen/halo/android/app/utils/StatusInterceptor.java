package com.mobgen.halo.android.app.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

/**
 * Helper class to set the messages for the status in the mock app screens.
 */
public class StatusInterceptor {

    public static void intercept(@Nullable HaloStatus status, @NonNull DataStatusRecyclerAdapter.StatusViewHolder viewHolder) {
        Context context = viewHolder.itemView.getContext();
        viewHolder.getStatusText().setText(getMessage(status, context));
    }

    public static void intercept(@Nullable HaloStatus status, @Nullable View view) {
        if (view != null) {
            TextView text = (TextView) view.findViewById(R.id.tv_data_status);
            text.setText(getMessage(status, view.getContext()));
            if (status != null && status.isFresh()) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    private static String getMessage(@Nullable HaloStatus status, @NonNull Context context) {
        if (status != null) {
            if (status.isLocal()) {
                return context.getString(R.string.status_message_offline);
            } else if (status.isError()) {
                return context.getString(R.string.status_message_error, status.getExceptionMessage());
            }
        }
        return null;
    }
}
