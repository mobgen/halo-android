package com.mobgen.halo.android.framework.network.client.body;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * The request body factory class.
 */
public class HaloBodyFactory {

    /**
     * Body factory for easy creation of bodies.
     */
    private HaloBodyFactory() {
        // Do not allow instances for the factory
    }

    /**
     * Request body for form url encoded data.
     *
     * @return The encoding.
     */
    @NonNull
    @Api(2.0)
    public static FormBody.Builder formBody() {
        return new FormBody.Builder();
    }

    /**
     * Creates a request body based on a media type. This is a generic media type posting.
     *
     * @param haloMediaType The media type to post.
     * @param stringBody    The string body to post.
     * @return The request body.
     */
    @Api(2.0)
    @NonNull
    public static RequestBody stringBody(@NonNull HaloMediaType haloMediaType, @NonNull String stringBody) {
        return RequestBody.create(haloMediaType.parseType(), stringBody);
    }

    /**
     * Creates a request body based on a media type. This is a generic media type posting.
     *
     * @param json The json body to post.
     * @return The request body.
     */
    @Api(2.0)
    @NonNull
    public static RequestBody jsonObjectBody(@NonNull JSONObject json) {
        return RequestBody.create(HaloMediaType.APPLICATION_JSON.parseType(), json.toString());
    }

    /**
     * Creates a request body based on a media type. This is a generic media type posting.
     *
     * @param jsonArray The json array body to post.
     * @return The request body.
     */
    @Api(2.0)
    @NonNull
    public static RequestBody jsonObjectBody(@NonNull JSONArray jsonArray) {
        return RequestBody.create(HaloMediaType.APPLICATION_JSON.parseType(), jsonArray.toString());
    }

    /**
     * Creates a request body based on a media type to post a file.
     *
     * @param haloMediaType The media type to post.
     * @param file          The file being posted.
     * @return The request body generated.
     */
    @Api(2.0)
    @NonNull
    public static RequestBody fileBody(@NonNull HaloMediaType haloMediaType, @NonNull File file) {
        return RequestBody.create(haloMediaType.parseType(), file);
    }

}
