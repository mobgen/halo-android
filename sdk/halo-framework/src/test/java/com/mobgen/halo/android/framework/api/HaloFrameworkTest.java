package com.mobgen.halo.android.framework.api;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import static com.mobgen.halo.android.framework.mock.instrumentation.HaloFrameworkInstrument.givenAStorageConfig;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloFrameworkInstrument.givenASubcriber;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloFrameworkTest extends HaloRobolectricTest {

    private HaloFramework mFramework;
    private CallbackFlag mCallbackFlag;

    @Before
    public void initialize() {
        mFramework = FrameworkMock.createSameThreadFramework("myTestEndpoint");
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatCreateStorageApi(){
        HaloStorageApi storage = mFramework.createStorage(givenAStorageConfig());
        assertThat(storage).isNotNull();
        assertThat(storage.db()).isNotNull();
        assertThat(mFramework.storage("HaloNetClientTest")).isEqualTo(storage);
    }

    @Test
    public void thatCreateStorageApiThatExist(){
        HaloStorageApi storage = mFramework.createStorage(givenAStorageConfig());
        HaloStorageApi storageCopy = mFramework.createStorage(givenAStorageConfig());
        assertThat(storage).isNotNull();
        assertThat(storageCopy).isNotNull();
        assertThat(storageCopy).isEqualTo(storage);
    }

    @Test
    public void thatCanSubcribeToEvent(){
        EventId eventId = EventId.create("myEventId");
        Event event =  new Event(eventId);
        ISubscription subscription = mFramework.subscribe(givenASubcriber(mCallbackFlag),eventId);
        assertThat(subscription).isNotNull();
        subscription.unsubscribe();
    }

    @Test
    public void thatCanReceiveEvent(){
        EventId eventId = EventId.create("myEventId");
        Event event =  new Event(eventId,new Bundle());
        ISubscription subscription = mFramework.subscribe(givenASubcriber(mCallbackFlag),eventId);
        mFramework.emit(event);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(subscription).isNotNull();
        subscription.unsubscribe();
    }

    @Test
    public void thatCanGetToolbox(){
        assertThat(mFramework.toolbox()).isNotNull();
    }

    @Test
    public void thatCanGetNetwork(){
        assertThat(mFramework.network()).isNotNull();
    }

    @Test
    public void thatCanGetParser(){
        assertThat(mFramework.parser()).isNotNull();
    }

    @Test
    public void thatCanSetDebugMode(){
        mFramework.setDebugFlag(true);
        assertThat(mFramework.isInDebugMode()).isTrue();
    }



}
