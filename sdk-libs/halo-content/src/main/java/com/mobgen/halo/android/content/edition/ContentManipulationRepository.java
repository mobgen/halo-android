package com.mobgen.halo.android.content.edition;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloEditContentOptions;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloAuthenticationException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

/**
 * General content instance content manipulation reporsitory
 */
public class ContentManipulationRepository {

    /**
     * Remote data source.
     */
    private ContentManipulationRemoteDataSource mRemoteDatasource;
    /**
     * The halo content api instance.
     */
    private HaloContentApi mHaloContentApi;

    /**
     * Constructor for the repository.
     *
     * @param haloContentApi The halo content api instance.
     * @param contentManipulationRemoteDataSource The remote data source.
     */
    public ContentManipulationRepository(@NonNull HaloContentApi haloContentApi, @NonNull ContentManipulationRemoteDataSource contentManipulationRemoteDataSource) {
        AssertionUtils.notNull(haloContentApi, "haloContentApi");
        AssertionUtils.notNull(contentManipulationRemoteDataSource, "contentManipulationRemoteDataSource");
        mRemoteDatasource = contentManipulationRemoteDataSource;
        mHaloContentApi = haloContentApi;
    }

    /**
     * Request to add a new general content instance.
     *
     * @param operation Halo request operation type
     * @param haloEditContentOptions The halo general content instance to add.
     * @return A HaloResultV2<HaloEditContentOptions> with the result
     * @throws HaloNetException
     * @throws HaloParsingException
     */
    @NonNull
    public HaloResultV2<HaloContentInstance> addContent(@NonNull HaloRequestMethod operation, @NonNull HaloEditContentOptions haloEditContentOptions) throws HaloNetException, HaloParsingException {
        HaloStatus.Builder status = HaloStatus.builder();
        HaloContentInstance response = null;
        try {
            response = mRemoteDatasource.addContent(operation, haloEditContentOptions);
            mHaloContentApi.sync(SyncQuery.create(haloEditContentOptions.getModuleName(), Threading.POOL_QUEUE_POLICY),true);
        } catch (HaloAuthenticationException haloAuthenticationException) {
            status.error(haloAuthenticationException);
        } catch (HaloNetException haloNetException) {
            status.error(haloNetException);
        } catch (HaloParsingException haloParsingException) {
            status.error(haloParsingException);
        }
        return new HaloResultV2<>(status.build(), response);
    }

    /**
     * Request to update a giben general content instance.
     *
     * @param operation Halo request operation type
     * @param haloEditContentOptions The halo general content instance to update.
     * @return A HaloResultV2<HaloEditContentOptions> with the result.
     * @throws HaloNetException
     * @throws HaloParsingException
     */
    @NonNull
    public HaloResultV2<HaloContentInstance> updateContent(@NonNull HaloRequestMethod operation, @NonNull HaloEditContentOptions haloEditContentOptions) throws HaloNetException, HaloParsingException {
        HaloStatus.Builder status = HaloStatus.builder();
        HaloContentInstance response = null;
        try {
            response = mRemoteDatasource.updateContent(operation, haloEditContentOptions);
            mHaloContentApi.sync(SyncQuery.create(haloEditContentOptions.getModuleName(), Threading.POOL_QUEUE_POLICY),true);
        } catch (HaloAuthenticationException haloAuthenticationException) {
            status.error(haloAuthenticationException);
        } catch (HaloNetException haloNetException) {
            status.error(haloNetException);
        } catch (HaloParsingException haloParsingException) {
            status.error(haloParsingException);
        }
        return new HaloResultV2<>(status.build(), response);
    }

    /**
     * Request to update a giben general content instance.
     *
     * @param operation Halo request operation type
     * @param haloEditContentOptions The halo general content instance to update.
     * @return A HaloResultV2<HaloEditContentOptions> with the result.
     * @throws HaloNetException
     * @throws HaloParsingException
     */
    @NonNull
    public HaloResultV2<HaloContentInstance> deleteContent(@NonNull HaloRequestMethod operation, @NonNull HaloEditContentOptions haloEditContentOptions) throws HaloNetException, HaloParsingException {
        HaloStatus.Builder status = HaloStatus.builder();
        HaloContentInstance response = null;
        try {
            response = mRemoteDatasource.deleteContent(operation, haloEditContentOptions);
            mHaloContentApi.sync(SyncQuery.create(haloEditContentOptions.getModuleName(), Threading.POOL_QUEUE_POLICY),true);
        } catch (HaloAuthenticationException haloAuthenticationException) {
            status.error(haloAuthenticationException);
        } catch (HaloNetException haloNetException) {
            status.error(haloNetException);
        } catch (HaloParsingException haloParsingException) {
            status.error(haloParsingException);
        }
        return new HaloResultV2<>(status.build(), response);
    }
}
