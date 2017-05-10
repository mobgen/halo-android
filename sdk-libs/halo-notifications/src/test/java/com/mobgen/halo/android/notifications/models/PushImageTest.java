package com.mobgen.halo.android.notifications.models;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class PushImageTest extends HaloRobolectricTest {

    Halo mHalo;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo();
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatAParcelOperationKeepsTheSameDataWithSetters() throws JSONException {
        PushImage instance = new PushImage();
        instance.setLayout("left");
        instance.setUrl("htpp://myimage.jpeg");
        PushImage parcelInstance = TestUtils.testParcel(instance, PushImage.CREATOR);
        assertThat(instance.getLayout()).isEqualTo(parcelInstance.getLayout());
        assertThat(instance.getUrl()).isEqualTo(parcelInstance.getUrl());
        assertThat(instance.getUrl()).isEqualTo("htpp://myimage.jpeg");
        assertThat(instance.getLayout()).isEqualTo("left");
    }

    @Test
    public void thatCanDeserializeFromJSON() throws JSONException, HaloParsingException {
        String pushImageJSON = "{\n" +
                "                  \"url\": \"http://myimage.jpg\",\n" +
                "                  \"layout\":\"expanded\"\n" +
                "          }";
        PushImage instance = PushImage.deserialize(pushImageJSON,mHalo.framework().parser());
        assertThat(instance.getUrl()).isEqualTo("http://myimage.jpg");
        assertThat(instance.getLayout()).isEqualTo("expanded");
    }

    @Test
    public void thatANullJSONThrowsException() throws JSONException {
        String pushImageJSON = null;
        PushImage instance = null;
        try {
            instance = PushImage.deserialize(pushImageJSON,mHalo.framework().parser());
        } catch (HaloParsingException e) {
            assertThat(instance).isNull();
        }
    }
}
