package com.mobgen.halo.android.sdk.core.management.events;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.sdk.core.management.models.HaloEvent;

/**
 * Created by f.souto.gonzalez on 02/06/2017.
 */

public class EventRepository {

    EventRemoteDatasource mEventRemoteDataSource;

    public EventRepository(@NonNull EventRemoteDatasource eventRemoteDatasource){
        mEventRemoteDataSource = eventRemoteDatasource;
    }

    public HaloEvent sendEvent(@NonNull HaloEvent haloEvent) throws HaloParsingException, HaloNetException {
        return mEventRemoteDataSource.sendEvent(haloEvent);
    }
}
