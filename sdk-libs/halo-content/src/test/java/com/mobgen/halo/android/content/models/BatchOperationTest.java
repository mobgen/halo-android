package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class BatchOperationTest extends HaloRobolectricTest {

    private static Halo mHalo;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo("");
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatAParcelOperationKeepsTheSameDataWithBuilder() throws JSONException {
        HaloContentInstance haloContentInstance = new HaloContentInstance(null,"the name","1", "dummy", null,null, null, new Date(), null, new Date(), null, null);
        BatchOperations.Builder operationsBuilder = new BatchOperations.Builder()
                .create(haloContentInstance)
                .createOrUpdate(haloContentInstance)
                .createOrUpdate(haloContentInstance);
        BatchOperations instance = operationsBuilder.build();
        BatchOperations parcelInstance = TestUtils.testParcel(instance, BatchOperations.CREATOR);
        assertThat(instance.getCreated().size()).isEqualTo(1);
        assertThat(instance.getCreatedOrUpdated().size()).isEqualTo(2);
        assertThat(instance.getDeleted()).isNull();
        assertThat(instance.getUpdated()).isNull();
    }
}
