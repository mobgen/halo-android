package com.mobgen.halo.android.framework.mock.instrumentation;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseErrorHandler;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseMigration;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseVersionManager;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.testing.CallbackFlag;

import org.robolectric.RuntimeEnvironment;

import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloDatabaseInstrument {

    public static String givenADatabaseName(){
        return UUID.randomUUID().toString();
    }

    public static String givenAAliasName(){
        return "halo-test-alias";
    }

    public static HaloDataLite givenAHaloDataLite(){
        return HaloDataLite.builder(RuntimeEnvironment.application)
                .setErrorHandler(givenAErrorHandler())
                .setDatabaseVersion(givenDatabaseMigrations().getDatabaseVersion())
                .setDatabaseName(givenADatabaseName())
                .setVersionManager(givenADatabaseVersionManager())
                .build();
    }

    public static HaloDataLite.Builder givenAHaloDataLiteBuilder(){
        return HaloDataLite.builder(RuntimeEnvironment.application)
                .setErrorHandler(givenAErrorHandler())
                .setDatabaseVersion(givenDatabaseMigrations().getDatabaseVersion())
                .setDatabaseName(givenADatabaseName())
                .setVersionManager(givenADatabaseVersionManager());
    }

    public static HaloDatabaseVersionManager givenADatabaseVersionManager(){
        return HaloDatabaseVersionManager.builder()
                .add(givenDatabaseMigrations())
                .build();
    }

    public static HaloDatabaseVersionManager.Builder givenADatabaseVersionManagerBuilder(){
        return HaloDatabaseVersionManager.builder()
                .add(givenDatabaseMigrations());
    }

    public static HaloDatabaseMigration givenDatabaseMigrations(){
        return new HaloDatabaseMigration() {
            public int VERSION = 3;

            @Override
            public void updateDatabase(@NonNull SQLiteDatabase database) {
                Create.table(HaloManagerContractInstrument.HaloTableContentTest.class).on(database, "Create halo table");
                database.setVersion(4);
                VERSION = database.getVersion();
            }

            @Override
            public int getDatabaseVersion() {
                return VERSION;
            }
        };
    }

    public static HaloDatabaseErrorHandler givenAErrorHandler(){
        HaloDatabaseErrorHandler errorHandler =  new HaloDatabaseErrorHandler();
        return new HaloDatabaseErrorHandler();
    }

    public static HaloDataLite.HaloDataLiteTransaction givenATransactionCallback(final CallbackFlag flag){
        return new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                flag.flagExecuted();
            }
        };
    }

    public static HaloDataLite.HaloDataLiteTransaction givenATransactionCallbackException(final CallbackFlag flag,final HaloDataLite haloDataLite){
        return new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException{
                haloDataLite.attachDatabase(givenADatabaseName(),givenAAliasName());
            }
        };
    }
}
