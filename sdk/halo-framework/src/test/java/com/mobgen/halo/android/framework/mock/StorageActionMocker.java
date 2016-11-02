//package com.mobgen.halo.android.framework.mock;
//
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//
//import com.mobgen.halo.android.framework.api.HaloStorageApi;
//import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
//import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
//import com.mobgen.halo.android.framework.toolbox.actions.HaloStorageAction;
//import com.mobgen.halo.android.framework.toolbox.request.ActionRequest;
//import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
//
//public class StorageActionMocker<N, P, U> implements HaloStorageAction<N, P, U> {
//
//    private P mParsed;
//    private U mUnparsed;
//
//    public StorageActionMocker(P parsed, U unparsed) {
//        mParsed = parsed;
//        mUnparsed = unparsed;
//    }
//
//    @Nullable
//    @Override
//    public P parse(@NonNull HaloStorageApi storageApi, @Nullable U dataFetched, @NonNull ActionRequest request) throws HaloStorageException {
//        return mParsed;
//    }
//
//    @Nullable
//    @Override
//    public U fetch(@NonNull HaloStorageApi storageApi, @NonNull ActionRequest request) throws HaloStorageGeneralException {
//        return mUnparsed;
//    }
//
//    @Override
//    public void saveInStorage(@NonNull HaloStorageApi storage, @Nullable N data, @NonNull ActionRequest actionRequest, @NonNull HaloStatus.Builder status) throws HaloStorageGeneralException {
//
//    }
//}
