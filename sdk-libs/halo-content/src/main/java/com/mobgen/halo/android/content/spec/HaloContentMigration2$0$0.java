package com.mobgen.halo.android.content.spec;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.spec.HaloContentContract.ContentSearch;
import com.mobgen.halo.android.content.spec.HaloContentContract.ContentSearchQuery;
import com.mobgen.halo.android.content.spec.HaloContentContract.ContentSync;
import com.mobgen.halo.android.content.spec.HaloContentContract.ContentSyncLog;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseMigration;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;

/**
 * @hide Migration for the 2.0.0 release.
 */
public class HaloContentMigration2$0$0 extends HaloDatabaseMigration {

    /**
     * The version for the 2.0.0 version.
     */
    public static final int VERSION =  2;

    @Override
    public void updateDatabase(@NonNull SQLiteDatabase database) {
        Create.table(ContentSearch.class).on(database, "Create general content instance table");
        Create.table(ContentSearchQuery.class).on(database, "Create general content search table");
        Create.table(ContentSync.class).on(database, "Creates the sync table");
        Create.table(ContentSyncLog.class).on(database, "Creates the sync log table");
        Create.table(HaloContentContract.Batch.class).on(database, "Create the batch table");
    }

    @Override
    public int getDatabaseVersion() {
        return VERSION;
    }
}
