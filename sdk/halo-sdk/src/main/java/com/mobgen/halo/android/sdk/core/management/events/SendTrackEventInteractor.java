package com.mobgen.halo.android.sdk.core.management.events;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.management.models.HaloEvent;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

import java.util.List;

/**
 * Created by f.souto.gonzalez on 02/06/2017.
 */

public class SendTrackEventInteractor implements HaloInteractorExecutor.Interactor<HaloEvent> {


    HaloEvent mHaloEvent;

    EventRepository mEventRepository;

    public SendTrackEventInteractor(@NonNull EventRepository eventRepository, @NonNull HaloEvent haloEvent) {
        mEventRepository = eventRepository;
        mHaloEvent = haloEvent;
    }

    @NonNull
    @Override
    public HaloResultV2<HaloEvent> executeInteractor() throws Exception {
        HaloStatus.Builder status = HaloStatus.builder();
        HaloEvent responseEvent = null;
        try {
            responseEvent = mEventRepository.sendEvent(mHaloEvent);
        } catch (HaloNetException | HaloParsingException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), responseEvent);
    }
}
