package com.mobgen.halo.android.notifications;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationEventListener;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationListener;
import com.mobgen.halo.android.notifications.decorator.HaloNotificationDecorator;
import com.mobgen.halo.android.notifications.events.NotificationEventInteractor;
import com.mobgen.halo.android.notifications.events.NotificationEventRemoteDatasource;
import com.mobgen.halo.android.notifications.events.NotificationEventRepository;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.notifications.services.NotificationIdGenerator;
import com.mobgen.halo.android.notifications.services.HaloNotificationIdGenerator;
import com.mobgen.halo.android.notifications.services.InstanceIDService;
import com.mobgen.halo.android.notifications.services.NotificationEmitter;
import com.mobgen.halo.android.notifications.services.NotificationService;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * The notifications API is a wrapper library that allows you
 * to receive and manage notifications inside the current
 * application. To use the notifications api you need a valid
 * instance of the HALO SDK with valid credentials.
 * Then you create a new instance of the api by calling:
 * <pre><code>
 * HaloNotificationsApi notificationsApi = HaloNotificationsApi.with(halo);
 * </code></pre>
 * <p>
 * Also remember to call <code>notificationsApi.release()</code> in the
 * onTerminate method of your application. This removes the listeners
 * attached in this API.
 * <p>
 * Then you can listen for the notifications and modify the notifications that
 * the sdk shows.
 * <p>
 * To listen for notifications we provide 3 ways of doing it:
 * <ol>
 * <li>Listen for normal notifications</li>
 * <li>Listen for silent notifications</li>
 * <li>Listen to all the notifications</li>
 * </ol>
 * <p>
 * As other APIs you can access all the methods through the
 * {@link HaloNotificationsApi}. Use the following snippets as
 * samples to listen for every notification type.
 * <pre><code>
 * notificationsApi.listenNotSilentNotifications(listener);
 * notificationsApi.listenSilentNotifications(listener);
 * notificationsApi.listenAllNotifications(listener);
 * </code></pre>
 * <p>
 * Moreover you can customize the notifications behaviour and
 * the way it shows off by creating a decorator. A decorator
 * is just a piece of code that provides you with the notification
 * already configured in a callback where you can change its behavior.
 * See the example above for details:
 * <pre><code>
 * new HaloNotificationDecorator(){
 *      public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
 *          return builder;
 *      }
 * };
 * </code></pre>
 * You can also return null in the decorator if you don't want to show
 * this notification for some reason.
 */
@Keep
public class HaloNotificationsApi extends HaloPluginApi {

    /**
     * Subscription added to stay in touch when the token is being
     * refreshed by firebase.
     */
    @NonNull
    private ISubscription mRefreshSubscription;

    /**
     * Subscription added to stay in touch when the sync device
     * finished
     */
    @NonNull
    private ISubscription mDeviceSyncSubscription;

    /**
     * Instance id of firebase. It provides the token when
     * the notification API is ready.
     */
    @NonNull
    private FirebaseInstanceId mFirebaseInstanceId;

    /**
     * Subscription added to stay in touch when an action push event was receipt.
     */
    private ISubscription mActionEventSubscription;

    /**
     * Constructor for the push notifications API.
     *
     * @param halo The halo instance.
     */
    private HaloNotificationsApi(@NonNull final Halo halo, @NonNull FirebaseInstanceId firebaseInstanceId) {
        super(halo);
        AssertionUtils.notNull(firebaseInstanceId, "firebaseInstanceId");
        mRefreshSubscription = NotificationEmitter.listenRefreshTokenEvent(halo.context(), new InstanceIDService.RefreshNotificationTokenListener() {
            @Override
            public void onRefreshToken() {
                updateToken();
            }
        });
        mFirebaseInstanceId = firebaseInstanceId;
        mDeviceSyncSubscription = Halo.core().manager().subscribeForDeviceSync(new Subscriber() {
            @Override
            public void onEventReceived(@NonNull Event event) {
                updateToken();
            }
        });

        NotificationService.setIdGenerator(new HaloNotificationIdGenerator());
    }

    /**
     * Creates the push api with the halo instance. You need the
     * halo instance to be properly configured and ready to be used.
     *
     * @param halo The halo instance.
     * @return The created notifications API.
     */
    @Keep
    @NonNull
    @Api(2.0)
    public static HaloNotificationsApi with(@NonNull Halo halo) {
        return new HaloNotificationsApi(halo, FirebaseInstanceId.getInstance());
    }

    /**
     * Enable the push events to track
     */
    @Keep
    @NonNull
    @Api(2.4)
    public void enablePushEvents() {
        mActionEventSubscription = NotificationEmitter.createNotificationEventSubscription(context(),null);
        NotificationService.enablePushEvents();
    }

    /**
     * Enable the push events to track
     * @param listener The listener to receive the actions.
     */
    @Keep
    @NonNull
    @Api(2.4)
    public void enablePushEvents(@NonNull HaloNotificationEventListener listener) {
        mActionEventSubscription = NotificationEmitter.createNotificationEventSubscription(context(),listener);
        NotificationService.enablePushEvents();
    }

    /**
     * Notify a action event on the push notification.
     *
     * @param haloPushEvent The HALO push notification action event.
     * @return
     */
    @Keep
    @NonNull
    @Api(2.4)
    public HaloInteractorExecutor<HaloPushEvent> notifyPushEvent(@NonNull HaloPushEvent haloPushEvent) {
        return new HaloInteractorExecutor<>(halo(),
                "Push notify event action",
                new NotificationEventInteractor(new NotificationEventRepository(new NotificationEventRemoteDatasource(halo().framework().network())), haloPushEvent)
        );
    }

    /**
     * Add a custom id generator for notifications.
     *
     * @param customIdGenerator The custom id generator
     */
    @Keep
    @Api(2.3)
    public void customIdGenerator(@NonNull NotificationIdGenerator customIdGenerator) {
        AssertionUtils.notNull(customIdGenerator, "customIdGenerator");
        NotificationService.setIdGenerator(customIdGenerator);
    }

    /**
     * Sets a notification listener that listens for every notification received from HALO.
     * You can attach as many listeners as you want, but make
     * sure you remove the listener by calling <code>subscription.unsubscribe();</code>
     *
     * @param listener The listener that will be attached.
     * @return A subscription for the listener provided.
     */
    @Keep
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "Subscription.unsubscribe() to avoid memory leaks")
    public ISubscription listenAllNotifications(@NonNull HaloNotificationListener listener) {
        AssertionUtils.notNull(listener, "listener");
        return NotificationEmitter.createAllNotificationSubscription(context(), listener);
    }

    /**
     * Sets a notification listener that listens for not silent received from HALO.
     * You can attach as many listeners as you want, but make
     * sure you remove the listener by calling <code>subscription.unsubscribe();</code>
     *
     * @param listener The listener that will be attached.
     * @return A subscription for the listener provided.
     */
    @Keep
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "Subscription.unsubscribe() to avoid memory leaks")
    public ISubscription listenNotSilentNotifications(@NonNull HaloNotificationListener listener) {
        AssertionUtils.notNull(listener, "listener");
        return NotificationEmitter.createNotSilentNotificationSubscription(context(), listener);
    }

    /**
     * Sets a notification listener that listens for silent notifications received from HALO.
     * You can attach as many listeners as you want, but make
     * sure you remove the listener by calling <code>subscription.unsubscribe();</code>
     *
     * @param listener The listener that will be attached.
     * @return A subscription for the listener provided.
     */
    @Keep
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "Subscription.unsubscribe() to avoid memory leaks")
    public ISubscription listenSilentNotifications(@NonNull HaloNotificationListener listener) {
        AssertionUtils.notNull(listener, "listener");
        return NotificationEmitter.createSilentNotificationSubscription(context(), listener);
    }

    /**
     * Sets a notification listener that listens for two factor authentication code notifications received from HALO.
     * You can attach as many listeners as you want, but make
     * sure you remove the listener by calling <code>subscription.unsubscribe();</code>
     *
     * @param listener The listener that will be attached.
     * @return A subscription for the listener provided.
     */
    @Keep
    @NonNull
    @Api(2.3)
    @CheckResult(suggest = "Subscription.unsubscribe() to avoid memory leaks")
    public ISubscription listenTwoFactorNotifications(@NonNull HaloNotificationListener listener) {
        AssertionUtils.notNull(listener, "listener");
        return NotificationEmitter.createTwoFActorSubscription(context(), listener);
    }

    /**
     * Sets the custom notification decorator. A decorator allows the user to
     * override current behavior of the notification and customize the way it is shown.
     * You can only attach one custom decorator and chain between those decorators. Here we
     * provide an example to add a title to the notification and chain the next decorator.
     * <pre><code>
     * public class NotificationTitleDecorator extends HaloNotificationDecorator {
     *
     *  public NotificationTitleDecorator(HaloNotificationDecorator decorator) {
     *      super(decorator);
     *  }
     *
     *  public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
     *      String title = bundle.getString("title");
     *      if (!TextUtils.isEmpty(title)) {
     *          builder.setContentTitle(title);
     *      }
     *      return chain(builder, bundle);
     *  }
     * }
     * </code></pre>
     * <p>
     * If you set two different custom decorators the last one is the used.
     *
     * @param decorator The decorator to set in the notification service.
     */
    @Keep
    @Api(2.0)
    public void setNotificationDecorator(@Nullable HaloNotificationDecorator decorator) {
        NotificationService.setNotificationDecorator(decorator);
    }

    /**
     * Gets the custom notification decorator. A decorator allows the user to
     * override current behavior of the notification and customize the way it is shown.
     *
     * @return The decorator.
     */
    @Keep
    @Nullable
    @Api(2.3)
    public HaloNotificationDecorator getNotificationDecorator() {
        return NotificationService.getNotificationDecorator();
    }

    /**
     * Provides the current firebase token available in the device.
     * We discourage to store it in other place than the instance of firebase
     * since the token can change and unexpected behaviors may reflect.
     *
     * @return The token provided by firebase.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public String token() {
        return mFirebaseInstanceId.getToken();
    }

    /**
     * Releases the notification API. This removes the listeners and avoids
     * leaking memory because of those listeners. The proper callback
     * to call it is <code>onTerminate</code> in the <code>Application</code> class.
     */
    @Keep
    @Api(2.0)
    public void release() {
        mRefreshSubscription.unsubscribe();
        mDeviceSyncSubscription.unsubscribe();
        if (mActionEventSubscription != null) {
            mActionEventSubscription.unsubscribe();
        }
    }

    /**
     * Given a bundle provided with the notification it tries to
     * extract the id of the notification related to this bundle.
     * This way you can interact with the notification, cancelling it or
     * relaunching if needed.
     *
     * @param bundle The bundle of provided by the {@link HaloNotificationListener}.
     * @return The id or null if this data is not related to an UI notification
     * showed with HALO.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public Integer getNotificationId(@NonNull Bundle bundle) {
        AssertionUtils.notNull(bundle, "bundle");
        return NotificationService.getNotificationId(bundle);
    }

    /**
     * Renews the token and syncs the information into HALO.
     */
    private void updateToken() {
        String token = token();
        if (token != null) {
            halo().manager().setNotificationsToken(token).execute();
        }
    }
}
