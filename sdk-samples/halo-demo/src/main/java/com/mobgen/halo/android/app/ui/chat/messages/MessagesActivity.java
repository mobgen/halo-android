package com.mobgen.halo.android.app.ui.chat.messages;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mobgen.halo.android.app.BuildConfig;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.generated.HaloContentQueryApi;
import com.mobgen.halo.android.app.model.chat.ChatMessage;
import com.mobgen.halo.android.app.model.chat.QRContact;
import com.mobgen.halo.android.app.model.notification.Notification;
import com.mobgen.halo.android.app.model.notification.NotificationRequest;
import com.mobgen.halo.android.app.model.notification.Payload;
import com.mobgen.halo.android.app.model.notification.Schedule;
import com.mobgen.halo.android.app.notifications.MessagesNotificationReceiver;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.chat.ChatMessageService;
import com.mobgen.halo.android.app.ui.chat.ChatRoomActivity;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.framework.network.client.body.HaloMediaType;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.RequestBody;

import static com.mobgen.halo.android.app.notifications.DeeplinkDecorator.BUNDLE_NOTIFICATION_INAPP;

/**
 * Conversation with a user
 */
public class MessagesActivity extends MobgenHaloActivity implements MessagesNotificationReceiver.MessageReceiveListener,View.OnClickListener,SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessagesCallback {

    public static final String MULTIPLE_ROOM = "multiple";
    /**
     * The user name.
     */
    public static final String BUNDLE_USER_NAME = "bundle_user_name";
    /**
     * The user alias.
     */
    public static final String BUNDLE_USER_ALIAS = "bundle_user_alias";
    /**
     * Idenfified if conversation is multiple for broadcasting
     */
    public static final String BUNDLE_MULTIPLE = "bundle_user_alias";
    /**
     * The intent filter to handle push notification with new messages
     */
    public  static final String CHAT_MESSAGE_FILTER = "chat-newmessage";
    /**
     * The context.
     */
    private Context mContext;

    /**
     * The refresh layout
     */
    private SwipeRefreshLayout mRefreshLayout;
    /**
     * The coordinator layout parent
     */
    private CoordinatorLayout mCoordinatorParent;
    /**
     * The linear layout manager
     */
    private LinearLayoutManager mLinearLayoutManager;
    /**
     * The recyclerview.
     */
    private RecyclerView mRecyclerView;
    /**
     * The mSnackbar
     */
    private  Snackbar mSnackbar;
    /**
     *
     * Chat room name
     */
    private String mMessageRoomName;
    /**
     * The messages adapter
     */
    private MessagesAdapter mAdapter;
    /**
     * The contact user name
     */
    private String mContactUserName;
    /**
     * The contact alias
     */
    private String mContactAlias;
    /**
     * Identified if conversation is for broadcast
     */
    private boolean mIsMultiple;
    /**
     * My user name
     */
    private String mUserName;
    /**
     * Fild to write messages
     */
    private EditText mMessageField;
    /**
     * The button to send messages
     */
    private ImageButton mSendButton;
    /**
     * Receiver to handle new messages via push.
     */
    private MessagesNotificationReceiver mMessageReceiver;

    private Intent mChatService;

    private String[] broadcastAlias;

    /**
     * Starts the activity.
     *
     * @param context The context to start this activity.
     */
    public static void startActivity(@NonNull Context context, @NonNull String userName, @NonNull String alias) {
        Bundle data = new Bundle();
        data.putString(BUNDLE_USER_NAME, userName);
        data.putString(BUNDLE_USER_ALIAS, alias);
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContactUserName = getIntent().getExtras().getString(BUNDLE_USER_NAME);
        mContactAlias = getIntent().getExtras().getString(BUNDLE_USER_ALIAS);
        if(mContactAlias.equals(MessagesActivity.MULTIPLE_ROOM)){
            mIsMultiple = true;
            mMessageRoomName = getString(R.string.chat_multiple_room);
        } else {
            mIsMultiple = false;
            mMessageRoomName = mContactUserName;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_recycler);

        mCoordinatorParent = (CoordinatorLayout)findViewById(R.id.cl_parent);

        mMessageField = (EditText) findViewById(R.id.et_send_message);
        mSendButton = (ImageButton)findViewById(R.id.im_send);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mAdapter = new MessagesAdapter(this, this,mIsMultiple);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //recyclerView.setSta
        mRefreshLayout.setOnRefreshListener(this);
        ViewUtils.refreshing(mRefreshLayout, true);
        mContext = this;

        //TODO Delete this code when APP+ credential is ready to send push
        if(HaloManagerApi.with(MobgenHaloApplication.halo())
                .isAppAuthentication()) {
            Halo.instance().getCore().logout();
            Halo.instance().core().credentials(Credentials.createUser(BuildConfig.EDITOR_EMAIL,BuildConfig.EDITOR_PASS));
        }

        listenToNewMessages();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        requestChatMessages();
        mSendButton.setOnClickListener(this);

        //get all contacts
        HaloContentQueryApi.with(MobgenHaloApplication.halo())
                .getContacts(MobgenHaloApplication.halo().getCore().manager().getDevice().getAlias(),MessagesActivity.MULTIPLE_ROOM)
                .asContent(QRContact.class)
                .execute(new CallbackV2<List<QRContact>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {
                        if(result.data().size()>0) {
                            broadcastAlias = new String[result.data().size()];
                            for (int i=0;i<result.data().size();i++){
                                broadcastAlias[i] = result.data().get(i).getAlias();
                            }
                        }
                    }
                });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    scrollToBottom(10);
                }
            }
        });
    }

    @Override
    public void onNewMessage(final Intent intent) {
        if(intent.getBundleExtra(BUNDLE_NOTIFICATION_INAPP).getString(BUNDLE_USER_ALIAS).equals(mContactAlias)) {
            //handle notification
            requestChatMessages();
        } else {
            //show notification from other user
            String title;
            if (intent.getBundleExtra(BUNDLE_NOTIFICATION_INAPP).getString(BUNDLE_USER_ALIAS).equals(MessagesActivity.MULTIPLE_ROOM)) {
                title = mContext.getString(R.string.chat_new_msg_title_multiple);
            } else {
                title = mContext.getString(R.string.chat_new_msg_title);
            }
            title = title + " " + intent.getBundleExtra(BUNDLE_NOTIFICATION_INAPP).getString(BUNDLE_USER_NAME);

            mSnackbar = Snackbar
                    .make(mCoordinatorParent, title, Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.chat_new_message_inapp), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSnackbar.dismiss();
                            MessagesActivity.startActivity(mContext, intent.getBundleExtra(BUNDLE_NOTIFICATION_INAPP).getString(BUNDLE_USER_NAME),
                                    intent.getBundleExtra(BUNDLE_NOTIFICATION_INAPP).getString(BUNDLE_USER_ALIAS));
                            finish();
                        }
                    });
            mSnackbar.setActionTextColor(getResources().getColor(R.color.dark_green));
            View sbView = mSnackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange_mobgen));
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
            params.gravity = Gravity.TOP;
            sbView.setLayoutParams(params);
            mSnackbar.show();
        }
    }

    /**
     * Listen to new messages.
     */
    private void listenToNewMessages() {
        mChatService = new Intent(this, ChatMessageService.class);
        startService(mChatService);
        mMessageReceiver = new MessagesNotificationReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(CHAT_MESSAGE_FILTER));
    }

    /**
     * Remove the push listener to avoid memory leaks.
     */
    private void removeNewMessage() {
        stopService(mChatService);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeNewMessage();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.im_send) {
            //save local the message and send the push
            String message = mMessageField.getText().toString();
            if(!message.isEmpty()) {
                String storeUserName;
                String storeAlias;
                String senderAlias;
                if(mIsMultiple){
                    storeAlias = MessagesActivity.MULTIPLE_ROOM;
                    storeUserName = mUserName;
                    senderAlias = MessagesActivity.MULTIPLE_ROOM;
                } else {
                    storeAlias = mContactAlias;
                    storeUserName = mContactUserName;
                    senderAlias = MobgenHaloApplication.halo().getCore().manager().getDevice().getAlias();
                }
                final ChatMessage chatMessageToStore = new ChatMessage(storeAlias, storeUserName, message, new Date(), mIsMultiple, false);
                final ChatMessage chatMessageToSend = new ChatMessage(senderAlias, mUserName, message, new Date(), mIsMultiple, true);
                HaloContentQueryApi.with(MobgenHaloApplication.halo())
                        .insertMessage(chatMessageToStore.getAlias(), chatMessageToStore.getUserName(), chatMessageToStore.getMessage(),
                                chatMessageToStore.getCreationDate(), chatMessageToStore.getIsMultiple(), chatMessageToStore.getIsFromSender())
                        .asContent(ChatMessage.class)
                        .execute(new CallbackV2<List<ChatMessage>>() {
                            @Override
                            public void onFinish(@NonNull HaloResultV2<List<ChatMessage>> result) {
                                if (result.status().isOk()) {
                                    mAdapter.addNewMessage(chatMessageToStore);
                                    mAdapter.notifyDataSetChanged();
                                    mMessageField.setText("");
                                    //scroll to bottom
                                    scrollToBottom(100);
                                    //send push to other phone
                                    final Schedule schedule = createAliasNotification(chatMessageToSend, mContactAlias);
                                    final NotificationRequest[] request = {null};
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                request[0] = HaloRequest.builder(Halo.instance().framework().network())
                                                        .url(HaloNetworkConstants.HALO_ENDPOINT_ID, "api/authentication/schedule/")
                                                        .method(HaloRequestMethod.POST)
                                                        .body(RequestBody.create(HaloMediaType.APPLICATION_JSON.parseType(), ((Parser<Schedule, String>) Halo.instance().framework().parser().serialize(Schedule.class)).convert(schedule)))
                                                        .build().execute(NotificationRequest.class);
                                                request[0].setSchedule(schedule);
                                            } catch (IOException e) {
                                                Log.v("exception",e.toString());
                                            }
                                        }
                                    });


                                }
                            }
                        });
            }
        }
    }

    /**
     * Create the notification playload.
     *
     * @param chatMessage The chat message.
     * @param senderAlias The sender alias.
     * @return
     */
    public Schedule createAliasNotification(ChatMessage chatMessage, String senderAlias) {
        int appId = Integer.parseInt(MobgenHaloApplication.halo().getCore().manager().getAppId());
        String pushtTitle;
        if(mIsMultiple){
            pushtTitle = mContext.getString(R.string.chat_new_msg_title_multiple);
        } else {
            pushtTitle = mContext.getString(R.string.chat_new_msg_title);
        }
        Payload payload = new Payload(Notification.builder()
                .setTitle(pushtTitle + " " + chatMessage.getUserName())
                .setBody(chatMessage.getMessage())
                .build(), false , chatMessage);
        return new Schedule("Chat message",appId , getAlias(senderAlias), null, payload, false);
    }

    /**
     * Get a array with contacts
     *
     * @param senderAlias The alias to notify if its
     * @return If true Alias array with all contacts; Otherwise the alias
     */
    @NonNull
    private String[] getAlias(String senderAlias) {
        if(mIsMultiple){
            return broadcastAlias;
        } else {
            return new String[]{senderAlias};
        }
    }

    /**
     * Scroll to bottom the conversation.
     *
     * @param delay Delay time to scroll.
     */
    private void scrollToBottom(int delay) {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        }, delay);
    }

    /**
     * Get the messages with between this users.
     *
     */
    private void requestChatMessages(){
        if(mIsMultiple){//load the room for multiple chats
            HaloContentQueryApi.with(MobgenHaloApplication.halo())
                    .getUserName(MobgenHaloApplication.halo().getCore().manager().getDevice().getAlias())
                    .asContent(QRContact.class)
                    .execute(new CallbackV2<List<QRContact>>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {
                            if (result.data().size() > 0) {
                                mUserName = result.data().get(0).getName();
                            }
                            HaloContentQueryApi.with(MobgenHaloApplication.halo())
                                    .getMessagesMultipleRoom(true)
                                    .asContent(ChatMessage.class)
                                    .execute(new CallbackV2<List<ChatMessage>>() {
                                        @Override
                                        public void onFinish(@NonNull HaloResultV2<List<ChatMessage>> result) {
                                            ViewUtils.refreshing(mRefreshLayout, false);
                                            mAdapter.setChatMessage(result);
                                            mAdapter.notifyDataSetChanged();
                                            //scroll to bottom
                                            scrollToBottom(100);
                                        }
                                    });
                        }
                    });
        } else {
            HaloContentQueryApi.with(MobgenHaloApplication.halo())
                    .getUserName(MobgenHaloApplication.halo().getCore().manager().getDevice().getAlias())
                    .asContent(QRContact.class)
                    .execute(new CallbackV2<List<QRContact>>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {
                            if (result.data().size() > 0) {
                                mUserName = result.data().get(0).getName();
                            }
                            HaloContentQueryApi.with(MobgenHaloApplication.halo())
                                    .getMessages(mContactAlias, false)
                                    .asContent(ChatMessage.class)
                                    .execute(new CallbackV2<List<ChatMessage>>() {
                                        @Override
                                        public void onFinish(@NonNull HaloResultV2<List<ChatMessage>> result) {
                                            ViewUtils.refreshing(mRefreshLayout, false);
                                            mAdapter.setChatMessage(result);
                                            mAdapter.notifyDataSetChanged();
                                            //scroll to bottom
                                            scrollToBottom(100);
                                        }
                                    });
                        }
                    });
        }

    }

    @Override
    public String getToolbarTitle() {
        return mContactUserName;
    }


    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    public void onRefresh() {
        requestChatMessages();
    }


    @Override
    public void onMessageSelected(ChatMessage chatMessage, MessagesViewHolder viewHolder) {

    }
}
