package com.mobgen.halo.android.sdk.core.management.models;

import com.mobgen.halo.android.testing.HaloJUnitTest;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloServerVersionTest extends HaloJUnitTest {

    @Test
    public void thatConstructAEmptyObject() {
        HaloServerVersion version = new HaloServerVersion();
        assertThat(version.getHaloVersion()).isNull();
        assertThat(version.getChangeLogUrl()).isNull();
    }

    @Test
    public void thatConstructorAObject() {
        HaloServerVersion version = new HaloServerVersion("changelog", "1.0");
        assertThat(version.getChangeLogUrl()).isEqualTo("changelog");
        assertThat(version.getHaloVersion()).isEqualTo("1.0");
    }

    @Test
    public void thatVersionAreEquals() {
        HaloServerVersion version = new HaloServerVersion("changelog", "1.1");
        HaloServerVersion version2 = new HaloServerVersion("changelog", "1.1");
        assertThat(version2).isEqualTo(version);
        HaloServerVersion version3 = new HaloServerVersion("changelog2", "1.1");
        assertThat(version3).isNotEqualTo(version2);
        HaloServerVersion version4 = new HaloServerVersion("changelog", "1");
        assertThat(version3).isNotEqualTo(version4);
        assertThat(1).isNotEqualTo(version);
        assertThat(version).isNotNull();
        assertThat(new HaloServerVersion(null, "1.1")).isNotEqualTo(version);
        assertThat(version).isNotEqualTo(new HaloServerVersion(null, "1.1"));
        assertThat(new HaloServerVersion(null, null)).isNotEqualTo(version);
        assertThat(new HaloServerVersion("changelog", null)).isNotEqualTo(version);
        assertThat(version).isNotEqualTo(new HaloServerVersion("changelog", null));
        assertThat(version).isEqualTo(version);
        assertThat(version2.hashCode()).isEqualTo(version.hashCode());
        assertThat(new HaloServerVersion(null, null).hashCode() == 0).isTrue();
        assertThat(new HaloServerVersion(null, null).hashCode()).isNotEqualTo(version.hashCode());
        assertThat(version3.hashCode()).isNotEqualTo(version.hashCode());
    }

    @Test
    public void thatVersionIsOutdated() {
        HaloServerVersion version = new HaloServerVersion("changelog", "1.0");
        HaloServerVersion newVersion = new HaloServerVersion("changelog", "1.1");
        assertThat(version.isOutdated("1.1")).isFalse();
        assertThat(version.isOutdated("1.0")).isFalse();
        assertThat(version.isOutdated("1.0.1")).isFalse();
        assertThat(version.isOutdated("0.9")).isTrue();
        assertThat(version.isOutdated("0")).isTrue();
        assertThat(version.isOutdated("0.5.3")).isTrue();
        assertThat(newVersion.isOutdated("1")).isTrue();
    }
}
