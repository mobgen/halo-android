package com.mobgen.halo.android.auth.mock.instrumentation;

import android.content.res.Resources;

import com.mobgen.halo.android.auth.R;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowResources;

import static org.mockito.Mockito.mock;

/**
 * This class is used to generate Android resources that are otherwise
 * not present during Robolectric tests
 */
@Implements(Resources.class)
public class StringShadowResources extends ShadowResources {
    @Implementation
    public CharSequence getText(int id) throws Resources.NotFoundException {
        if (id == R.string.halo_social_google_client){
            return "dummy google key";
        } else {
            return "This is a test Text";
        }
    }
}
