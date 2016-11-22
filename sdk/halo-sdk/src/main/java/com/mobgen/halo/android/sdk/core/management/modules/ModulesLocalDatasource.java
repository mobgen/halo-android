package com.mobgen.halo.android.sdk.core.management.modules;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Delete;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Select;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.sdk.core.internal.storage.HaloManagerContract;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;

import java.util.List;

/**
 * Local datasource to access the modules.
 */
public class ModulesLocalDatasource {

    /**
     * The storage api.
     */
    private HaloStorageApi mStorageApi;


    /**
     * Constructor for the local datasource.
     * @param storageApi The storage api.
     */
    public ModulesLocalDatasource(HaloStorageApi storageApi) {
        mStorageApi = storageApi;
    }

    /**
     * Provides the modules stored in local.
     * @return The modules stored.
     */
    @NonNull
    public Cursor getModules(){
        return Select.all()
                .from(HaloManagerContract.RemoteModules.class)
                .on(mStorageApi.db(), "Queries all the modules joined with its module types in the dataLocal database");
    }

    /**
     * Saves the list of modules into the database.
     * @param modules The list of modules.
     */
    public void saveModules(@Nullable final List<HaloModule> modules) throws HaloStorageGeneralException {
        HaloDataLite database = mStorageApi.db();
        database.transaction(new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) {
                Delete.from(HaloManagerContract.RemoteModules.class).on(database);
                if (modules != null) {
                    for (HaloModule remoteModule : modules) {
                        database.insert(ORMUtils.getTableName(HaloManagerContract.RemoteModules.class), null, remoteModule.getContentValues());
                    }
                }
            }
        });
    }
}
