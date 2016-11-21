package com.mobgen.halo.android.sdk.core.internal.storage;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.storage.database.HaloDatabaseMigration;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;


/**
 * This is the first database version for HALO which should create the tables for the first time.
 * Once working it should never be modified.
 */
public class HaloMigration2$0$0 extends HaloDatabaseMigration {

    /**
     * The version of the database for this script.
     */
    public static final int VERSION = 1;

    @Override
    public void updateDatabase(@NonNull SQLiteDatabase database) {
        //General content table
        Create.table(HaloManagerContract.RemoteModules.class).on(database, "Create module table");
    }

    @Override
    public int getDatabaseVersion() {
        return VERSION;
    }
}
