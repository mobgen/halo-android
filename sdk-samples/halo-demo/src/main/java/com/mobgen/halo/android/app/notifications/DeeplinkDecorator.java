package com.mobgen.halo.android.app.notifications;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.generated.HaloContentQueryApi;
import com.mobgen.halo.android.app.model.PendingNotification;
import com.mobgen.halo.android.app.model.chat.ChatMessage;
import com.mobgen.halo.android.app.model.chat.QRContact;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.chat.ChatMessageService;
import com.mobgen.halo.android.app.ui.chat.ChatRoomActivity;
import com.mobgen.halo.android.app.ui.chat.messages.MessagesActivity;
import com.mobgen.halo.android.app.ui.generalcontent.GeneralContentItemActivity;
import com.mobgen.halo.android.app.ui.news.ArticleActivity;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.notifications.decorator.HaloNotificationDecorator;
import com.mobgen.halo.android.sdk.api.Halo;

import java.util.ArrayList;
import java.util.List;

import static com.mobgen.halo.android.app.ui.chat.messages.MessagesActivity.BUNDLE_NOTIFICATION_ID;
import static com.mobgen.halo.android.app.ui.chat.messages.MessagesActivity.BUNDLE_USER_NAME;
import static com.mobgen.halo.android.app.ui.chat.messages.MessagesActivity.BUNDLE_USER_ALIAS;


/**
 * Manages the deep linking if we recognize the module
 */
public class DeeplinkDecorator extends HaloNotificationDecorator {

    public static final String BUNDLE_NOTIFICATION_INAPP = "bundle_notification";

    private static final String NEWS_ID_INT = "560539b8e81e3b0100ef6cbe";
    private static final String NEWS_ID_STAGE = "56161a166947b516009db5b8";

    private static final String STORE_LOCATOR_INT = "";
    private static final String STORE_LOCATOR_STAGE = "";

    private List<PendingNotification> notifications = new ArrayList<>();

    /**
     * The context.
     */
    private Context mContext;

    public DeeplinkDecorator(Context context) {
        mContext = context;
    }

    @Override
    public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
        String extras = bundle.getString("extra");
        Object custom = bundle.get("custom");
        PendingIntent pendingIntent = null;
        if (extras != null) {
            String moduleId = bundle.getString("moduleId");
            if (moduleId != null) {
                //In case of the news item
                if (moduleId.equals(NEWS_ID_INT) || moduleId.equals(NEWS_ID_STAGE)) {
                    pendingIntent = ArticleActivity.getDeeplink(mContext, bundle, moduleId);
                } else if (!moduleId.equals(STORE_LOCATOR_INT) && !moduleId.equals(STORE_LOCATOR_STAGE)) { // It is not the station locator
                    pendingIntent = GeneralContentItemActivity.getDeeplink(mContext, bundle, moduleId);
                }
            }
        } else if(custom != null){
            //extract from notification bundle the data
            try {
                ChatMessage chatMessage = ChatMessage.deserialize(custom.toString(), Halo.instance().framework().parser());
                if(chatMessage.getAlias()!=null) {//this is a new message
                    if(chatMessage.getAlias().equals(MessagesActivity.MULTIPLE_ROOM)){
                        chatMessage.setIsMultiple(true);
                    } else {
                        chatMessage.setIsMultiple(false);
                    }
                    HaloContentQueryApi.with(MobgenHaloApplication.halo())
                            .insertMessage(chatMessage.getAlias(), chatMessage.getUserName(), chatMessage.getMessage(),
                                    chatMessage.getCreationDate(), chatMessage.getIsMultiple(), true)
                            .asContent(ChatMessage.class)
                            .execute();
                    Bundle data = new Bundle();
                    data.putString(BUNDLE_USER_NAME, chatMessage.getUserName());
                    data.putString(BUNDLE_USER_ALIAS, chatMessage.getAlias());
                    if (!isMessageServiceRunning(ChatMessageService.class)) {
                        data.putInt(BUNDLE_NOTIFICATION_ID,chatMessage.getAlias().hashCode());
                    }
                    pendingIntent = ChatRoomActivity.getDeeplinkMessage(mContext, data);
                    if (isMessageServiceRunning(ChatMessageService.class)) {
                        Intent newIntent = new Intent(MessagesActivity.CHAT_MESSAGE_FILTER);
                        newIntent.putExtra(BUNDLE_NOTIFICATION_INAPP,data);
                        newIntent.putExtra("pendingIntent", pendingIntent);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(newIntent);
                        return null;
                    } else {
                        stackNotifications(builder, chatMessage,chatMessage.getAlias().hashCode());
                    }
                } else { // save the new contact
                    QRContact qrContact = QRContact.deserialize(custom.toString(), Halo.instance().framework().parser());
                    if(qrContact.getAlias()!=null) {
                        Bundle data = new Bundle();
                        data.putString(BUNDLE_USER_NAME, qrContact.getName());
                        data.putString(BUNDLE_USER_ALIAS, qrContact.getAlias());
                        pendingIntent = ChatRoomActivity.getDeeplink(mContext, data);
                    } else {
                        return null;
                    }
                }
            } catch (HaloParsingException e) {
                return null;
            }
        }

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        return chain(builder, bundle);
    }

    /**
     * Stack notification by id.
     *
     * @param builder The builder of the notification
     * @param chatMessage The chat message to stack
     * @param notificationId The notification id.
     */
    private void stackNotifications(@NonNull NotificationCompat.Builder builder, ChatMessage chatMessage, int notificationId) {
        //we must store messages
        //save current notification
        HaloContentQueryApi.with(MobgenHaloApplication.halo())
                .savePendingMessage(notificationId, chatMessage.getMessage())
                .asContent(PendingNotification.class)
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute();
        //get pending notifications
        HaloContentQueryApi.with(MobgenHaloApplication.halo())
                .getPendingMessages(notificationId)
                .asContent(PendingNotification.class)
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(new CallbackV2<List<PendingNotification>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<PendingNotification>> result) {
                        notifications = result.data();
                    }
                });
        builder.setGroup("HALO");
        builder.setCategory(Notification.CATEGORY_MESSAGE);
        builder.setGroupSummary(true);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        String title;
        if (chatMessage.getAlias().equals(MessagesActivity.MULTIPLE_ROOM)) {
            title = mContext.getString(R.string.chat_new_msg_title_multiple);
        } else {
            title = mContext.getString(R.string.chat_new_msg_title);
        }
        title = title + " " + chatMessage.getUserName();
        inboxStyle.setBigContentTitle(title);
        inboxStyle.setSummaryText(mContext.getString(R.string.chat_stack_summary_part1) + " " + notifications.size() + " " + mContext.getString(R.string.chat_stack_summary_part2));
        // Moves events into the expanded layout
        for (int i = 0; i < notifications.size(); i++) {
            inboxStyle.addLine(notifications.get(i).getMessage());
        }
        builder.setStyle(inboxStyle);
    }

    /**
     * Check if message service is running
     *
     * @param serviceClass The service class.
     * @return True if its running; Otherwise false.
     */
    private boolean isMessageServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
