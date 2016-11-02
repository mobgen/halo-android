package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloSyncLogTest extends HaloRobolectricTest {

    @Test
    public void thatAParcelSyncLogContainsTheSameInfo() {
        Date now = new Date();
        HaloSyncLog log = HaloSyncLog.create("fakeModuleId", HaloLocale.ABKHAZIAN, now, 10, 10, 10);
        HaloSyncLog parcelLog = TestUtils.testParcel(log, HaloSyncLog.CREATOR);
        assertThat(log.getModuleName()).isEqualTo(parcelLog.getModuleName());
        assertThat(log.getLocale()).isEqualTo(parcelLog.getLocale());
        assertThat(log.getSyncDate()).isEqualTo(parcelLog.getSyncDate());
        assertThat(log.getCreations()).isEqualTo(parcelLog.getCreations());
        assertThat(log.getUpdates()).isEqualTo(parcelLog.getUpdates());
        assertThat(log.getDeletions()).isEqualTo(parcelLog.getDeletions());
        assertThat(log.getModifiedEntries()).isEqualTo(parcelLog.getModifiedEntries());
        assertThat(log.describeContents()).isEqualTo(0);

    }

    @Test
    public void thatModificationsAreCorrectlyMarked() {
        Date now = new Date();
        HaloSyncLog log = HaloSyncLog.create("fakeModuleId", HaloLocale.ABKHAZIAN, now, 10, 10, 10);
        assertThat(log.didSomethingChange()).isTrue();
        assertThat(log.getModifiedEntries()).isEqualTo(30);
        HaloSyncLog unmodified = HaloSyncLog.create("fakeModuleId", HaloLocale.ABKHAZIAN, now, 0, 0, 0);
        assertThat(unmodified.didSomethingChange()).isFalse();
        assertThat(unmodified.getModifiedEntries()).isEqualTo(0);
    }
}
