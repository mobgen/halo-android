package com.mobgen.halo.android.notifications.decorator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

/**
 * @hide Decorator that adds the icon to the notification. The notification element provided comes in the
 * data field as "icon" and iy should be the name of the icon in the application. This will be found
 * in resources/drawable or in resources/mimap.
 */
public class NotificationIconDecorator extends HaloNotificationDecorator {

    /**
     * The context of the service.
     */
    private Context mContext;

    /**
     * Constructor to show the icon on the notification.
     *
     * @param context   The context of the service.
     * @param decorator The chained decorator.
     */
    public NotificationIconDecorator(Context context, HaloNotificationDecorator decorator) {
        super(decorator);
        mContext = context;
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String iconName = bundle.getString("icon");
        if (TextUtils.isEmpty(iconName)) {
            iconName = "";
        }
        Resources resources = mContext.getResources();
        int drawableId = resources.getIdentifier(iconName, "drawable", mContext.getPackageName());
        if (drawableId == 0) {
            drawableId = resources.getIdentifier(iconName, "mipmap", mContext.getPackageName());
        }
        if (drawableId == 0) { // Get the default application icon
            try {
                drawableId = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA).icon;
            } catch (PackageManager.NameNotFoundException e) {
                Halog.e(getClass(), "Could not get the application icon.");
            }
        }
        builder.setSmallIcon(drawableId);
        return chain(builder, bundle);
    }
}
