package com.mobgen.halo.android.notifications.decorator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.ReflectionUtils;
import com.mobgen.halo.android.notifications.R;
import com.mobgen.halo.android.notifications.models.PushImage;
import com.mobgen.halo.android.sdk.api.Halo;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * @hide Decorator that adds the possibility to process the click_action element from the notification
 * to open an activity when the notification is clicked.
 */
public class NotificationImageDecorator extends HaloNotificationDecorator {

    /**
     * The maximun size of the image to show on the notification aprox. 1M
     * Fit well to a 1280x720 resolution. The maximun resolution supported is 2048x1024.
     */
    private static final int IMAGE_MAX_SIZE = 1000000;

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
     * Class of the glide library.
     */
    private Class<?> glide;

    /**
     * Class of the picasso library.
     */
    private Class<?> picasso;

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
        //only use libraries out of main thread
        if(Looper.getMainLooper() != Looper.myLooper()){
            glide = ReflectionUtils.toClass("com.bumptech.glide.Glide");
            picasso = ReflectionUtils.toClass("com.squareup.picasso.Picasso");
        }
    }


    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        if (bundle.get(IMAGE_KEY) != null) {
            try {
                PushImage pushImage = PushImage.deserialize(bundle.get(IMAGE_KEY).toString(), Halo.instance().framework().parser());
                Bitmap bitmapNotification = getImageBitmap(pushImage.getUrl());
                if (bitmapNotification != null) {
                    String title = bundle.getString("title");
                    String message = bundle.getString("body");
                    RemoteViews remoteView, remoteViewExpanded;
                    switch (pushImage.getLayout()) {
                        case PushImage.DEFAULT:
                            builder.setLargeIcon(bitmapNotification);
                            break;
                        case PushImage.EXPANDED:
                            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmapNotification));
                            break;
                        case PushImage.LEFT:
                            remoteView = getRemoteView(pushImage.getLayout(), bitmapNotification, title, message);
                            builder.setCustomContentView(remoteView);
                            break;
                        case PushImage.RIGHT:
                            remoteView = getRemoteView(pushImage.getLayout(), bitmapNotification, title, message);
                            builder.setCustomContentView(remoteView);
                            break;
                        case PushImage.TOP:
                            remoteView = getRemoteView(PushImage.LEFT, bitmapNotification, title, message);
                            builder.setCustomContentView(remoteView);
                            remoteViewExpanded = getRemoteView(pushImage.getLayout(), bitmapNotification, title, message);
                            builder.setCustomBigContentView(remoteViewExpanded);
                            break;
                        case PushImage.BOTTOM:
                            remoteView = getRemoteView(PushImage.RIGHT, bitmapNotification, title, message);
                            builder.setCustomContentView(remoteView);
                            remoteViewExpanded = getRemoteView(pushImage.getLayout(), bitmapNotification, title, message);
                            builder.setCustomBigContentView(remoteViewExpanded);
                            break;
                        case PushImage.BACKGROUND:
                            remoteView = getRemoteView(pushImage.getLayout(), bitmapNotification, title, message);
                            builder.setCustomContentView(remoteView);
                            builder.setCustomBigContentView(remoteView);
                            break;
                    }
                }

            } catch (HaloParsingException e) {
                Halog.v(NotificationImageDecorator.class, "Cannot parse the content");
            }
        }

        return chain(builder, bundle);
    }

    /**
     * Get downscale to apply to the bitmap.
     *
     * @param originalWidth  The initial width of the bitmap.
     * @param originalHeight The initial height of the bitmap.
     * @return The scale to downscale the bitmap. Otherwise -1.
     */
    private int getScale(int originalWidth, int originalHeight) {
        int scale = 1;
        if (originalWidth > 0 && originalHeight > 0) {
            while ((originalWidth * originalHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
        } else {
            scale = -1;
        }
        return scale;
    }

    /**
     * Get the bitmap of the image url.
     *
     * @param pushImageUrl The url of the image to load.
     * @return The bitmap of the image provided. Otherwise null.
     */
    @Nullable
    private Bitmap getImageBitmap(@NonNull String pushImageUrl) {
        if (glide != null) {
            try {
                Bitmap glideBitmap = Glide.with(mContext)
                        .load(pushImageUrl)
                        .asBitmap()
                        .into(-1, -1)
                        .get();
                int scale = getScale(glideBitmap.getWidth(), glideBitmap.getHeight());
                if (scale != -1) {
                    glideBitmap = Bitmap.createScaledBitmap(glideBitmap, glideBitmap.getWidth() / scale, glideBitmap.getHeight() / scale, true);
                    return glideBitmap;
                }
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        } else if (picasso != null) {
            try {
                Bitmap picassoBitmap = Picasso.with(mContext)
                        .load(pushImageUrl)
                        .get();
                int scale = getScale(picassoBitmap.getWidth(), picassoBitmap.getHeight());
                if (scale != -1) {
                    picassoBitmap = Bitmap.createScaledBitmap(picassoBitmap, picassoBitmap.getWidth() / scale, picassoBitmap.getHeight() / scale, true);
                    return picassoBitmap;
                }
            } catch (IOException e) {
                return null;
            }
        } else {
            return downloadScaledBitmapFromURL(pushImageUrl);
        }
        return null;
    }

    /**
     * Get image from url and convert this to bitmap. Downscale the bitmap if its bigger than maximun size.
     *
     * @param url The url to fetch.
     * @return The bitmap.
     */
    @Nullable
    private Bitmap downloadScaledBitmapFromURL(String url) {
        try {
            int inWidth = 0;
            int inHeight = 0;

            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //get the metadata of the image
            BitmapFactory.decodeStream(input, null, options);
            inWidth = options.outWidth;
            inHeight = options.outHeight;
            input.close();
            int scale = getScale(inWidth, inHeight);
            if (scale == -1) {
                //we cannot downscale the bitmap
                return null;
            }
            //download the image and downscale
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            connection = (HttpURLConnection) urlConnection.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input, null, options);
            input.close();
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
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
    private RemoteViews getRemoteView(@PushImage.Layout String type, @NonNull Bitmap image,
                                      @Nullable String title, @Nullable String message) {
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
     * Sets the intent for testing purposes.
     *
     * @param intent The intent.
     */
    @VisibleForTesting
    public void setIntent(Intent intent) {
        mActionIntent = intent;
    }
}
