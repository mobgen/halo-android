package com.mobgen.halo.android.app.ui.chat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.zxing.WriterException;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.generated.HaloContentQueryApi;
import com.mobgen.halo.android.app.model.chat.QRContact;
import com.mobgen.halo.android.app.model.notification.Notification;
import com.mobgen.halo.android.app.model.notification.NotificationRequest;
import com.mobgen.halo.android.app.model.notification.Payload;
import com.mobgen.halo.android.app.model.notification.Schedule;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.chat.messages.MessagesActivity;
import com.mobgen.halo.android.app.ui.modules.partial.ModulesActivity;
import com.mobgen.halo.android.app.ui.views.DividerItemDecoration;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.network.client.body.HaloMediaType;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import okhttp3.RequestBody;

/**
 * Chat rooms for current user
 */
public class ChatRoomActivity extends MobgenHaloActivity implements SwipeRefreshLayout.OnRefreshListener, ChatsAdapter.ChatRoomsCallback {
    /**
     *
     * The bundle for user name.
     */
    public static final String BUNDLE_QRCONTACT_USER_NAME = "bundle_user_name";
    /**
     * The bundle for alais
     */
    public static final String BUNDLE_QRCONTACT_ALIAS = "bundle_user_alias";
    /**
     * Scan code for activity result
     */
    private static final int CODE_SCAN_ACTIVITY = 102;
    /**
     * Permission camera to scan qr codes.
     */
    private static final int REQUEST_CAMAERA_PERMS = 12;
    /**
     * The context.
     */
    private Context mContext;
    /**
     * The refresh layout.
     */
    private SwipeRefreshLayout mRefreshLayout;
    /**
     * Chat contact adapter.
     */
    private ChatsAdapter mAdapter;
    /**
     * The current user alias
     */
    private String mAlias;
    /**
     * The current user name.
     */
    private String mUserName;

    /**
     * Starts the activity.
     *
     * @param context The context to start this activity.
     */
    public static void startActivity(@NonNull Context context) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        context.startActivity(intent);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, ChatRoomActivity.class);
    }

    /**
     * Provides the deeplink for chat rooms activity.
     *
     * @param context The context to start the activity.
     * @param extras  The extras.
     */
    public static PendingIntent getDeeplink(Context context, Bundle extras) {
        Intent intentModulesActivity = ModulesActivity.getIntent(context);
        Intent intentChatRoomActivity = ChatRoomActivity.getIntent(context);
        intentChatRoomActivity.putExtras(extras);
        return TaskStackBuilder.create(context)
                .addNextIntent(intentModulesActivity)
                .addNextIntent(intentChatRoomActivity)
                .getPendingIntent(0, Intent.FILL_IN_PACKAGE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Provides the deeplink for a chat room.
     *
     * @param context The context to start the activity.
     * @param extras  The extras.
     */
    public static PendingIntent getDeeplinkMessage(Context context, Bundle extras) {
        Intent intentModulesActivity = ModulesActivity.getIntent(context);
        Intent intentChatRoomActivity = ChatRoomActivity.getIntent(context);
        Intent intentMessagesActivity = new Intent(context, MessagesActivity.class);
        intentMessagesActivity.putExtras(extras);
        return TaskStackBuilder.create(context)
                .addNextIntent(intentModulesActivity)
                .addNextIntent(intentChatRoomActivity)
                .addNextIntent(intentMessagesActivity)
                .getPendingIntent(0, Intent.FILL_IN_PACKAGE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //save the new contact info from push notification
        if(getIntent()!=null && getIntent().getExtras()!=null &&
                getIntent().getExtras().getString(BUNDLE_QRCONTACT_USER_NAME)!=null &&
                getIntent().getExtras().getString(BUNDLE_QRCONTACT_ALIAS)!=null ) {
            String newContactUserName = getIntent().getExtras().getString(BUNDLE_QRCONTACT_USER_NAME);
            String newContactAlias = getIntent().getExtras().getString(BUNDLE_QRCONTACT_ALIAS);
            QRContact newContactFromPush = new QRContact(newContactAlias, newContactUserName, null);
            saveContant(newContactFromPush);
        }

        setContentView(R.layout.generic_recycler_refresh);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mAdapter = new ChatsAdapter(this, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(this);
        ViewUtils.refreshing(mRefreshLayout, true);
        mContext = this;

        //change to editor credential
        Halo.instance().core().credentials(Credentials.createClient("halotestappclient", "halotestapppass"));
    }

    @Override
    public void onResume(){
        super.onResume();
        //get permission for camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},REQUEST_CAMAERA_PERMS);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        requestChatRooms();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CODE_SCAN_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                Bundle result =data.getExtras();
                String scanResult = result.getString(QRScanActivity.BUNDLE_SCAN_RESULT);
                Map<String, String> params = getParams(scanResult);
                if(params!=null) {
                    String alias = params.get("alias");
                    String appId = MobgenHaloApplication.halo().getCore().manager().getAppId();
                    String userName = params.get("userName");
                    QRContact newContact = new QRContact(alias, userName, null);
                    QRContact myContact = new QRContact(mAlias, mUserName, null);
                    saveContant(newContact);
                    sendPushToInviteOther(myContact, newContact.getAlias(), appId);
                } else {
                    Toast.makeText(ChatRoomActivity.this, getString(R.string.chat_scan_result_error), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Get chat rooms available to chat
     */
    private void requestChatRooms(){
        mAlias = MobgenHaloApplication.halo().getCore().manager().getDevice().getAlias();
        HaloContentQueryApi.with(MobgenHaloApplication.halo())
                .getUserName(MobgenHaloApplication.halo().getCore().manager().getDevice().getAlias())
                .asContent(QRContact.class)
                .execute(new CallbackV2<List<QRContact>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {
                        if(result.data().size()>0) {
                            mUserName = result.data().get(0).getName();
                        }
                        HaloContentQueryApi.with(MobgenHaloApplication.halo())
                                .getConversations(mAlias)
                                .asContent(QRContact.class)
                                .execute(new CallbackV2<List<QRContact>>() {
                                    @Override
                                    public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {
                                        ViewUtils.refreshing(mRefreshLayout, false);
                                        if(result.data().size()==1 && result.data().get(0).getAlias().equals(MessagesActivity.MULTIPLE_ROOM)){
                                            showEmptyState();
                                        } else {
                                            mAdapter.setQRContact(result);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }
                });


    }

    /**
     * Show a tutorial for new users to scan a qr contact
     */
    private void showEmptyState() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Rect bounds = new Rect(width*2-240,180,0,0);
        TapTargetView.showFor((ChatRoomActivity)mContext,
                TapTarget.forBounds(bounds, getString(R.string.chat_empty_contacts_title), getString(R.string.chat_empty_contacts))
                        .outerCircleColor(R.color.orange_mobgen)
                        .targetCircleColor(R.color.white)
                        .titleTextSize(25)
                        .titleTextColor(R.color.white)
                        .descriptionTextSize(20)
                        .descriptionTextColor(R.color.white)
                        .textColor(R.color.white)
                        .textTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Lab-Medium.ttf"))
                        .dimColor(R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(false)
                        .targetRadius(60),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        QRScanActivity.startActivityForResult((ChatRoomActivity)mContext,CODE_SCAN_ACTIVITY);
                    }
                });
    }


    /**
     * Save the contact on database and qr image file.
     *
     * @param qrContact the contact to save.
     */
    private void saveContant(QRContact qrContact) {
        String appId = MobgenHaloApplication.halo().getCore().manager().getAppId();
        String qrText = "halo://chat?alias=" + qrContact.getAlias() + "&appId="+ appId + "&userName=" + qrContact.getName();

        //save new contact
        QRGEncoder qrgEncoder = new QRGEncoder(qrText, null, QRGContents.Type.TEXT, 200);
        File photoFile =  new File(MobgenHaloApplication.halo().context().getExternalFilesDir(null).getAbsolutePath().toString() + "/qr/");
        try {
            //Save QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            QRGSaver.save(photoFile.toString(), "/" + qrContact.getName(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
            //save contact data into storage
            HaloContentQueryApi.with(MobgenHaloApplication.halo())
                    .insertContact(qrContact.getAlias(),qrContact.getName(),photoFile.toString() + "/" + qrContact.getName() + ".jpg")
                    .asContent(QRContact.class)
                    .threadPolicy(Threading.POOL_QUEUE_POLICY)
                    .execute(new CallbackV2<List<QRContact>>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {
                            if(result.status().isOk()){
                                requestChatRooms();
                            }
                        }
                    });
        } catch (WriterException e) {
        }
    }

    /**
     * Invite a contact via push notification to start a conversation
     *
     * @param myContact My contact to share
     * @param alaisToSend The alias to send the message
     * @param appId The appId
     */
    private void sendPushToInviteOther(QRContact myContact, String alaisToSend,String appId){
        //send push to other phone
        final Schedule schedule = createAliasNotification(myContact, alaisToSend,appId);
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
                }
            }
        });
    }

    /**
     * Create the playload for the notification
     *
     * @param qrContact The contact
     * @param senderAlias The alias who send the push
     * @param appid The appId to notify
     * @return
     */
    private Schedule createAliasNotification(QRContact qrContact, String senderAlias,String appid) {
        int appId = Integer.parseInt(appid);
        Payload payload = new Payload(Notification.builder()
                .setTitle(mContext.getString(R.string.chat_new_contact_title)+ qrContact.getName())
                .setBody(mContext.getString(R.string.chat_new_contact_msg))
                .build(), false , qrContact);
        return new Schedule("Chat contact",appId , new String[]{senderAlias}, null, payload, false);
    }

    /**
     * Extract params from qr uri.
     * @param url The url from qr code.
     * @return The params extracted
     */
    @Nullable
    private Map<String, String> getParams(@NonNull String url) {
        try {
            url = url.substring(url.indexOf("?") + 1, url.length());
            String[] params = url.split("&");
            Map<String, String> map = new HashMap<String, String>();
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
            return map;
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_qr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_scan_qr) {
            QRScanActivity.startActivityForResult(this,CODE_SCAN_ACTIVITY);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.chat_room_title);
    }


    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    public void onRefresh() {
        requestChatRooms();
    }

    @Override
    public void onChatRoomTap(QRContact qrContact, ChatRoomViewHolder viewHolder, Boolean isFromImage) {
        if(isFromImage){
            final ImagePopup imagePopup = new ImagePopup(this);
            imagePopup.setBackgroundColor(Color.BLACK);
            imagePopup.setWindowWidth(1000);
            imagePopup.setWindowHeight(1000);
            imagePopup.setHideCloseIcon(false);
            imagePopup.setImageOnClickClose(true);
            imagePopup.initiatePopup(viewHolder.mThumbnail.getDrawable());
        } else {
            MessagesActivity.startActivity(this, qrContact.getName(), qrContact.getAlias());
        }
    }

    @Override
    public void onChatRoomSelected(final QRContact qrContact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomActivity.this); //alert for confirm to delete
        builder.setMessage(getString(R.string.chat_delete_msg_title));    //set message

        builder.setPositiveButton(getString(R.string.chat_delete_confirm), new DialogInterface.OnClickListener() { //when click on DELETE
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!qrContact.getAlias().equals(MessagesActivity.MULTIPLE_ROOM)) {
                    HaloContentQueryApi.with(MobgenHaloApplication.halo())
                            .deleteContact(qrContact.getAlias())
                            .asContent(QRContact.class)
                            .execute(new CallbackV2<List<QRContact>>() {
                                @Override
                                public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {
                                    if (result.status().isOk()) {
                                        mAdapter.deteleItem(qrContact);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ChatRoomActivity.this, getString(R.string.chat_delete_channel_broadcast), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }).setNegativeButton(getString(R.string.chat_delete_cancel), new DialogInterface.OnClickListener() {  //not removing items if cancel is done
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        }).show();
    }
}
