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
import java.util.ArrayList;
import java.util.List;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.in;

public class BatchOperationResultTest extends HaloRobolectricTest {

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
    public void thatAParcelOperationKeepsTheSameDataWithConstructor() throws JSONException {
        BatchOperationResult instance = new BatchOperationResult(BatchOperator.TRUNCATE, 0, true, new JSONArray("[\n" +
                "        {\n" +
                "          \"id\": \"58f73971af013400107b93c4\"\n" +
                "        }\n" +
                "      ]"));
        BatchOperationResult parcelInstance = TestUtils.testParcel(instance, BatchOperationResult.CREATOR);
        assertThat(instance.getRawData()).isEqualTo(parcelInstance.getRawData());
        assertThat(instance.getDataTruncate().get(0).getItemId()).isEqualTo("58f73971af013400107b93c4");
    }

    @Test
    public void thatCanReadSuccessData() throws JSONException {
        BatchOperationResult instance = new BatchOperationResult(BatchOperator.CREATE, 0, true, new JSONObject("{\n" +
                "        \"name\": \"From Android SDK\",\n" +
                "        \"module\": \"586a47f836a6b01300ec9f00\",\n" +
                "        \"tags\": [],\n" +
                "        \"archivedAt\": null,\n" +
                "        \"removedAt\": null,\n" +
                "        \"publishedAt\": null,\n" +
                "        \"createdBy\": \"Editor Editor\",\n" +
                "        \"customerId\": 1,\n" +
                "        \"values\": {},\n" +
                "        \"pushSchedule\": null,\n" +
                "        \"externalId\": null,\n" +
                "        \"revision\": 0,\n" +
                "        \"createdAt\": 1492610860696,\n" +
                "        \"updatedAt\": null,\n" +
                "        \"deletedAt\": null,\n" +
                "        \"updatedBy\": null,\n" +
                "        \"deletedBy\": null,\n" +
                "        \"id\": \"5874c5f06a3a0d1e00c8039d\"\n" +
                "      }"));
        BatchOperationResult parcelInstance = TestUtils.testParcel(instance, BatchOperationResult.CREATOR);
        assertThat(instance.getData().getItemId()).isEqualTo(parcelInstance.getData().getItemId());
        assertThat(instance.getData().getItemId()).isEqualTo("5874c5f06a3a0d1e00c8039d");
    }

    @Test
    public void thatCanReadErrorData() throws JSONException {
        BatchOperationResult instance = new BatchOperationResult(BatchOperator.DELETE, 0, false, new JSONObject("{\n" +
                "        \"error\": {\n" +
                "          \"status\": 403,\n" +
                "          \"message\": \"You can not update an instance of a module that does not belong to you or does not exist\",\n" +
                "          \"extra\": {},\n" +
                "          \"type\": \"HaloError\"\n" +
                "        }\n" +
                "      }"));
        BatchOperationResult parcelInstance = TestUtils.testParcel(instance, BatchOperationResult.CREATOR);
        assertThat(instance.getDataError().getError().mMessage).isEqualTo(parcelInstance.getDataError().getError().mMessage);
        assertThat(instance.getPosition()).isEqualTo(0);
    }
}
