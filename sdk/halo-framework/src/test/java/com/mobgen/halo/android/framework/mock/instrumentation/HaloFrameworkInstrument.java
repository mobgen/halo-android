package com.mobgen.halo.android.framework.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.StorageConfig;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenAErrorHandler;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloDatabaseInstrument.givenDatabaseMigrations;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.testing.CallbackFlag;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloFrameworkInstrument {

    public static StorageConfig givenAStorageConfig(){
        return StorageConfig.builder()
                .storageName("HaloNetClientTest")
                .databaseVersion(1)
                .addMigrations(givenDatabaseMigrations())
                .errorHandler(givenAErrorHandler())
                .build();
    }

    public static Subscriber givenASubcriber(final CallbackFlag flag){
        return new Subscriber() {
            @Override
            public void onEventReceived(@NonNull Event event) {
                flag.flagExecuted();
                assertThat(event.getEventId().getId()).isEqualTo("myEventId");
            }
        };
    }


}
