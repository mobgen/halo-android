package com.mobgen.halo.android.translations.spec;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.storage.database.HaloDatabaseMigration;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;

/**
 * @hide Translations database migration for the plugin.
 */
public class TranslationsMigration2$0$0 extends HaloDatabaseMigration {

    /**
     * The version for this plugin.
     */
    public static final int VERSION = 1;

    @Override
    public void updateDatabase(@NonNull SQLiteDatabase database) {
        Create.table(HaloTranslationsContract.Translations.class).on(database);
    }

    @Override
    public int getDatabaseVersion() {
        return VERSION;
    }
}
