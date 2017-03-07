package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Serialize or deserialize Object to query on database from generated content.
 */
@Keep
public class GeneratedContent{

    public static String serialize(@NonNull Object haloContentInstance, @NonNull Parser.Factory parser) throws HaloParsingException {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        AssertionUtils.notNull(parser, "parser");
        try {
            return ((Parser<Object, String>) parser.serialize(Object.class)).convert(haloContentInstance);
        } catch (IOException e) {
            throw new HaloParsingException("Error while serializing the Object instance", e);
        }
    }

    @Nullable
    public static <T> T deserialize(@Nullable String haloContentInstance, @NonNull Parser.Factory parser, @NonNull Class clazz) throws HaloParsingException {
        if (haloContentInstance != null) {
            try {
                return ((Parser<InputStream, T>) parser.deserialize(clazz)).convert(new ByteArrayInputStream(haloContentInstance.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the object instance", e);
            }
        }
        return null;
    }

}
