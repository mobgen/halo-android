package com.mobgen.halo.android.content.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.testing.CallbackFlag;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloEditContentInstruments {

    public static CallbackV2<HaloContentInstance> givenAContentSuccessCallback(final CallbackFlag flag, final String textToTest) {
        return new CallbackV2<HaloContentInstance>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<HaloContentInstance> result) {
                flag.flagExecuted();
                assertThat(textToTest).isEqualTo(result.data().getItemId());
            }
        };
    }


    public static CallbackV2<HaloContentInstance> givenAContentAuthenticationErrorCallback(final CallbackFlag flag) {
        return new CallbackV2<HaloContentInstance>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<HaloContentInstance> result) {
                flag.flagExecuted();
                assertThat(result.status().isSecurityError()).isEqualTo(true);
            }
        };
    }

    public static HaloContentInstance givenAUpdateHaloContentEditOptions(){
        Map<String,String> values = new HashMap<>();
        values.put("Title","My title");

        HaloContentInstance.Builder instanceBuilder = new HaloContentInstance.Builder("halomodulename")
                .withModuleId("586a47f836a6b01300ec9f00")
                .withName("From Android SDK")
                .withContentData(values);

        return instanceBuilder.build();
    }

    public static HaloContentInstance givenANewaloContentEditOptions(){
        Map<String,String> values = new HashMap<>();
        values.put("Title","My title");

        HaloContentInstance.Builder instanceBuilder = new HaloContentInstance.Builder("halomodulename")
                .withId("5874c5f06a3a0d1e00c8039d")
                .withModuleId("586a47f836a6b01300ec9f00")
                .withName("From Android SDK")
                .withContentData(values);

        return instanceBuilder.build();
    }
}
