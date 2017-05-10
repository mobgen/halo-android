package com.mobgen.halo.android.notifications.decorator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.notifications.R;
import com.mobgen.halo.android.notifications.models.PushImage;
import com.mobgen.halo.android.sdk.api.Halo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @hide Decorator that adds the possibility to process the click_action element from the notification
 * to open an activity when the notification is clicked.
 */
public class NotificationImageDecorator extends HaloNotificationDecorator {

    /**
     * The image notification ket
     */
    private static final String IMAGE_KEY = "image";

    /**
     * The context used to create a pending intent.
     */
    private Context mContext;

    /**
     * The action intent.
     */
    private Intent mActionIntent;

    /**
     * Constructor for the action decorator.
     *
     * @param context   The context for the notification service.
     * @param decorator The notification decorator.
     */
    public NotificationImageDecorator(Context context, HaloNotificationDecorator decorator) {
        super(decorator);
        mContext = context;
        mActionIntent = new Intent();
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        if (bundle.get(IMAGE_KEY) != null) {
            try {
                PushImage pushImage = PushImage.deserialize(bundle.get(IMAGE_KEY).toString(), Halo.instance().framework().parser());
                Bitmap bitmap = null;
                bitmap = getBitmapFromURL(pushImage.getUrl());
                String title = bundle.getString("title");
                String message = bundle.getString("body");
                RemoteViews remoteView, remoteViewExpanded;
                switch (pushImage.getLayout()) {
                    case PushImage.DEFAULT:
                        builder.setLargeIcon(bitmap);
                        break;
                    case PushImage.EXPANDED:
                        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
                        break;
                    case PushImage.LEFT:
                        remoteView = getRemoteView(pushImage.getLayout(), bitmap, title, message);
                        builder.setCustomContentView(remoteView);
                        break;
                    case PushImage.RIGHT:
                        remoteView = getRemoteView(pushImage.getLayout(), bitmap, title, message);
                        builder.setCustomContentView(remoteView);
                        break;
                    case PushImage.TOP:
                        remoteView = getRemoteView(PushImage.LEFT, bitmap, title, message);
                        builder.setCustomContentView(remoteView);
                        remoteViewExpanded = getRemoteView(pushImage.getLayout(), bitmap, title, message);
                        builder.setCustomBigContentView(remoteViewExpanded);
                        break;
                    case PushImage.BOTTOM:
                        remoteView = getRemoteView(PushImage.RIGHT, bitmap, title, message);
                        builder.setCustomContentView(remoteView);
                        remoteViewExpanded = getRemoteView(pushImage.getLayout(), bitmap, title, message);
                        builder.setCustomBigContentView(remoteViewExpanded);
                        break;
                    case PushImage.BACKGROUND:
                        remoteView = getRemoteView(pushImage.getLayout(), bitmap, title, message);
                        builder.setCustomContentView(remoteView);
                        builder.setCustomBigContentView(remoteView);
                        break;
                }

            } catch (HaloParsingException e) {
                Halog.v(NotificationImageDecorator.class, "Cannot parse the content");
            }
        }

        return chain(builder, bundle);
    }

    /**
     * Get the remote view to use in the notification.
     *
     * @param type    The type of layout ro inflate.
     * @param image   The bitmap of the image to show.
     * @param title   The title of the notification.
     * @param message The message of the notification.
     * @return The remoteView to show in the notification.
     */
    @NonNull
    private RemoteViews getRemoteView(@PushImage.Layout String type, @NonNull Bitmap image, @Nullable String title, @Nullable String message) {
        RemoteViews remoteViews;
        switch (type) {
            case PushImage.LEFT:
                remoteViews = new RemoteViews(mContext.getPackageName(),
                        R.layout.push_image_left);
                remoteViews.setImageViewBitmap(R.id.im_notification_left, image);
                break;
            case PushImage.RIGHT:
                remoteViews = new RemoteViews(mContext.getPackageName(),
                        R.layout.push_image_right);
                remoteViews.setImageViewBitmap(R.id.im_notification_right, image);
                break;
            case PushImage.TOP:
                remoteViews = new RemoteViews(mContext.getPackageName(),
                        R.layout.push_image_top);
                remoteViews.setImageViewBitmap(R.id.im_notification_top, image);
                break;
            case PushImage.BOTTOM:
                remoteViews = new RemoteViews(mContext.getPackageName(),
                        R.layout.push_image_bottom);
                remoteViews.setImageViewBitmap(R.id.im_notification_bottom, image);
                break;
            case PushImage.BACKGROUND:
                remoteViews = new RemoteViews(mContext.getPackageName(),
                        R.layout.push_image_background);
                remoteViews.setImageViewBitmap(R.id.im_notification_background, image);
                break;
            default:
                remoteViews = new RemoteViews(mContext.getPackageName(),
                        R.layout.push_image_left);
                remoteViews.setImageViewBitmap(R.id.im_notification_left, image);
                break;
        }

        if (!TextUtils.isEmpty(title)) {
            remoteViews.setTextViewText(R.id.tv_title, title);
        }
        if (!TextUtils.isEmpty(message)) {
            remoteViews.setTextViewText(R.id.tv_text, message);
        }

        return remoteViews;
    }

    /**
     * Get image from url and convert this to bitmap.
     *
     * @param url The url to fetch.
     * @return The bitmap.
     */
    private Bitmap getBitmapFromURL(String url) {
        try {
            URL urlconnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlconnection.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the intent for testing purposes.
     *
     * @param intent The intent.
     */
    @VisibleForTesting
    public void setIntent(Intent intent) {
        mActionIntent = intent;
    }
}
