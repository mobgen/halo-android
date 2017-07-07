package com.mobgen.halo.android.auth.social.models;

import com.mobgen.halo.android.auth.models.ReferenceFilter;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.auth.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by f.souto.gonzalez on 22/06/2017.
 */

public class ReferenceFilterTest extends HaloRobolectricTest {
    private Halo mHalo;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo("");
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatCanCreateAReferenceFilter() {
        ReferenceFilter referenceFilter = new ReferenceFilter.Builder()
                .filters("myAwesomeFilter")
                .build();

        assertThat(referenceFilter).isNotNull();
        assertThat(referenceFilter.getCurrentReferences()).contains("myAwesomeFilter");
    }

    @Test
    public void thatCheckParcelFilter() {
        ReferenceFilter referenceFilter = new ReferenceFilter.Builder()
                .filters("myAwesomeFilter")
                .build();
        ReferenceFilter newRefFilter = TestUtils.testParcel(referenceFilter, ReferenceFilter.CREATOR);
        assertThat(referenceFilter.getCurrentReferences()).isEqualTo(newRefFilter.getCurrentReferences());
    }
}
