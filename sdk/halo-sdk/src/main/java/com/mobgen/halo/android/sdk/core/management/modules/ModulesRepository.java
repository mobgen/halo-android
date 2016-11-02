package com.mobgen.halo.android.sdk.core.management.modules;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;

import java.util.List;

/**
 * Contains the modules.
 */
public class ModulesRepository {

    /**
     * Remote data source.
     */
    private ModulesRemoteDatasource mRemoteDatasource;
    /**
     * Local data source.
     */
    private ModulesLocalDatasource mLocalDatasource;

    /**
     * Constructor for the repository.
     * @param modulesRemoteDatasource The remote data source.
     * @param modulesLocalDatasource The local data source.
     */
    public ModulesRepository(@NonNull ModulesRemoteDatasource modulesRemoteDatasource, @NonNull ModulesLocalDatasource modulesLocalDatasource) {
        AssertionUtils.notNull(modulesRemoteDatasource, "remoteDatasource");
        AssertionUtils.notNull(modulesLocalDatasource, "localDatasource");
        mRemoteDatasource = modulesRemoteDatasource;
        mLocalDatasource = modulesLocalDatasource;
    }

    /**
     * Provides the modules cached from the network.
     * @return The modules cached from the network.
     */
    public HaloResultV2<List<HaloModule>> getModulesFromNetwork() {
        HaloStatus.Builder status = HaloStatus.builder();
        List<HaloModule> modules = null;
        try {
            modules = mRemoteDatasource.getModules();
        } catch (HaloNetException e) {
            Halog.e(getClass(), "Could not retrieve the modules from network.", e);
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), modules);
    }

    /**
     * Provides the modules.
     * @return Provides the modules from the local data source.
     */
    public HaloResultV2<Cursor> getModules() {
        HaloStatus.Builder status = HaloStatus.builder();
        Cursor cursor = null;
        try {
            mLocalDatasource.saveModules(mRemoteDatasource.getModules());
        } catch (HaloNetException | HaloStorageGeneralException e) {
            Halog.e(getClass(), "Error saving instances", e);
            status.error(e);
            status.dataLocal();
        } finally {
            try {
                cursor = mLocalDatasource.getModules();
            } catch (Exception e) {
                status.error(e);
            }
        }
        return new HaloResultV2<>(status.build(), cursor);
    }

    /**
     * Provides the local cached modules.
     * @return The local modules.
     */
    public HaloResultV2<Cursor> getCachedModules() {
        HaloStatus.Builder status = HaloStatus.builder();
        Cursor cursor = null;
        try {
            cursor = mLocalDatasource.getModules();
        }catch (Exception e){
            Halog.e(getClass(), "Could not obtain the modules from the local data source.", e);
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), cursor);
    }
}
