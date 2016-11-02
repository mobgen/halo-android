//package com.mobgen.halo.android.framework.mock;
//
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//
//import com.mobgen.halo.android.framework.api.HaloNetworkApi;
//import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
//import com.mobgen.halo.android.framework.toolbox.actions.HaloNetworkAction;
//import com.mobgen.halo.android.framework.toolbox.request.ActionRequest;
//import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
//
//public class NetworkActionMocker<P> implements HaloNetworkAction<P> {
//
//    private P mParsed;
//
//    public NetworkActionMocker(@Nullable P data) {
//        mParsed = data;
//    }
//
//    @Nullable
//    @Override
//    public P executeNetwork(@NonNull HaloNetworkApi clientApi, @NonNull ActionRequest actionRequest, @NonNull HaloStatus.Builder result) throws HaloNetException {
//        return mParsed;
//    }
//}
