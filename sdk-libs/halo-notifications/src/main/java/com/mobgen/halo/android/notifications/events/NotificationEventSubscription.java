package com.mobgen.halo.android.notifications.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationEventListener;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Created by f.souto.gonzalez on 22/01/2018.
 */

/**
 * Subscription to handle notification event actions.
 */
@Keep
public class NotificationEventSubscription extends BroadcastReceiver implements ISubscription {

    private static final String TAG = "NotificationEven";

    /**
     * The context.
     */
    @NonNull
    private Context mContext;

    /**
     * The halo notification event listener.
     */
    @Nullable
    private HaloNotificationEventListener mListener;

    /**
     * Constructor for the receiver.
     *
     * @param context      The context.
     * @param listener     The listener.
     * @param intentFilter The intent filter to listen.
     */
    public NotificationEventSubscription(@NonNull Context context, @Nullable HaloNotificationEventListener listener, IntentFilter intentFilter) {
        mContext = context;
        mListener = listener;
        mContext.registerReceiver(this, intentFilter);
    }

    @Override
    public void unsubscribe() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getExtras().getString("action");
        String scheduleId = intent.getExtras().getString("scheduleId");

        if(Halo.instance().manager().getDevice()!=null) {

            HaloPushEvent receiptPush = new HaloPushEvent.Builder(Halo.instance().manager().getDevice().getAlias())
                    .withAction(action)
                    .withSchedule(scheduleId)
                    .build();

            HaloNotificationsApi.with(Halo.instance())
                    .notifyPushEvent(receiptPush)
                    .threadPolicy(Threading.POOL_QUEUE_POLICY)
                    .execute(new CallbackV2<HaloPushEvent>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<HaloPushEvent> haloResultV2) {
                            if (haloResultV2.status().isOk()) {
                                if (mListener != null) {
                                    mListener.onEventReceived(haloResultV2.data());
                                }
                            }
                        }
                    });
        } else {
            if (mListener != null) {
                mListener.onEventReceived(null);
            }
        }
    }
}
