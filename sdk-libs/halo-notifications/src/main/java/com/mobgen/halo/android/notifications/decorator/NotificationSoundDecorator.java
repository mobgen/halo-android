package com.mobgen.halo.android.notifications.decorator;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * @hide The sound notification decorator that is capable to reproduce a sound.
 */
public class NotificationSoundDecorator extends HaloNotificationDecorator {

    /**
     * The notification service context.
     */
    private Context mContext;

    /**
     * Constructor for the sound notification decorator.
     *
     * @param context   The context used to display the notification.
     * @param decorator The decorator.
     */
    public NotificationSoundDecorator(Context context, HaloNotificationDecorator decorator) {
        super(decorator);
        mContext = context;
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String sound = bundle.getString("sound");
        if (!TextUtils.isEmpty(sound)) {
            Uri soundUri = null;
            if ("default".equalsIgnoreCase(sound)) {
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            } else {
                int soundId = mContext.getResources().getIdentifier(sound, "raw", mContext.getPackageName());
                if (soundId != 0) {
                    soundUri = HaloUtils.getUriFromResource(mContext, soundId);
                }
            }
            if (soundUri != null) {
                builder.setSound(soundUri);
            }
        }
        return chain(builder, bundle);
    }
}
