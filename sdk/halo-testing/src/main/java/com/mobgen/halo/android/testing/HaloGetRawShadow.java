package com.mobgen.halo.android.testing;

import android.content.res.Resources;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Kiko on 21/01/2019 for sdk.
 */
@Implements(Resources.class)
public class HaloGetRawShadow {

    @Implementation
    public InputStream openRawResource(int id) throws Resources.NotFoundException, UnsupportedEncodingException {
        return  new ByteArrayInputStream(getInputStream().getBytes("UTF-8"));
    }

    private String getInputStream() {
        return "defautl cert text";
    }

}
