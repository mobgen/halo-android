package com.mobgen.halo.android.content.edition;


import android.os.Bundle;

import com.mobgen.halo.android.content.edition.batch.BatchBundleizeHelper;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;


import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.content.edition.HaloContentEditApi.BATCH_FINISHED_EVENT;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.CONTENT_BATCH_API;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.CONTENT_EDIT_API;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.enqueueServerError;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenABatchContentSuccessCallback;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenABatchContentSuccessScheduledCallback;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenABatchOperationsEventSubscription;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenABatchOperationsEventSubscriptionScheduled;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloEditContentInstruments.givenANewHaloContentEditOptions;
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
        HaloContentEditApi.with(mHalo)
                .addContent(givenANewHaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanHandleAutenticationExceptionAfterAddingContent() throws IOException {
        enqueueServerError(mMockServer, 403);
        CallbackV2<HaloContentInstance> callback = givenAContentAuthenticationErrorCallback(mCallbackFlag);
        HaloContentEditApi.with(mHalo)
                .addContent(givenANewHaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanUpdateGeneralContentInstance() throws IOException {
        enqueueServerFile(mMockServer, CONTENT_EDIT_API);
        CallbackV2<HaloContentInstance> callback = givenAContentSuccessCallback(mCallbackFlag, "5874c5f06a3a0d1e00c8039d");
        HaloContentEditApi.with(mHalo)
                .updateContent(givenAUpdateHaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanHandleAutenticationExceptionAfterUpdatingContent() throws IOException {
        enqueueServerError(mMockServer, 403);
        CallbackV2<HaloContentInstance> callback = givenAContentAuthenticationErrorCallback(mCallbackFlag);
        HaloContentEditApi.with(mHalo)
                .updateContent(givenANewHaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanDeleteGeneralContentInstance() throws IOException {
        enqueueServerFile(mMockServer, CONTENT_EDIT_API);
        CallbackV2<HaloContentInstance> callback = givenAContentSuccessCallback(mCallbackFlag, "5874c5f06a3a0d1e00c8039d");
        HaloContentEditApi.with(mHalo)
                .deleteContent(givenAUpdateHaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanHandleAutenticationExceptionAfterDeletingContent() throws IOException {
        enqueueServerError(mMockServer, 403);
        CallbackV2<HaloContentInstance> callback = givenAContentAuthenticationErrorCallback(mCallbackFlag);
        HaloContentEditApi.with(mHalo)
                .deleteContent(givenANewHaloContentEditOptions())
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanPerfomABatchOperation() throws IOException {
        enqueueServerFile(mMockServer, CONTENT_BATCH_API);
        CallbackV2<BatchOperationResults> callback = givenABatchContentSuccessCallback(mCallbackFlag, false);
        HaloContentInstance instanceDelete = givenANewHaloContentEditOptions();
        HaloContentInstance instanceUpdate = givenANewHaloContentEditOptions();
        HaloContentInstance instanceCreate = givenAUpdateHaloContentEditOptions();
        BatchOperations batchOperations = BatchOperations.builder()
                .delete(instanceDelete)
                .create(instanceCreate)
                .update(instanceUpdate)
                .build();
        HaloContentEditApi.with(mHalo)
                .batch(batchOperations, true)
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanReceiveResultOperationByEventOnSubscription() throws IOException {
        enqueueServerFile(mMockServer, CONTENT_BATCH_API);
        CallbackV2<BatchOperationResults> callback = givenABatchContentSuccessCallback(mCallbackFlag, true);
        HaloContentInstance instanceDelete = givenANewHaloContentEditOptions();
        HaloContentInstance instanceUpdate = givenANewHaloContentEditOptions();
        HaloContentInstance instanceCreate = givenAUpdateHaloContentEditOptions();
        BatchOperations batchOperations = BatchOperations.builder()
                .delete(instanceDelete)
                .create(instanceCreate)
                .update(instanceUpdate)
                .build();
        ISubscription eventSubscription = givenABatchOperationsEventSubscription(mHalo, true);
        HaloContentEditApi.with(mHalo)
                .batch(batchOperations, true)
                .execute(callback);

        assertThat(eventSubscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanReceiveConflictsOperationByEventOnSubscription() throws IOException {
        HaloContentInstance instanceDelete = givenAUpdateHaloContentEditOptions();
        HaloContentInstance instanceUpdate = givenAUpdateHaloContentEditOptions();
        HaloContentInstance instanceCreate = givenANewHaloContentEditOptions();
        BatchOperations conflictOperations = BatchOperations.builder()
                .delete(instanceDelete)
                .create(instanceCreate)
                .update(instanceUpdate)
                .build();
        ISubscription eventSubscription = givenABatchOperationsEventSubscription(mHalo, false);
        Bundle batchConflict = BatchBundleizeHelper.bundleizeBatchOperations(conflictOperations);
        Halo.instance().framework().emit(new Event(EventId.create(BATCH_FINISHED_EVENT), batchConflict));


        assertThat(eventSubscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isFalse();
    }


    @Test
    public void thatCanScheduleABatchOperation() throws IOException {
        enqueueServerError(mMockServer, 500);
        enqueueServerFile(mMockServer, CONTENT_BATCH_API);
        CallbackV2<BatchOperationResults> callback = givenABatchContentSuccessScheduledCallback(mCallbackFlag);
        HaloContentInstance instanceDelete = givenAUpdateHaloContentEditOptions();
        HaloContentInstance instanceUpdate = givenAUpdateHaloContentEditOptions();
        HaloContentInstance instanceCreate = givenANewHaloContentEditOptions();
        BatchOperations batchOperations = BatchOperations.builder()
                .delete(instanceDelete)
                .create(instanceCreate)
                .update(instanceUpdate)
                .build();
        ISubscription eventSubscription = givenABatchOperationsEventSubscriptionScheduled(mHalo);
        HaloContentEditApi.with(mHalo)
                .batch(batchOperations, true)
                .execute(callback);

        assertThat(eventSubscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanPerfomAllBatchOperationsAfterSchedule() throws IOException {
        enqueueServerError(mMockServer, 500);
        enqueueServerFile(mMockServer, CONTENT_BATCH_API);
        CallbackV2<BatchOperationResults> callback = givenABatchContentSuccessScheduledCallback(mCallbackFlag);
        HaloContentInstance instanceDelete = givenAUpdateHaloContentEditOptions();
        HaloContentInstance instanceUpdate = givenAUpdateHaloContentEditOptions();
        HaloContentInstance instanceCreate = givenANewHaloContentEditOptions();
        BatchOperations batchOperations = BatchOperations.builder()
                .delete(instanceDelete)
                .create(instanceCreate)
                .update(instanceUpdate)
                .truncate(instanceCreate)
                .createOrUpdate(instanceCreate)
                .build();
        ISubscription eventSubscription = givenABatchOperationsEventSubscriptionScheduled(mHalo);
        HaloContentEditApi.with(mHalo)
                .batch(batchOperations, true)
                .execute(callback);

        assertThat(eventSubscription).isNotNull();
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

}
