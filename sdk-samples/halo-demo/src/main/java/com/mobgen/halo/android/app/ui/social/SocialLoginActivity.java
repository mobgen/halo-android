package com.mobgen.halo.android.app.ui.social;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.SignInButton;
import com.google.zxing.WriterException;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.generated.HaloContentQueryApi;
import com.mobgen.halo.android.app.model.chat.QRContact;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.models.IdentifiedUser;
import com.mobgen.halo.android.auth.providers.SocialNotAvailableException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

/**
 * Social login activity to login with different accounts.
 */
public class SocialLoginActivity extends MobgenHaloActivity implements View.OnClickListener, CallbackV2<IdentifiedUser> {

    /**
     * The context.
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
     * Halo sign in.
     */
    private Button mSignInWithHalo;
    /**
     * Token information button
     */
    private Button mTokenInformation;
    /**
     * Google sign in.
     */
    private SignInButton mSignInWithGoogle;
    /**
     * Button to sign in.
     */
    private Button mSignInWithFacebook;

    /**
     * The qr image
     */
    private ImageView mQrCredential;

    /**
     * The QR file
     */
    private File mQrFile;
    /**
     * Starts the activity.
     *
     * @param context The context to start this activity.
     */
    public static void startActivity(@NonNull Context context) {
        Intent intent = new Intent(context, SocialLoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
        mContext = this;
        mAuthApi = MobgenHaloApplication.getHaloAuthApi();
        mSignInWithGoogle = (SignInButton) findViewById(R.id.google_sign_in);
        mSignInWithFacebook = (Button) findViewById(R.id.facebook_sign_in);
        mSignInWithHalo = (Button) findViewById(R.id.halo_sign_in);
        mLoginWithHalo = (Button) findViewById(R.id.halo_login);
        mTokenInformation = (Button) findViewById(R.id.halo_token_information);
        mQrCredential = (ImageView) findViewById(R.id.im_qr_credential);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSignInWithGoogle.setOnClickListener(this);
        mSignInWithFacebook.setOnClickListener(this);
        mLoginWithHalo.setOnClickListener(this);
        mSignInWithHalo.setOnClickListener(this);
        mTokenInformation.setOnClickListener(this);

        mQrFile =  new File(MobgenHaloApplication.halo().context().getExternalFilesDir(null).getAbsolutePath().toString() + "/qr/");
    }

    @Override
    public void onResume(){
        super.onResume();
        if(new File(mQrFile.toString() + "/profile.jpg").exists()){
            mQrCredential.setVisibility(View.VISIBLE);
            Picasso.with(this).load(new File(mQrFile.toString() + "/profile.jpg")).into(mQrCredential);
        }
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.social_login_title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.google_sign_in) {
            try {
                mAuthApi.loginWithSocial(HaloAuthApi.SOCIAL_GOOGLE_PLUS, this);
            } catch (SocialNotAvailableException e) {
                Snackbar.make(getWindow().getDecorView(), getString(R.string.error_provider_not_available), Snackbar.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.facebook_sign_in) {
            try {
                mAuthApi.loginWithSocial(HaloAuthApi.SOCIAL_FACEBOOK, this);
            } catch (SocialNotAvailableException e) {
                Snackbar.make(getWindow().getDecorView(), getString(R.string.error_provider_not_available), Snackbar.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.halo_login) {
            Intent intent = new Intent(mContext, SocialHaloLogin.class);
            mContext.startActivity(intent);
        } else if (v.getId() == R.id.halo_sign_in) {
            Intent intent = new Intent(mContext, SocialHaloSignIn.class);
            mContext.startActivity(intent);
        } else if (v.getId() == R.id.halo_token_information) {
            Intent intent = new Intent(mContext, SocialTokenInformation.class);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
        if (!result.status().isCanceled()) {
            if (result.status().isOk()) { // Ok
                String alias = MobgenHaloApplication.halo().getCore().manager().getDevice().getAlias();
                String appId = MobgenHaloApplication.halo().getCore().manager().getAppId();
                String userName = result.data().getUser().getName();
                String qrText = "halo://chat?alias=" + alias + "&appId="+ appId + "&userName=" + userName;
                QRGEncoder qrgEncoder = new QRGEncoder(qrText, null, QRGContents.Type.TEXT, 200);
                try {
                    // Getting QR-Code as Bitmap
                    Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                    // Setting Bitmap to ImageView
                    mQrCredential.setImageBitmap(bitmap);
                    mQrCredential.setVisibility(View.VISIBLE);
                    QRGSaver.save(mQrFile.toString(), "/profile", bitmap, QRGContents.ImageType.IMAGE_JPEG);
                    //save contact data into storage
                    HaloContentQueryApi.with(MobgenHaloApplication.halo())
                            .insertContact(alias,userName,mQrFile.toString() + "/profile.jpg")
                            .asContent(QRContact.class)
                            .threadPolicy(Threading.POOL_QUEUE_POLICY)
                            .execute(new CallbackV2<List<QRContact>>() {
                                @Override
                                public void onFinish(@NonNull HaloResultV2<List<QRContact>> result) {

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
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }
}
