package com.mobgen.halo.android.app.ui.social;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.WriterException;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.generated.HaloContentQueryApi;
import com.mobgen.halo.android.app.model.chat.QRContact;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.chat.messages.MessagesActivity;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.IdentifiedUser;
import com.mobgen.halo.android.auth.providers.SocialNotAvailableException;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

import java.io.File;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class SocialHaloLogin extends MobgenHaloActivity implements View.OnClickListener, CallbackV2<IdentifiedUser> {

    /**
     * The context
     */
    private Context mContext;
    /**
     * The auth api instance.
     */
    private HaloAuthApi mAuthApi;
    /**
     * Halo sign in.
     */
    private Button mLoginWithHalo;

    /**
     * The QR file
     */
    private File mQrFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_halo_login);
        mContext = this;
        mAuthApi = MobgenHaloApplication.getHaloAuthApi();
        mLoginWithHalo = (Button) findViewById(R.id.halo_login);

        mQrFile =  new File(MobgenHaloApplication.halo().context().getExternalFilesDir(null).getAbsolutePath().toString() + "/qr/");

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mLoginWithHalo.setOnClickListener(this);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.social_halo_login_title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.halo_login) {
            final EditText editEmail = (EditText) findViewById(R.id.edit_login_email);
            final EditText editPassword = (EditText) findViewById(R.id.edit_login_password);
            try {
                final HaloAuthProfile authProfile = new HaloAuthProfile(editEmail.getText().toString().trim(), editPassword.getText().toString().trim());
                mAuthApi.loginWithHalo(HaloAuthApi.SOCIAL_HALO, authProfile, this);
            } catch (SocialNotAvailableException e) {
                Snackbar.make(getWindow().getDecorView(), getString(R.string.error_provider_not_available), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
        if (result.status().isOk()) { // Ok
            // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
            String alias = MobgenHaloApplication.halo().getCore().manager().getDevice().getAlias();
            String appId = MobgenHaloApplication.halo().getCore().manager().getAppId();
            String userName = result.data().getUser().getName();
            String qrText = "halo://chat?alias=" + alias + "&appId="+ appId + "&userName=" + userName;
            QRGEncoder qrgEncoder = new QRGEncoder(qrText, null, QRGContents.Type.TEXT, 200);
            try {
                // Getting QR-Code as Bitmap
                Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                QRGSaver.save(mQrFile.toString(), "/profile", bitmap, QRGContents.ImageType.IMAGE_JPEG);
                //save contact data into storage
                HaloContentQueryApi.with(MobgenHaloApplication.halo())
                        .insertContact(alias,userName,mQrFile.toString() + "/profile.jpg")
                        .asContent(QRContact.class)
                        .threadPolicy(Threading.POOL_QUEUE_POLICY)
                        .execute(new CallbackV2<List<QRContact>>() {
                            @Override
                            public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {
                                //create multichannel
                                HaloContentQueryApi.with(MobgenHaloApplication.halo())
                                        .insertContact(MessagesActivity.MULTIPLE_ROOM,mContext.getString(R.string.chat_multiple_room),mQrFile.toString() + "/profile.jpg")
                                        .asContent(QRContact.class)
                                        .execute();
                            }
                        });
            } catch (WriterException e) {
            }
            Halog.d(getClass(), result.data().toString());

            Snackbar.make(getWindow().getDecorView(), result.data().getUser().getName(), Snackbar.LENGTH_LONG).show();
        } else { // Error
            Halog.d(getClass(), result.status().exception().toString());
        }
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }
}
