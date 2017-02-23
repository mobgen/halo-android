package com.mobgen.halo.android.twofactor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.twofactor.HaloTwoFactorApi;
import com.mobgen.halo.android.twofactor.callbacks.HaloSMSListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Listen to sms broadcast receiver to parse code form two factor authentication.
 */
@Keep
public class HaloSMSSubscription extends BroadcastReceiver implements ISubscription {

    /**
     * The context.
     */
    @NonNull
    private Context mContext;
    /**
     * The listener for this subscription.
     */
    @NonNull
    private HaloSMSListener mListener;

    /**
     * The provider name to look for.
     */
    @NonNull
    private String mProviderName;

    /**
     *  The error code to return if sms was not correct.
     */
    private static String ERROR_CODE = "-1";

    /**
     * Empty constuctor.
     */
    public HaloSMSSubscription(){

    }

    /**
     * Constructor for the receiver.
     *
     * @param context  The context.
     * @param listener The listener.
     * @param filter   A filter to register the receiver.
     */
    public HaloSMSSubscription(@NonNull Context context, @NonNull HaloSMSListener listener, @NonNull IntentFilter filter,@NonNull String providerName) {
        mContext = context;
        mListener = listener;
        mProviderName = providerName;
        context.registerReceiver(this, filter);
    }

    @Override
    public void unsubscribe() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String code = "";
        if (bundle != null) {
            final Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdusObj.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                String senderNumber = smsMessage.getDisplayOriginatingAddress();
                if (senderNumber.equals(mProviderName)) {
                    String message = smsMessage.getDisplayMessageBody();
                    code = getCode(message);
                    mListener.onSMSReceived(context, code, HaloTwoFactorApi.TWO_FACTOR_SMS_ISSUER);
                    break;
                }
            }
        } else {
            mListener.onSMSReceived(context,ERROR_CODE , HaloTwoFactorApi.TWO_FACTOR_SMS_ISSUER);
        }
    }

    /**
     * Helper regex to parse a 6 digits code from the sms.
     * @param message
     * @return The 6 digits code from sms otherwise null.
     */
    @Nullable
    private String getCode(@NonNull String message){
        AssertionUtils.notNull(message,"message");
        List<String> numberGroup = new ArrayList<>();
        String codeFromMessage = null;
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(message);
        while (m.find()) {
            numberGroup.add(m.group());
        }
        for(int i=0;i<numberGroup.size();i++){
            if(numberGroup.get(i).length()==6){
                codeFromMessage = numberGroup.get(i);
            }
        }
        return codeFromMessage;
    }
}
