package com.mobgen.halo.android.sdk.core.internal.storage;

import android.database.sqlite.SQLiteDatabase;

import com.mobgen.halo.android.testing.HaloJUnitTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class HaloMigration2$0$0Test extends HaloJUnitTest {

    private HaloMigration2$0$0 mMigration;

    @Before
    public void initialize() {
        mMigration = new HaloMigration2$0$0();
    }

    @Test
    public void thatEnsureVersionIsFirst() {
        assertThat(mMigration.getDatabaseVersion() == 1).isTrue();
    }

    @Test
    public void thatEnsureCreateTables() {
        SQLiteDatabase database = mock(SQLiteDatabase.class);
        final int[] numOps = {0};
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                numOps[0]++;
                return null;
            }
        }).when(database).execSQL(any(String.class));
        mMigration.updateDatabase(database);
        assertThat(numOps[0]).isEqualTo(1);
    }
}
