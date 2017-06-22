package com.mobgen.halo.android.auth.social.pocket;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.mock.instrumentation.UserDummy;
import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.auth.models.ReferenceFilter;
import com.mobgen.halo.android.auth.pocket.HaloPocketApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.mobgen.halo.android.auth.mock.fixtures.ServerFixtures.POCKET_FILTER_SUCESS;
import static com.mobgen.halo.android.auth.mock.fixtures.ServerFixtures.POCKET_SUCESS;
import static com.mobgen.halo.android.auth.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloPocketApiInstrument.givenAPocket;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloPocketApiInstrument.givenAPocketCallback;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloPocketApiInstrument.givenAPocketOnlyWithDataCallback;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloPocketApiInstrument.givenAPocketOnlyWithDataCallbackAsCustomClass;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloPocketApiInstrument.givenAReferenceCallback;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloSocialApiMock.givenASocialApiWithHalo;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by f.souto.gonzalez on 22/06/2017.
 */

public class HaloPocketApiTest extends HaloRobolectricTest {

    private MockServer mMockServer;
    private static Halo mHalo;
    private HaloAuthApi mHaloAuthApi;
    private CallbackFlag mCallbackFlag;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mMockServer = MockServer.create();
        mHalo = givenADefaultHalo(mMockServer.start());
        mHaloAuthApi = givenASocialApiWithHalo(mHalo);
        mCallbackFlag = newCallbackFlag();
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatCanGetPocketDataAsPocket() throws IOException {
        enqueueServerFile(mMockServer, POCKET_SUCESS);
        HaloPocketApi pocketApi = mHaloAuthApi.pocket();
        CallbackV2<Pocket> callback = givenAPocketCallback(mCallbackFlag, "132224", 3);
        pocketApi.get().execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanGetPocketCustomDataAsCustomClassAsPocket() throws IOException {
        enqueueServerFile(mMockServer, POCKET_SUCESS);
        HaloPocketApi pocketApi = mHaloAuthApi.pocket();
        CallbackV2<Pocket> callback = givenAPocketOnlyWithDataCallback(mCallbackFlag, "132224");
        pocketApi.getData().asPocket().execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanGetPocketCustomDataAsCustomClassAsCustomClass() throws IOException {
        enqueueServerFile(mMockServer, POCKET_SUCESS);
        HaloPocketApi pocketApi = mHaloAuthApi.pocket();
        CallbackV2<UserDummy> callback = givenAPocketOnlyWithDataCallbackAsCustomClass(mCallbackFlag, "132224");
        pocketApi.getData().asCustomData(UserDummy.class).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanGetPocketReferences() throws IOException {
        enqueueServerFile(mMockServer, POCKET_FILTER_SUCESS);
        HaloPocketApi pocketApi = mHaloAuthApi.pocket();
        CallbackV2<List<ReferenceContainer>> callback = givenAReferenceCallback(mCallbackFlag, 1);
        ReferenceFilter referenceFilter = new ReferenceFilter.Builder().filters("favorites").build();
        pocketApi.getReferences(referenceFilter).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanSaveAPocket() throws IOException {
        enqueueServerFile(mMockServer, POCKET_FILTER_SUCESS);
        HaloPocketApi pocketApi = mHaloAuthApi.pocket();
        CallbackV2<Pocket> callback = givenAPocketCallback(mCallbackFlag);
        Pocket pocket = givenAPocket();
        pocketApi.save(pocket).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanSaveAReference() throws IOException {
        enqueueServerFile(mMockServer, POCKET_FILTER_SUCESS);
        HaloPocketApi pocketApi = mHaloAuthApi.pocket();
        CallbackV2<Pocket> callback = givenAPocketCallback(mCallbackFlag);
        ReferenceContainer referenceContainer = new ReferenceContainer("mycollection", null);
        pocketApi.saveReferences(referenceContainer).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanSaveACustomDataClass() throws IOException {
        enqueueServerFile(mMockServer, POCKET_FILTER_SUCESS);
        HaloPocketApi pocketApi = mHaloAuthApi.pocket();
        CallbackV2<Pocket> callback = givenAPocketCallback(mCallbackFlag);
        UserDummy userDummy = new UserDummy("132224", "My user", new Date(), "This is my contennt", "htpp://google.com");
        pocketApi.saveData(userDummy).execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

}