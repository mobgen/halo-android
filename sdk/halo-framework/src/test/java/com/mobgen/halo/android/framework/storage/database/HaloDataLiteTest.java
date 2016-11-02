package com.mobgen.halo.android.framework.storage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenAAliasName;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenAHaloDataLiteBuilder;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenAHaloDataLite;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenADatabaseName;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenATransactionCallback;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenATransactionCallbackException;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenDatabaseMigrations;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenADatabaseVersionManagerBuilder;
import static org.assertj.core.api.Java6Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

public class HaloDataLiteTest extends HaloRobolectricTest {

    private HaloDataLite mHaloDatabase;
    private CallbackFlag mCallbackFlag;
    private Context mContext;

    @Before
    public void initialize() {
        mHaloDatabase = givenAHaloDataLite();
        mCallbackFlag =  new CallbackFlag();
    }

    @After
    public void tearDown() {
        mHaloDatabase.deleteDatabase();
    }

    @Test
    public void thatCanGetDatabaseSQL(){
        assertThat(mHaloDatabase.getDatabase().getVersion()).isEqualTo(3);
        assertThat(mHaloDatabase.getReadableDatabase().getVersion()).isEqualTo(3);
        assertThat(mHaloDatabase.getWritableDatabase().getVersion()).isEqualTo(3);
    }

    @Test
    public void thatDatabaseIsInitialized(){
        mHaloDatabase.ensureInitialized();
        assertThat(RuntimeEnvironment.application.getDatabasePath(givenADatabaseName())).isNotNull();
        assertThat(mHaloDatabase.getDatabase()).isNotNull();
    }

    @Test
    public void thatCannotInitializeWithoutDatabaseName(){
        HaloDataLite haloDataLite=null;
        try{
            haloDataLite = givenAHaloDataLiteBuilder().setDatabaseName(null).build();
        }
        catch(HaloConfigurationException haloConfigurationException){
            assertThat(haloDataLite).isNull();
        }
    }

    @Test
    public void thatCannotInitializeWithoutVersion(){
        HaloDataLite haloDataLite=null;
        try{
            haloDataLite = givenAHaloDataLiteBuilder().setDatabaseVersion(-1).build();
        }
        catch(HaloConfigurationException haloConfigurationException){
            assertThat(haloDataLite).isNull();
        }
    }

    @Test
    public void thatCannotInitializeWithoutVersionManager(){
        HaloDataLite haloDataLite=null;
        try{
            haloDataLite = givenAHaloDataLiteBuilder().setVersionManager(null).build();
        }
        catch(HaloConfigurationException haloConfigurationException){
            assertThat(haloDataLite).isNull();
        }
    }

    @Test
    public void thatCannotInitializeWithoutErrorHandler(){
        HaloDataLite haloDataLite=null;
        try{
            haloDataLite = givenAHaloDataLiteBuilder().setErrorHandler(null).build();
        }
        catch(HaloConfigurationException haloConfigurationException){
            assertThat(haloDataLite).isNull();
        }
    }

    @Test
    public void thatDataBaseMigrationAreEquals(){
        HaloDatabaseMigration databaseMigration = givenDatabaseMigrations();
        HaloDatabaseMigration databaseMigrationCopy = givenDatabaseMigrations();
        assertThat(databaseMigration.compareTo(databaseMigrationCopy)).isEqualTo(0);
    }

    @Test
    public void thatCorruptionCallbackIsHandled(){
        HaloDatabaseErrorHandler errorHandler =  new HaloDatabaseErrorHandler();
        Corruptible corruptible = new Corruptible() {
            @Override
            public void onCorrupted() {
                mCallbackFlag.flagExecuted();
            }
        };
        errorHandler.setCorruptible(corruptible);
        errorHandler.onCorruption(mHaloDatabase.getDatabase());
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanCreateAVersionMaganerFromAnotherBuilder(){
        HaloDatabaseVersionManager.Builder builder = new HaloDatabaseVersionManager.Builder(givenADatabaseVersionManagerBuilder())
                .add(givenDatabaseMigrations());
        HaloDatabaseVersionManager versionManager = builder.build();
        assertThat(versionManager).isNotNull();
    }

    @Test
    public void thatDatabaseCanBeDeleted(){
        SQLiteDatabase database = mHaloDatabase.getDatabase();
        mHaloDatabase.deleteDatabase();
        assertThat(database.isOpen()).isFalse();
    }

    @Test
    public void thatDatabaseCanIsCloseAfterCorruption(){
        SQLiteDatabase database = mHaloDatabase.getDatabase();
        mHaloDatabase.onCorrupted();
        assertThat(database.isOpen()).isFalse();
    }

    @Test
    public void thatHandleTransactionCallback() throws HaloStorageGeneralException {
        HaloDataLite.HaloDataLiteTransaction transactionCallback = givenATransactionCallback(mCallbackFlag);
        mHaloDatabase.transaction(transactionCallback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatHandleTransactionException() throws HaloStorageGeneralException {
        HaloDataLite.HaloDataLiteTransaction transactionCallback = givenATransactionCallbackException(mCallbackFlag,mHaloDatabase);
        try {
            mHaloDatabase.transaction(transactionCallback);
        }
        catch (HaloStorageGeneralException haloStorageException){
            mCallbackFlag.flagExecuted();
        }
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanUpgradeDatabaseVersion(){
        mHaloDatabase.onUpgrade(mHaloDatabase.getDatabase(),2,4);
        assertThat(mHaloDatabase.getDatabase().getVersion()).isEqualTo(4);
    }

    @Test
    public void thatCanAttachADatabase(){
        SQLiteDatabase database = mHaloDatabase.getDatabase();
        HaloDataLite haloDataLite = givenAHaloDataLite("secondDatabase");
        mHaloDatabase.attachDatabase(givenADatabaseName("secondDatabase"),givenAAliasName("databaseToAttach"));
        assertThat(database.getAttachedDbs().size()).isEqualTo(2);
    }

    @Test
    public void thatCanDettachADatabase(){
        SQLiteDatabase database = mHaloDatabase.getDatabase();
        HaloDataLite haloDataLite = givenAHaloDataLite("secondDatabase");
        mHaloDatabase.attachDatabase(givenADatabaseName("secondDatabase"),givenAAliasName("databaseToAttach"));
        mHaloDatabase.detachDatabase(givenAAliasName("databaseToAttach"));
        assertThat(database.getAttachedDbs().size()).isEqualTo(1);
    }
}
