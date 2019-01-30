package com.mobgen.halo.android.sdk.core.management.models;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.mock.HaloMock;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloManagerApiInstrument.givenAHaloEvent;
import static org.assertj.core.api.Java6Assertions.assertThat;
public class HaloEventTest extends HaloRobolectricTest {

    private static Halo mHalo;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = HaloMock.create("");
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatAParcelOperationKeepsTheSameDataWithBuilder() throws JSONException {
        HaloEvent event = givenAHaloEvent();
        HaloEvent parcelInstance = TestUtils.testParcel(event, HaloEvent.CREATOR);
        assertThat(event.getCoord()).isEqualTo(parcelInstance.getCoord());
        assertThat(event.getIp()).isNull();
        assertThat(event.getExtra().get("prevRoom")).isEqualTo(parcelInstance.getExtra().get("prevRoom"));
        assertThat(event.getType()).isEqualTo(parcelInstance.getType());
        assertThat(event.getCategory()).isNull();

    }

    @Test
    public void thatCanSerializeObject() throws JSONException, HaloParsingException {
        HaloEvent event = givenAHaloEvent();
        String objectSerialized = HaloEvent.serialize(event, mHalo.framework().parser());
        JSONObject jsonObject = new JSONObject(objectSerialized);
        assertThat(jsonObject.get("coord")).isEqualTo("3,3");
    }

}
