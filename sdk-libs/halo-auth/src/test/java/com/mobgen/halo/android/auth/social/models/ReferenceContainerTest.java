package com.mobgen.halo.android.auth.social.models;

import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.auth.models.ReferenceFilter;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mobgen.halo.android.auth.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by f.souto.gonzalez on 22/06/2017.
 */

public class ReferenceContainerTest extends HaloRobolectricTest {
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
    public void thatCanCreateAReferenceContainer() {
        ReferenceContainer referenceContainer = new ReferenceContainer.Builder("referenceName")
                .references("myAwesomeContent")
                .build();

        assertThat(referenceContainer).isNotNull();
        assertThat(referenceContainer.getName()).contains("referenceName");
        assertThat(referenceContainer.getReferences().get(0)).contains("myAwesomeContent");
    }

    @Test
    public void thatCanCreateAReferenceContainerWithBuilder() {
        ReferenceContainer referenceContainerBuilder = new ReferenceContainer.Builder("referenceName")
                .references("myAwesomeContent")
                .build();
        List<String> myRefs = new ArrayList<>();
        myRefs.add("myAwesomeContent");
        ReferenceContainer referenceContainer = new ReferenceContainer("referenceName", myRefs);
        assertThat(referenceContainer).isNotNull();
        assertThat(referenceContainer.getName()).isEqualTo(referenceContainerBuilder.getName());
        assertThat(referenceContainer.getReferences().get(0)).isEqualTo(referenceContainerBuilder.getReferences().get(0));
    }

    @Test
    public void thatCheckParcelReferenceContainer() {
        ReferenceContainer referenceContainerBuilder = new ReferenceContainer.Builder("referenceName")
                .references("myAwesomeContent")
                .build();
        ReferenceContainer newRefContainer = TestUtils.testParcel(referenceContainerBuilder, ReferenceContainer.CREATOR);
        assertThat(referenceContainerBuilder.getName()).isEqualTo(newRefContainer.getName());
        assertThat(referenceContainerBuilder.getReferences().get(0)).isEqualTo(newRefContainer.getReferences().get(0));
    }
}
