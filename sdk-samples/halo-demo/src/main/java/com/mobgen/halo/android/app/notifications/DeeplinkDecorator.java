package com.mobgen.halo.android.app.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.mobgen.halo.android.app.ui.generalcontent.GeneralContentItemActivity;
import com.mobgen.halo.android.app.ui.news.ArticleActivity;
import com.mobgen.halo.android.notifications.decorator.HaloNotificationDecorator;

/**
 * Manages the deep linking if we recognize the module
 */
public class DeeplinkDecorator extends HaloNotificationDecorator {

    private static final String NEWS_ID_INT = "560539b8e81e3b0100ef6cbe";
    private static final String NEWS_ID_STAGE = "56161a166947b516009db5b8";

    private static final String STORE_LOCATOR_INT = "";
    private static final String STORE_LOCATOR_STAGE = "";

    /**
     * The context.
     */
    private Context mContext;

    public DeeplinkDecorator(Context context) {
        mContext = context;
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        Bundle extras = bundle.getBundle("extra");
        if (extras != null) {
            String moduleId = extras.getString("moduleId");
            if (moduleId != null) {
                PendingIntent pendingIntent = null;
                //In case of the news item
                if (moduleId.equals(NEWS_ID_INT) || moduleId.equals(NEWS_ID_STAGE)) {
                    pendingIntent = ArticleActivity.getDeeplink(mContext, extras, moduleId);
                } else if (!moduleId.equals(STORE_LOCATOR_INT) && !moduleId.equals(STORE_LOCATOR_STAGE)) { // It is not the station locator
                    pendingIntent = GeneralContentItemActivity.getDeeplink(mContext, extras, moduleId);
                }

                if (pendingIntent != null) {
                    builder.setContentIntent(pendingIntent);
                }
            }
        }
        return chain(builder, bundle);
    }
}
