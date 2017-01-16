package com.mobgen.halo.android.content.edition;


import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;


import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.CONTENT_EDIT_API;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.enqueueServerError;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenANewaloContentEditOptions;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenAUpdateHaloContentEditOptions;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenAContentSuccessCallback;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenAContentAuthenticationErrorCallback;

public class HaloContentEditApiTest extends HaloRobolectricTest {

    private MockServer mMockServer;
    private static Halo mHalo;
    private CallbackFlag mCallbackFlag;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mMockServer = MockServer.create();
        mHalo = givenADefaultHalo(mMockServer.start());
        mCallbackFlag = newCallbackFlag();
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatCanAddGeneralContentInstance() throws IOException {
        enqueueServerFile(mMockServer, CONTENT_EDIT_API);
        CallbackV2<HaloContentInstance> callback = givenAContentSuccessCallback(mCallbackFlag, "5874c5f06a3a0d1e00c8039d");
        HaloContentEditApi.addContent(givenANewaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanHandleAutenticationExceptionAfterAddingContent() throws IOException {
        enqueueServerError(mMockServer, 403);
        CallbackV2<HaloContentInstance> callback = givenAContentAuthenticationErrorCallback(mCallbackFlag);
        HaloContentEditApi.addContent(givenANewaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanUpdateGeneralContentInstance() throws IOException {
        enqueueServerFile(mMockServer, CONTENT_EDIT_API);
        CallbackV2<HaloContentInstance> callback = givenAContentSuccessCallback(mCallbackFlag, "5874c5f06a3a0d1e00c8039d");
        HaloContentEditApi.updateContent(givenAUpdateHaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanHandleAutenticationExceptionAfterUpdatingContent() throws IOException {
        enqueueServerError(mMockServer, 403);
        CallbackV2<HaloContentInstance> callback = givenAContentAuthenticationErrorCallback(mCallbackFlag);
        HaloContentEditApi.updateContent(givenANewaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanDeleteGeneralContentInstance() throws IOException {
        enqueueServerFile(mMockServer, CONTENT_EDIT_API);
        CallbackV2<HaloContentInstance> callback = givenAContentSuccessCallback(mCallbackFlag, "5874c5f06a3a0d1e00c8039d");
        HaloContentEditApi.deleteContent(givenAUpdateHaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanHandleAutenticationExceptionAfterDeletingContent() throws IOException {
        enqueueServerError(mMockServer, 403);
        CallbackV2<HaloContentInstance> callback = givenAContentAuthenticationErrorCallback(mCallbackFlag);
        HaloContentEditApi.deleteContent(givenANewaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

}
