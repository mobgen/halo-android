package com.mobgen.halo.android.framework.mock.instrumentation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseErrorHandler;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseMigration;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseVersionManager;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Delete;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Drop;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Select;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.testing.CallbackFlag;

import org.robolectric.RuntimeEnvironment;

import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloDatabaseInstrument {

    public static String givenADatabaseName(){
        return "halo-test-name";
    }

    public static String givenADatabaseName(String databaseName){
        return databaseName;
    }

    public static String givenAAliasName(){
        return "halo-test-alias";
    }

    public static String givenAAliasName(String databseAlias){
        return databseAlias;
    }

    public static HaloDataLite givenAHaloDataLite(){
        return HaloDataLite.builder(RuntimeEnvironment.application)
                .setErrorHandler(givenAErrorHandler())
                .setDatabaseVersion(givenDatabaseMigrations().getDatabaseVersion())
                .setDatabaseName(givenADatabaseName())
                .setVersionManager(givenADatabaseVersionManager())
                .build();
    }

    public static HaloDataLite givenAHaloDataLite(String databaseName){
        return HaloDataLite.builder(RuntimeEnvironment.application)
                .setErrorHandler(givenAErrorHandler())
                .setDatabaseVersion(givenDatabaseMigrations().getDatabaseVersion())
                .setDatabaseName(givenADatabaseName(databaseName))
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

    public static HaloDataLite.HaloDataLiteTransaction givenATransactionCallbackSelect(final CallbackFlag flag){
        return new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                flag.flagExecuted();
                Cursor cursor = Select.all().from(HaloManagerContractInstrument
                        .HaloTableContentTest.class)
                        .on(database,"Select query");
                Cursor cursor2 = Select.columns(HaloManagerContractInstrument.HaloTableContentTest.halo)
                        .from(HaloManagerContractInstrument.HaloTableContentTest.class)
                        .where(HaloManagerContractInstrument.HaloTableContentTest.halo)
                        .eq(0)
                        .on(database,"Select query");
                assertThat(cursor).isNotNull();
                assertThat(cursor.getCount()).isEqualTo(0);
                assertThat(cursor2).isNotNull();
                assertThat(cursor2.getCount()).isEqualTo(0);
            }
        };
    }

    public static HaloDataLite.HaloDataLiteTransaction givenATransactionCallbackDelete(final CallbackFlag flag){
        return new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                flag.flagExecuted();
                database.execSQL("INSERT INTO halotable VALUES(1,'halo1',500,1,null)");
                database.execSQL("INSERT INTO halotable VALUES(2,'halo2',501,2,null)");
                Delete.from(HaloManagerContractInstrument
                        .HaloTableContentTest.class)
                        .where(HaloManagerContractInstrument.HaloTableContentTest.halo)
                        .eq("halo2")
                        .on(database,"");
                Cursor cursor = Select.all().from(HaloManagerContractInstrument
                        .HaloTableContentTest.class)
                        .where(HaloManagerContractInstrument.HaloTableContentTest.halo)
                        .eq("halo1")
                        .on(database,"Select query");
                cursor.moveToFirst();
                assertThat(cursor).isNotNull();
                assertThat(cursor.getString(0)).isEqualTo("1");
            }
        };
    }

    public static HaloDataLite.HaloDataLiteTransaction givenATransactionCallbackDrop(final CallbackFlag flag){
        return new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                flag.flagExecuted();
                Create.table(HaloManagerContractInstrument
                        .HaloTableContentTest.class).on(database,"create table");
                Drop.table(HaloManagerContractInstrument
                        .HaloTableContentTest.class).on(database,"drop table");
                database.rawQuery("SELECT * FROM halotable",new String[]{null});
            }
        };
    }
}
