package com.mobgen.halo.android.framework.network.client.body;


import com.mobgen.halo.android.framework.common.annotations.Api;

import okhttp3.MediaType;

/**
 * The media type that contains the most common media types matching the
 * <a href="http://tools.ietf.org/html/rfc2045">RFC 2045</a>.
 */
public enum HaloMediaType {
    APPLICATION_JSON("application/json"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_OGG("application/ogg"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_XML("application/xml"),

    AUDIO_BASIC("audio/basic"),
    AUDIO_MP4("audio/mp4"),
    AUDIO_MPEG("audio/mpeg"),
    AUDIO_OGG("audio/ogg"),
    AUDIO_WEBM("audio/webm"),

    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_BMP("image/bmp"),
    IMAGE_TIFF("image/tiff"),

    MESSAGE_HTTP("message/http"),
    MESSAGE_PARTIAL("message/partial"),

    MODEL_MESH("model/mesh"),
    MODEL_VRML("model/vrml"),
    MODEL_X3D_BIN("model/x3d+binary"),
    MODEL_X3D_XML("model/x3d+xml"),

    MULTIPART_MIXED("multipart/mixed"),
    MULTIPART_ALTERNATIVE("multipart/alternative"),
    MULTIPART_RELATED("multipart/related"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    MULTIPART_ENCRYPTED("multipart/encrypted"),
    MULTIPART_PARALLEL("multipart/parallel"),

    TEXT_CSV("text/csv"),
    TEXT_HTML("text/html"),
    TEXT_MARKDOWN("text/markdown"),
    TEXT_RTF("text/rtf"),
    TEXT_VCARD("text/vcard"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml"),

    VIDEO_AVI("video/avi"),
    VIDEO_MPEG("video/mpeg"),
    VIDEO_MP4("video/mp4"),
    VIDEO_OGG("video/ogg"),
    VIDEO_QUICKTIME("video/quicktime"),
    VIDEO_WEBM("video/webm"),
    VIDEO_X_FLV("video/x-flv");

    /**
     * Stored media type.
     */
    final String mMediaType;

    /**
     * Constructor for the media types.
     *
     * @param mediaType The media type.
     */
    HaloMediaType(String mediaType) {
        mMediaType = mediaType;
    }

    /**
     * Parses and returns the media type.
     *
     * @return The media type.
     */
    @Api(2.0)
    public MediaType parseType() {
        return MediaType.parse(mMediaType);
    }

    /**
     * Provides the media type name.
     *
     * @return The media type name.
     */
    @Override
    public String toString() {
        return mMediaType;
    }
}
