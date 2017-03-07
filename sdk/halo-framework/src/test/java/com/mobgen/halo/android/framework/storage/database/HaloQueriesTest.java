package com.mobgen.halo.android.framework.storage.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mobgen.halo.android.framework.mock.instrumentation.HaloManagerContractInstrument;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Create;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Delete;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Drop;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Select;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenAHaloDataLite;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenATransactionCallbackDelete;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenATransactionCallbackDrop;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenATransactionCallbackSelect;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloQueriesTest extends HaloRobolectricTest {

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
    public void thatCreateAIndex(){
        SQLiteDatabase database =  mHaloDatabase.getDatabase();
        Create.IndexSyntax indexSyntax = Create.index(HaloManagerContractInstrument.HaloTableContentTest.class,"haloIndexTest",new String[]{HaloManagerContractInstrument.HaloTableContentTest.halo});
        indexSyntax.on(database,"Create a index");
        Cursor cursor = database.rawQuery("SELECT * FROM sqlite_master WHERE name = ?;",new String[]{"haloIndexTest"});
        cursor.moveToFirst();
        assertThat(cursor).isNotNull();
        assertThat(cursor.getString(cursor.getColumnIndex("name"))).isEqualTo("haloIndexTest");
    }

    @Test
    public void thatSelectDslWork() throws HaloStorageGeneralException {
        HaloDataLite.HaloDataLiteTransaction transactionCallback = givenATransactionCallbackSelect(mCallbackFlag);
        mHaloDatabase.transaction(transactionCallback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatSelectDslWorkWithoutTransactionCallback() throws HaloStorageGeneralException {
        Cursor cursor = Select.all().from(HaloManagerContractInstrument
                .HaloTableContentTest.class)
                .on(mHaloDatabase,"Select instance");
        cursor.moveToFirst();
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(0);
    }

    @Test
    public void thatDeleteDslWork() throws HaloStorageGeneralException {
        HaloDataLite.HaloDataLiteTransaction transactionCallback = givenATransactionCallbackDelete(mCallbackFlag);
        mHaloDatabase.transaction(transactionCallback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatDeleteDslWorkWithoutTransactionCallback() throws HaloStorageGeneralException {
        SQLiteDatabase database = mHaloDatabase.getDatabase();
        database.execSQL("INSERT INTO halotable VALUES(1,'halo1',500,1,null)");
        database.execSQL("INSERT INTO halotable VALUES(2,'halo2',501,2,null)");
        Delete.from(HaloManagerContractInstrument
                .HaloTableContentTest.class)
                .where(HaloManagerContractInstrument.HaloTableContentTest.halo)
                .eq("halo2")
                .on(mHaloDatabase,"");
        Cursor cursor = Select.all().from(HaloManagerContractInstrument
                .HaloTableContentTest.class)
                .where(HaloManagerContractInstrument.HaloTableContentTest.halo)
                .eq("halo1")
                .on(mHaloDatabase,"Select query");
        cursor.moveToFirst();
        assertThat(cursor).isNotNull();
        assertThat(cursor.getString(0)).isEqualTo("1");
    }

    @Test
    public void thatDeleteDslWorkWithoutTransactionCallbackAndOhterOperators() throws HaloStorageGeneralException {
        SQLiteDatabase database = mHaloDatabase.getDatabase();
        database.execSQL("INSERT INTO halotable VALUES(1,'halo1',500,1,null)");
        database.execSQL("INSERT INTO halotable VALUES(2,'halo2',501,2,null)");
        Delete.from(HaloManagerContractInstrument
                .HaloTableContentTest.class)
                .where(HaloManagerContractInstrument.HaloTableContentTest.halo)
                    .neq("halo2")
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                    .between(400,600)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                    .gt(100)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                    .is(500)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                    .gte(500)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                    .lt(900)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                    .lte(501)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_data)
                    .is(null)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo)
                    .isNot("halo2")
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_data)
                    .in(new Integer [] {500})
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_data)
                    .notIn(new Integer [] {501})
                .or(HaloManagerContractInstrument.HaloTableContentTest.id)
                    .like(1)
                .on(mHaloDatabase,"Delete query");
        Cursor cursor = Select.all().from(HaloManagerContractInstrument
                .HaloTableContentTest.class)
                .where(HaloManagerContractInstrument.HaloTableContentTest.halo)
                .eq("halo2")
                .on(mHaloDatabase,"Select query");
        cursor.moveToFirst();
        assertThat(cursor).isNotNull();
        assertThat(cursor.getString(0)).isEqualTo("2");
    }

    @Test
    public void thatSelectDslWorkWithoutTransactionCallbackAndOhterOperators() throws HaloStorageGeneralException {
        SQLiteDatabase database = mHaloDatabase.getDatabase();
        database.execSQL("INSERT INTO halotable VALUES(1,'halo1',500,1,null)");
        database.execSQL("INSERT INTO halotable VALUES(2,'halo2',501,2,null)");
        Cursor cursor = Select.all().from(HaloManagerContractInstrument
                .HaloTableContentTest.class)
                .where(HaloManagerContractInstrument.HaloTableContentTest.halo)
                .neq("halo2")
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                .between(400,600)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                .gt(100)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                .is(500)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                .gte(500)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                .lt(900)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_ref)
                .lte(501)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_data)
                .is(null)
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo)
                .isNot("halo2")
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_data)
                .in(new Integer [] {500})
                .and(HaloManagerContractInstrument.HaloTableContentTest.halo_data)
                .notIn(new Integer [] {501})
                .or(HaloManagerContractInstrument.HaloTableContentTest.id)
                .like(1)
                .on(mHaloDatabase,"Select query");
        cursor.moveToFirst();
        assertThat(cursor).isNotNull();
        assertThat(cursor.getString(0)).isEqualTo("1");
    }

    @Test(expected=HaloStorageGeneralException.class)
    public void thatDropDslWork() throws HaloStorageGeneralException {
        HaloDataLite.HaloDataLiteTransaction transactionCallback = givenATransactionCallbackDrop(mCallbackFlag);
        mHaloDatabase.transaction(transactionCallback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test(expected=SQLException.class)
    public void thatDropTableWorkWithoutTransactionCallback(){
        SQLiteDatabase database = mHaloDatabase.getDatabase();
        Create.table(HaloManagerContractInstrument
                .HaloTableContentTest.class).on(database,"create table");
        Drop.table(HaloManagerContractInstrument
                .HaloTableContentTest.class).on(database,"drop table");
        database.rawQuery("SELECT * FROM halotable",new String[]{null});
    }
}
