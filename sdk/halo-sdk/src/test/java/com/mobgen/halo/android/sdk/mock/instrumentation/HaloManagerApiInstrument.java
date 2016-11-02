package com.mobgen.halo.android.sdk.mock.instrumentation;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.telecom.Call;

import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;
import com.mobgen.halo.android.sdk.core.management.models.Token;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;
import com.mobgen.halo.android.testing.CallbackFlag;


import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloManagerApiInstrument {

    public static  CallbackV2<Device> givenCallbackWithDeviceSegmentationTagAdd(final CallbackFlag flag,final HaloSegmentationTag mySegmentationTag) {
       return new CallbackV2<Device>() {
           @Override
           public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data().getTags().size()).isGreaterThan(0);
                assertThat(result.data().getTags().contains(mySegmentationTag)).isTrue();
           }
       };
    }

    public static  CallbackV2<Device> givenCallbackWithDeviceSegmentationTagAddList(final CallbackFlag flag,final List<HaloSegmentationTag> mySegmentationTagList) {
        return new CallbackV2<Device>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data().getTags().size()).isGreaterThan(0);
                assertThat(result.data().getTags().contains(mySegmentationTagList.get(0))).isTrue();
                assertThat(result.data().getTags().contains(mySegmentationTagList.get(1))).isTrue();
            }
        };
    }

    public static  CallbackV2<Device> givenCallbackWithDeviceSegmentationTagRemoved(final CallbackFlag flag,final HaloSegmentationTag mySegmentationTag) {
        return new CallbackV2<Device>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data().getTags().size()).isGreaterThan(0);
                assertThat(result.data().getTags().contains(mySegmentationTag)).isFalse();
            }
        };
    }

    public static  CallbackV2<Device> givenCallbackWithDeviceSegmentationTagRemovedList(final CallbackFlag flag,final List<HaloSegmentationTag> mySegmentationTagList) {
        return new CallbackV2<Device>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data().getTags().size()).isGreaterThan(0);
                assertThat(result.data().getTags().contains(mySegmentationTagList.get(0))).isFalse();
                assertThat(result.data().getTags().contains(mySegmentationTagList.get(1))).isFalse();
            }
        };
    }

    public static CallbackV2<List<HaloModule>> givenCallbackWithGetModules(final CallbackFlag flag, final boolean isFromNetwork){
        return new CallbackV2<List<HaloModule>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<List<HaloModule>> result) {
                flag.flagExecuted();
                assertThat(result.status().isFresh()).isTrue();
                assertThat(result.data()).isNotNull();
                if(isFromNetwork) {
                    assertThat(result.data().size()).isGreaterThan(0);
                }else{
                    assertThat(result.data().size()).isEqualTo(0);
                }
            }
        };
    }

    public static CallbackV2<Cursor> givenCallbackWithGetModulesAsRaw(final CallbackFlag flag, final boolean isFromNetwork){
        return new CallbackV2<Cursor>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Cursor> result) {
                flag.flagExecuted();
                assertThat(result.status().isFresh()).isTrue();
                assertThat(result.data()).isNotNull();
                if(isFromNetwork) {
                    assertThat(result.data().getCount()).isGreaterThan(0);
                }else{
                    assertThat(result.data().getCount()).isEqualTo(0);
                }
            }
        };
    }

    public static CallbackV2<Device> givenCallbackWithSyncDevice(final CallbackFlag flag){
        return new CallbackV2<Device>(){
            @Override
            public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().getAlias()).isEqualTo("myTestUser");
            }
        };
    }

    public static CallbackV2<Device> givenCallbackWithSendDevice(final CallbackFlag flag){
        return new CallbackV2<Device>(){
            @Override
            public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().getAlias()).isEqualTo("myTestUser");
            }
        };
    }

    public static CallbackV2<Device> givenCallbackWithGetDeviceAnonymous(final CallbackFlag flag){
        return new CallbackV2<Device>(){
            @Override
            public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().getAlias()).isNull();
            }
        };
    }

    public static CallbackV2<Device> givenCallbackWithGetDevice(final CallbackFlag flag){
        return new CallbackV2<Device>(){
            @Override
            public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().getAlias()).isEqualTo("myTestUser");
            }
        };
    }

    public static CallbackV2<HaloServerVersion> givenCallbackServerVersion(final CallbackFlag flag, final String version){
        return new CallbackV2<HaloServerVersion>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<HaloServerVersion> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().getHaloVersion()).isNotEqualTo(version);
            }
        };
    }

    public static CallbackV2<Token> givenCallbackWithRequestToken(final CallbackFlag flag){
        return new CallbackV2<Token>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Token> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().getAccessToken()).isNotNull();
            }
        };
    }

    public static CallbackV2<Device> givenCallbackWithSetNotificationToken(final CallbackFlag flag){
        return new CallbackV2<Device>(){
            @Override
            public void onFinish(@NonNull HaloResultV2<Device> result) {
                flag.flagExecuted();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().getNotificationsToken()).isEqualTo("mytoken");
            }
        };
    }
}
