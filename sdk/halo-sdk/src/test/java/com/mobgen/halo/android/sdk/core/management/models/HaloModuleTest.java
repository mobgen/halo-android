package com.mobgen.halo.android.sdk.core.management.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockCursor;
import com.mobgen.halo.android.testing.TestUtils;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static com.mobgen.halo.android.sdk.core.internal.storage.HaloManagerContract.RemoteModules;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloModuleTest extends HaloRobolectricTest {

    private HaloModule mModule;
    private Date mUpdatedAt;
    private Date mCreatedAt;

    @Before
    public void initialize() {
        mUpdatedAt = new Date();
        mCreatedAt = new Date();
        mModule = new HaloModule(1, "1", "myModule", true, mCreatedAt, mUpdatedAt, "internalId", false, new JSONArray());
    }

    private void assertModule(HaloModule module) {
        assertThat(module.getId()).isEqualTo("1");
        assertThat(module.getCustomerId()).isEqualTo(Integer.valueOf(1));
        assertThat(module.getName()).isEqualTo("myModule");
        assertThat(module.isEnabled()).isEqualTo(true);
        assertThat(module.getCreationDate()).isEqualTo(mCreatedAt);
        assertThat(module.getLastUpdate()).isEqualTo(mUpdatedAt);
        assertThat(module.getInternalId()).isEqualTo("internalId");
        assertThat(module.isSingleItemInstance()).isEqualTo(false);
    }

    private void assertCursorModule(HaloModule module, Date creationDate, Date updateDate) {
        assertThat(module.getName()).isEqualTo("myModule");
        assertThat(module.getId()).isEqualTo("1");
        assertThat(module.getInternalId()).isEqualTo("myInternalId");
        assertThat(module.getCreationDate()).isEqualTo(creationDate);
        assertThat(module.getLastUpdate()).isEqualTo(updateDate);
        assertThat(module.isSingleItemInstance()).isTrue();
        assertThat(module.getCustomerId()).isEqualTo(Integer.valueOf(30));
        assertThat(module.isEnabled()).isTrue();
    }

    private Cursor createCursor(int times, Date creationDate, Date updateDate) {
        MockCursor.Builder builder = MockCursor.builder()
                .on(RemoteModules.NAME, "myModule")
                .on(RemoteModules.ID, "1")
                .on(RemoteModules.INTERNAL_ID, "myInternalId")
                .on(RemoteModules.CREATED_AT, creationDate)
                .on(RemoteModules.UPDATED_AT, updateDate)
                .on(RemoteModules.SINGLE, true)
                .on(RemoteModules.CUSTOMER, 30)
                .on(RemoteModules.ENABLED, true);
        if (times > 0) {
            builder.replicate(times);
        }
        return builder.build();
    }

    @Test
    public void thatParcelableCreatorIsOk() {
        HaloModule module = TestUtils.testParcel(mModule, HaloModule.CREATOR);
        assertModule(module);
        assertThat(module.describeContents()).isEqualTo(0);
    }

    @Test
    public void thatShallowConstructor() throws Exception {
        assertThat(new HaloModule()).isNotNull();
    }

    @Test
    public void thatContentValuesAreOk() {
        ContentValues values = mModule.getContentValues();
        assertThat(values.getAsInteger(RemoteModules.ID)).isEqualTo(Integer.valueOf(1));
        assertThat(values.getAsInteger(RemoteModules.CUSTOMER)).isEqualTo(Integer.valueOf(1));
        assertThat(values.getAsString(RemoteModules.NAME)).isEqualTo("myModule");
        assertThat(values.getAsBoolean(RemoteModules.ENABLED)).isEqualTo(true);
        assertThat(values.getAsLong(RemoteModules.CREATED_AT)).isEqualTo(Long.valueOf(mCreatedAt.getTime()));
        assertThat(values.getAsLong(RemoteModules.UPDATED_AT)).isEqualTo(Long.valueOf(mUpdatedAt.getTime()));
        assertThat(values.getAsString(RemoteModules.INTERNAL_ID)).isEqualTo("internalId");
        assertThat(values.getAsBoolean(RemoteModules.SINGLE)).isEqualTo(false);
    }

    @Test
    public void thatCreateFromCursor() {
        Date createDate = new Date();
        Date updateDate = new Date();
        HaloModule module = HaloModule.create(createCursor(0, createDate, updateDate));
        assertCursorModule(module, createDate, updateDate);
    }

    @Test
    public void thatCreateListFromCursor() {
        Date createDate = new Date();
        Date updateDate = new Date();
        Cursor cursor = createCursor(5, createDate, updateDate);
        List<HaloModule> modules = HaloModule.fromCursor(cursor, false);
        assertThat(modules.size()).isEqualTo(5);
        for (HaloModule module : modules) {
            assertCursorModule(module, createDate, updateDate);
        }
        assertThat(cursor.isClosed()).isFalse();

        cursor = createCursor(5, createDate, updateDate);
        modules = HaloModule.fromCursor(cursor, true);
        assertThat(modules.size()).isEqualTo(5);
        for (HaloModule module : modules) {
            assertCursorModule(module, createDate, updateDate);
        }
        assertThat(cursor.isClosed()).isTrue();
    }

    @Test
    public void thatPrintObjectToString() {
        assertThat(mModule.toString()).isNotNull();
    }

}
