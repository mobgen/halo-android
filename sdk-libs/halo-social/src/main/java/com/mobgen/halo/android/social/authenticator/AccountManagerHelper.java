package com.mobgen.halo.android.social.authenticator;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.models.HaloAuthProfile;

import java.util.Arrays;
import java.util.List;

/**
 *
 * The helper to work with account manager
 */
public class AccountManagerHelper {
    /**
     * The token type to store on account manager
     */
    private static String TOKEN_TYPE = "token_type";

    /**
     * The account manager.
     */
    private AccountManager mAccountManager;

    /**
     * Constructor
     *
     * @param context The Context.
     */
    public AccountManagerHelper(@NonNull Context context) {
        mAccountManager = AccountManager.get(context);
    }

    public AccountManager getAccountManager(){return mAccountManager;}

    /**
     * Set the token to an account
     *
     * @param account The account to store on account manager.
     * @param tokenType The token type
     * @param authToken The token generated
     *
     * @return The account  with the token.
     */
    @NonNull
    public Account setAuthToken(@NonNull Account account, @NonNull String tokenType, @NonNull String authToken) {
        AssertionUtils.notNull(account, "account");
        AssertionUtils.notNull(tokenType, "socialToken");
        AssertionUtils.notNull(authToken, "authToken");
        mAccountManager.setAuthToken(account, tokenType, authToken);
        return account;
    }

    /**
     * Add or update account on the account manager. This is for user password authentication
     *
     * @param accountType The account type to store on account manager.
     * @param tokenType   The token type
     * @param userName    The username of the user.
     * @param password    The password of the user.
     * @param authToken The token generated or updated
     *
     * @return Return the account added.
     */
    @Nullable
    public Account addAccountWithPassword(@NonNull String accountType, @NonNull String tokenType, @NonNull String userName, @NonNull String password, @NonNull String authToken) {
        AssertionUtils.notNull(accountType, "accountType");
        AssertionUtils.notNull(tokenType, "socialToken");
        AssertionUtils.notNull(userName, "userName");
        AssertionUtils.notNull(password, "password");
        if (ActivityCompat.checkSelfPermission(Halo.instance().context(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            Account account = new Account(userName, accountType);
            if(checkAccountExist(account,accountType)){
                mAccountManager.setPassword(account,password);
                mAccountManager.setUserData(account,tokenType,authToken);
                mAccountManager.setUserData(account,TOKEN_TYPE,tokenType);
                return account;
            }else {
                Bundle extraData = new Bundle();
                extraData.putString(tokenType,authToken);
                extraData.putString(TOKEN_TYPE,tokenType);
                if (mAccountManager.addAccountExplicitly(setAuthToken(account, tokenType, authToken), password, extraData)) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     * Add or update account on the account manager. This is for user with no password authentication
     *
     * @param accountType The account type to store on account manager.
     * @param tokenType   The token type
     * @param userName    The username of the user.
     * @param authToken The token generated
     *
     * @return Return the account added.
     */
    @Nullable
    public Account addAccountToken(@NonNull String accountType, @NonNull String tokenType, @NonNull String userName, @NonNull String authToken) {
        AssertionUtils.notNull(accountType, "accountType");
        AssertionUtils.notNull(tokenType, "socialToken");
        AssertionUtils.notNull(userName, "userName");
        if (ActivityCompat.checkSelfPermission(Halo.instance().context(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            Account account = new Account(userName, accountType);
            if(checkAccountExist(account,accountType)){
                mAccountManager.setUserData(account,tokenType,authToken);
                mAccountManager.setUserData(account,TOKEN_TYPE,tokenType);
                return setAuthToken(account,tokenType,authToken);
            }else {
                Bundle extraData = new Bundle();
                extraData.putString(tokenType,authToken);
                extraData.putString(TOKEN_TYPE,tokenType);
                if (mAccountManager.addAccountExplicitly(setAuthToken(account, tokenType, authToken), null, extraData)) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     * Gets the account stored based on email
     *
     * @param accountType The account type.
     * @param accountName The account name.
     *
     * @return Return the account stored.
     */
    @Nullable
    public Account recoverAccountByName(@NonNull String accountType, @NonNull String accountName) {
        AssertionUtils.notNull(accountType, "accountType");
        AssertionUtils.notNull(accountName, "accountName");
        if (ActivityCompat.checkSelfPermission(Halo.instance().context(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            List<Account> accounts = Arrays.asList(mAccountManager.getAccountsByType(accountType));
            if(accounts.size()>0){
                for(int i=0;i<accounts.size();i++) {
                    if (accounts.get(i).name.equals(accountName) && mAccountManager.getPassword(accounts.get(i)) != null) {
                        return accounts.get(i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the account stored based on account type and token type.
     *
     * @param accountType The account type.
     * @param tokenType The token type.
     *
     * @return Return the account stored.
     */
    @Nullable
    public Account recoverAccount(@NonNull String accountType, @NonNull String tokenType) {
        AssertionUtils.notNull(accountType, "accountType");
        AssertionUtils.notNull(tokenType, "tokenType");
        if (ActivityCompat.checkSelfPermission(Halo.instance().context(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            List<Account> accounts = Arrays.asList(mAccountManager.getAccountsByType(accountType));
            if(accounts.size()>0){
                for(int i=0;i<accounts.size();i++) {
                    if (tokenType.equals(mAccountManager.getUserData(accounts.get(i),TOKEN_TYPE))) {
                        return accounts.get(i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the HaloAuthProfile stored
     *
     * @param account The account.
     * @param alias The alias of the device.
     *
     * @return Return the authorization profile to make a halo login.
     */
    @Nullable
    public HaloAuthProfile getAuthProfile(@Nullable Account account, @NonNull String alias) {
        AssertionUtils.notNull(alias, "alias");
        if(account!=null && mAccountManager.getPassword(account)!=null) {
            return new HaloAuthProfile(account.name, mAccountManager.getPassword(account), alias);
        }else{
            return null;
        }
    }

    /**
     * Gets the auth token stored
     *
     * @param account The account.
     * @param tokenType The token type.
     *
     * @return Return the social token.
     */
    @Nullable
    public String getAuthToken(@Nullable Account account,@NonNull String tokenType) {
        AssertionUtils.notNull(tokenType, "tokenType");
        if(account!=null) {
            return mAccountManager.getUserData(account, tokenType);
        }
        return null;
    }

    /**
     * Check if exist account on account manager
     *
     * @param account The account.
     * @param accountType The account type to authenticate.
     * @return True if account is already authenticated, false otherwise.
     */
    @NonNull
    private boolean checkAccountExist(@NonNull Account account, @NonNull String accountType) {
        AssertionUtils.notNull(account, "account");
        AssertionUtils.notNull(accountType, "accountType");
        if (ActivityCompat.checkSelfPermission(Halo.instance().context(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            List<Account> accountList = Arrays.asList(mAccountManager.getAccountsByType(accountType));
            return accountList.contains(account);
        }
        return false;
    }



}
