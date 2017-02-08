package com.mobgen.halo.android.sdk.core.management.modules;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProvider;

import org.json.JSONException;

import java.util.List;

/**
 * Data provider for each of the data modes.
 */
public class RequestModulesInteractor implements SelectorProvider<List<HaloModule>, Cursor> {

    /**
     * The modules repository.
     */
    private ModulesRepository mModulesRepository;

    private boolean enableFieldsMode = false;

    /**
     * The modules repository.
     * @param modulesRepository The modules repository.
     */
    public RequestModulesInteractor(@NonNull ModulesRepository modulesRepository, boolean withFields) {
        mModulesRepository = modulesRepository;
        enableFieldsMode = withFields;
    }

    /**
     * The modules repository.
     * @param modulesRepository The modules repository.
     */
    public RequestModulesInteractor(@NonNull ModulesRepository modulesRepository) {
        mModulesRepository = modulesRepository;
    }

    @NonNull
    @Override
    public HaloResultV2<List<HaloModule>> fromNetwork() throws HaloNetException , HaloParsingException {
        return mModulesRepository.getModulesFromNetwork(enableFieldsMode);
    }

    @NonNull
    @Override
    public HaloResultV2<Cursor> fromStorage() throws HaloStorageException {
        return mModulesRepository.getCachedModules();
    }

    @NonNull
    @Override
    public HaloResultV2<Cursor> fromNetworkStorage() throws HaloNetException, HaloStorageException {
        return mModulesRepository.getModules();
    }
}
