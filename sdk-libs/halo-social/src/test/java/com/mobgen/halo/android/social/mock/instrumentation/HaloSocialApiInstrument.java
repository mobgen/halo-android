package com.mobgen.halo.android.social.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.testing.CallbackFlag;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloSocialApiInstrument {

    public static CallbackV2<IdentifiedUser>  givenAHaloSocialProfileCallback(final CallbackFlag flag, final String emailUser){
        return new CallbackV2<IdentifiedUser>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().getUser().getEmail()).isEqualTo(emailUser);
            }
        };
    }

    public static CallbackV2<HaloSocialProfile>  givenAHaloSocialProfileRegisteredCallback(final CallbackFlag flag, final String emailUser){
        return new CallbackV2<HaloSocialProfile>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<HaloSocialProfile> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().email()).isEqualTo(emailUser);
            }
        };
    }
}
