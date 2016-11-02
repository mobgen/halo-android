//package com.mobgen.halo.android.sdk.core.internal.storage;
//
//import android.database.Cursor;
//
//import com.mobgen.halo.android.framework.api.HaloConfig;
//import com.mobgen.halo.android.framework.api.HaloFramework;
//import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;
//import com.mobgen.halo.android.sdk.content.storage.HaloContentContract;
//import com.mobgen.halo.android.sdk.core.internal.parser.LoganSquareParserFactory;
//
//import org.junit.Test;
//import org.robolectric.RuntimeEnvironment;
//
//import static junit.framework.Assert.assertEquals;
//
///**
// * The
// */
//public class HaloMigration1$3$0Test extends AndroidRobolectricTest {
//
//    @Test
//    public void createTablesTest() {
//        HaloFramework framework = HaloFramework.create(HaloConfig.builder(RuntimeEnvironment.application)
//                .setParser(LoganSquareParserFactory.create())
//                .addDatabaseMigrations(
//                        new HaloMigration1$3$0()));
//        Cursor cursor = framework.storage().db().getDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = ? ;", new String[]{ORMUtils.getTableName(HaloContentContract.ContentSync.class)});
//        assertEquals(1, cursor.getCount());
//        cursor.close();
//        cursor = framework.storage().db().getDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = ? ;", new String[]{ORMUtils.getTableName(HaloContentContract.ContentSyncLog.class)});
//        assertEquals(1, cursor.getCount());
//        cursor.close();
//    }
//
//    @Test
//    public void versionTest() {
//        assertEquals(HaloManagerContract.CURRENT_VERSION, new HaloMigration1$3$0().getDatabaseVersion());
//    }
//}
