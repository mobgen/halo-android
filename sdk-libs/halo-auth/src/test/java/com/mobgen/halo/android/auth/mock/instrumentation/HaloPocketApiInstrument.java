package com.mobgen.halo.android.auth.mock.instrumentation;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.models.HaloUserProfile;
import com.mobgen.halo.android.auth.models.IdentifiedUser;
import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.auth.providers.SocialProvider;
import com.mobgen.halo.android.auth.providers.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.auth.providers.google.GoogleSocialProvider;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.testing.CallbackFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloPocketApiInstrument {

    public static Pocket givenAPocket() {
        UserDummy userDummy = new UserDummy("132224", "My user", new Date(), "This is my contennt", "htpp://google.com");
        List<String> myrefs = new ArrayList<>();
        myrefs.add("1");
        myrefs.add("2");
        List<String> empty = new ArrayList<>();
        ReferenceContainer referenceContainer = new ReferenceContainer("mycollection", myrefs);
        ReferenceContainer referenceContainer2 = new ReferenceContainer("mycollection222", myrefs);
        ReferenceContainer referenceContainer3 = new ReferenceContainer("mlol", empty);
        Pocket pocket = new Pocket.Builder()
                .withData(userDummy)
                .withReferences(new ReferenceContainer[]{referenceContainer, referenceContainer2})
                .withReferences(referenceContainer3)
                .build();
        return pocket;
    }

    public static CallbackV2<Pocket> givenAPocketCallback(final CallbackFlag flag) {
        return new CallbackV2<Pocket>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Pocket> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().getReferences()).isNotNull();
                assertThat(result.data().getValues()).isNotNull();
            }
        };
    }

    public static CallbackV2<Pocket> givenAPocketCallback(final CallbackFlag flag, final String id, final int numRefs) {
        return new CallbackV2<Pocket>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Pocket> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().getReferences().size()).isEqualTo(numRefs);
                UserDummy userDummy = result.data().getValues(UserDummy.class);
                assertThat(userDummy.getId()).isEqualTo(id);
            }
        };
    }

    public static CallbackV2<List<ReferenceContainer>> givenAReferenceCallback(final CallbackFlag flag, final int numberOfItems) {
        return new CallbackV2<List<ReferenceContainer>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<List<ReferenceContainer>> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().size()).isEqualTo(numberOfItems);
            }
        };
    }

    public static CallbackV2<Pocket> givenAPocketOnlyWithDataCallback(final CallbackFlag flag, final String id) {
        return new CallbackV2<Pocket>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<Pocket> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                UserDummy userDummy = result.data().getValues(UserDummy.class);
                assertThat(userDummy.getId()).isEqualTo(id);
            }
        };
    }

    public static CallbackV2<UserDummy> givenAPocketOnlyWithDataCallbackAsCustomClass(final CallbackFlag flag, final String id) {
        return new CallbackV2<UserDummy>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<UserDummy> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().getId()).isEqualTo(id);
            }
        };
    }

}
