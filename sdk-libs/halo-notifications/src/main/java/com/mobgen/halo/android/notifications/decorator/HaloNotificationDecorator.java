package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Decorator patter for notifications. This decorator allows the user to add behaviour to the current notifications system. It is intended
 * to create a class that extends {@link HaloNotificationDecorator HaloNotificationDecorator} an implements the method
 * {@link #decorate(NotificationCompat.Builder, Bundle) decorate}. Inside this method you should call
 * {@link #chain(NotificationCompat.Builder, Bundle) chain} so the whole pattern works as expected. Here it is an example:
 * <pre><code>
 * public NotificationCompat.Builder decorate (@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
 *      //Do some operation on the builder
 *      String title = bundle.getString("title");
 *      if(title != null) {
 *          builder.setContentTitle(title);
 *      }
 *      //Call chain to chain the next builder and return the result
 *      return chain(builder, bundle);
 * }
 * </code></pre>
 */
@Keep
public abstract class HaloNotificationDecorator {

    /**
     * The next chained decorator.
     */
    private HaloNotificationDecorator mDecorator;

    /**
     * The empty constructor to build the decorator without a chained decorator.
     */
    public HaloNotificationDecorator() {
        this(null);
    }

    /**
     * The constructor with a chained decorator.
     *
     * @param decorator The chain decorator.
     */
    @Keep
    @Api(2.0)
    public HaloNotificationDecorator(HaloNotificationDecorator decorator) {
        mDecorator = decorator;
    }

    /**
     * Chains the current decorator with the embedded one so all of them can decorate the builder.
     *
     * @param builder The notification builder.
     * @param bundle  The bundle in which all the data is provided.
     * @return The notification builder or null if the notification should not be shown.
     */
    @Keep
    @Api(2.0)
    protected final NotificationCompat.Builder chain(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        if (mDecorator != null) {
            return mDecorator.decorate(builder, bundle);
        } else {
            return builder;
        }
    }

    /**
     * Decorates the notification given the bundle.
     *
     * @param builder The Android notification builder.
     * @param bundle  The current bundle received as a message.
     * @return The builder.
     */
    @Keep
    @Api(2.0)
    public abstract NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle);
}
