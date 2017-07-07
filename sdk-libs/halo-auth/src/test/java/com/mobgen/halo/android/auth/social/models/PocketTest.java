package com.mobgen.halo.android.auth.social.models;

import com.mobgen.halo.android.auth.mock.instrumentation.UserDummy;
import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.mobgen.halo.android.auth.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by f.souto.gonzalez on 22/06/2017.
 */

public class PocketTest extends HaloRobolectricTest {
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
    public void thatCanCreateAPocket() {
        UserDummy userDummy = new UserDummy("132224", "My user", new Date(), "This is my contennt", "htpp://google.com");
        ReferenceContainer referenceContainer = new ReferenceContainer("mycollection", null);
        Pocket pocket = new Pocket.Builder()
                .withData(userDummy)
                .withReferences(referenceContainer)
                .build();
        UserDummy userDummyConverted = pocket.getValues(UserDummy.class);

        assertThat(pocket).isNotNull();
        assertThat(pocket.getReferences().size()).isEqualTo(1);
        assertThat(pocket.getValues()).isNotNull();
        assertThat(userDummyConverted.getId()).isEqualTo(userDummy.getId());
    }

    @Test
    public void thatCheckParcelPocket() {
        UserDummy userDummy = new UserDummy("132224", "My user", new Date(), "This is my contennt", "htpp://google.com");
        ReferenceContainer referenceContainer = new ReferenceContainer("mycollection", null);
        Pocket pocket = new Pocket.Builder()
                .withData(userDummy)
                .withReferences(referenceContainer)
                .build();
        Pocket newPocket = TestUtils.testParcel(pocket, Pocket.CREATOR);
        assertThat(pocket.describeContents()).isEqualTo(0);
        assertThat(pocket.getReferences().size()).isEqualTo(newPocket.getReferences().size());
        assertThat(pocket.getValues().toString()).isEqualTo(newPocket.getValues().toString());
    }
}
