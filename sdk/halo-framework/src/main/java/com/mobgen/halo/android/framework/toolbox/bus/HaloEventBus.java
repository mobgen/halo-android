package com.mobgen.halo.android.framework.toolbox.bus;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * Event hub for halo where some events can be emitted and you can also subscribe to those events.
 */
public class HaloEventBus implements EventBus {

    /**
     * The context.
     */
    private Context mContext;

    /**
     * Private constructor to use the create.
     *
     * @param context The context to add and remove receivers.
     */
    private HaloEventBus(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Creates an event hub.
     *
     * @param context The context to create the event bus.
     * @return The event hub to be created
     */
    @Api(1.3)
    public static HaloEventBus create(@NonNull Context context) {
        AssertionUtils.notNull(context, "context");
        return new HaloEventBus(context);
    }

    @NonNull
    @Override
    @CheckResult(suggest = "Subscription.unsubscribe() to avoid memory leaks")
    public Subscription subscribe(@NonNull final Subscriber eventSubscriber, @NonNull final EventId eventId) {
        //Listen for the broadcast
        String identifier = HaloUtils.getEventName(mContext, eventId.getId());
        IntentFilter filter = new IntentFilter(identifier);
        SubscriberAdapter adapter = new SubscriberAdapter(eventSubscriber, eventId);
        mContext.registerReceiver(adapter, filter);
        return new Subscription(this, adapter, eventId);
    }

    @Override
    public void unsubscribe(@NonNull SubscriberAdapter eventSubscriber, @NonNull EventId eventId) {
        mContext.unregisterReceiver(eventSubscriber);
    }

    @Override
    public void emit(@NonNull Event event) {
        //Create the intent
        String identifier = HaloUtils.getEventName(mContext, event.getEventId().getId());
        Intent intent = new Intent(identifier);
        intent.putExtras(event.getData());
        mContext.sendBroadcast(intent);
    }
}
