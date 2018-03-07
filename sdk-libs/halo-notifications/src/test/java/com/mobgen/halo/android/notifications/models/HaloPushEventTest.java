package com.mobgen.halo.android.notifications.models;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.notifications.events.NotificationEventsActions;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationActionsInstruments.givenAPushEvent;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by f.souto.gonzalez on 23/01/2018.
 */

public class HaloPushEventTest extends HaloRobolectricTest {

    Halo mHalo;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo("urlEndpoint");
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatAParcelOperationKeepsTheSameDataWithBuilder() throws JSONException {
        HaloPushEvent instance = givenAPushEvent(NotificationEventsActions.PUSH_RECEIPT);
        HaloPushEvent parcelInstance = TestUtils.testParcel(instance, HaloPushEvent.CREATOR);
        assertThat(instance.getAction()).isEqualTo(parcelInstance.getAction());
        assertThat(instance.getDevice()).isEqualTo(parcelInstance.getDevice());
        assertThat(instance.getSchedule()).isEqualTo(parcelInstance.getSchedule());
        assertThat(instance.getAction()).isEqualTo(NotificationEventsActions.PUSH_RECEIPT);
    }

    @Test
    public void thatAParcelOperationKeepsTheSameDataWithContructor() throws JSONException {
        HaloPushEvent instance = new HaloPushEvent("my awesome alias","shecudleID",NotificationEventsActions.PUSH_OPEN);
        HaloPushEvent parcelInstance = TestUtils.testParcel(instance, HaloPushEvent.CREATOR);
        assertThat(instance.getAction()).isEqualTo(parcelInstance.getAction());
        assertThat(instance.getDevice()).isEqualTo(parcelInstance.getDevice());
        assertThat(instance.getSchedule()).isEqualTo(parcelInstance.getSchedule());
        assertThat(instance.getAction()).isEqualTo(NotificationEventsActions.PUSH_OPEN);
    }


}
