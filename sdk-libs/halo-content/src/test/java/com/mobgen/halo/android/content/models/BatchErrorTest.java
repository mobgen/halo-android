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

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class BatchErrorTest extends HaloRobolectricTest {

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
    public void thatErrorIsParcelable() throws JSONException {
        BatchOperationResult instance = new BatchOperationResult(BatchOperator.DELETE, 0, false, new JSONObject("{\n" +
                "        \"error\": {\n" +
                "          \"status\": 403,\n" +
                "          \"message\": \"You can not update an instance of a module that does not belong to you or does not exist\",\n" +
                "          \"extra\": {},\n" +
                "          \"type\": \"HaloError\"\n" +
                "        }\n" +
                "      }"));
        BatchError error = instance.getDataError();
        BatchError parcelInstance = TestUtils.testParcel(error, BatchError.CREATOR);
        assertThat(error.getError().mMessage).isEqualTo(parcelInstance.getError().mMessage);
    }


    @Test
    public void thatCanReachErrorInfoData() throws JSONException {
        JSONObject errorExtra = new JSONObject("{\n" +
                "        \"error\": {\n" +
                "          \"status\": 403,\n" +
                "          \"message\": \"You can not update an instance of a module that does not belong to you or does not exist\",\n" +
                "          \"extra\": {\n" +
                "              \"field\": \"amount\",\n" +
                "              \"error\": \"validator.8\",\n" +
                "              \"message\": \"Please check the text field, it should be a string\"\n" +
                "            },\n" +
                "          \"type\": \"HaloError\"\n" +
                "        }\n" +
                "      }");
        BatchOperationResult instance = new BatchOperationResult(BatchOperator.DELETE, 0, false, errorExtra);
        BatchError error = instance.getDataError();
        BatchErrorInfo errorInfo = error.getError();

        assertThat(errorInfo.getStatus()).isEqualTo(403);
        assertThat(errorInfo.getType()).isEqualTo("HaloError");
        assertThat(errorInfo.getMessage()).contains("belong to you or does not exist");
        assertThat(errorInfo.getExtra().toString()).contains("validator.8");
    }
}
