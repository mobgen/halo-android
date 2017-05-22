package com.mobgen.halo.android.notifications.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fernandosouto on 08/05/17.
 */

/**
 * Model of a push image in the notification.
 */
@Keep
@JsonObject
public class PushImage implements Parcelable {
    /**
     * Layout types to apply.
     */
    @StringDef({DEFAULT, TOP, LEFT, BOTTOM, RIGHT, BACKGROUND, EXPANDED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Layout {
    }

    /**
     * default layout without modifying the layout
     */
    public static final String DEFAULT = "default";
    /**
     * expand layout with top image. Without expand mode it will use left mode
     */
    public static final String TOP = "top";
    /**
     * custom layout with the image at left position
     */
    public static final String LEFT = "left";
    /**
     * expand layout with bottom image. Without expand mode it will use right mode
     */
    public static final String BOTTOM = "bottom";
    /**
     * custom layout with the image at right positin
     */
    public static final String RIGHT = "right";
    /**
     * image as background
     */
    public static final String BACKGROUND = "background";
    /**
     * same as default but in expand mode
     */
    public static final String EXPANDED = "expanded";

    /**
     * The url of the image to show
     */
    @JsonField(name = "url")
    private String mUrl;

    /**
     * The layout type to apply
     */
    @JsonField(name = "layout")
    private String mLayout;

    /**
     * Default constructor
     */
    protected PushImage() {

    }

    /**
     * Get the url of the image.
     *
     * @return The url of the image.
     */
    @Api(2.3)
    @Nullable
    public String getUrl() {
        return mUrl;
    }

    /**
     * Set the url of the image.
     *
     * @param url The url of the image to show.
     */
    @Api(2.3)
    public void setUrl(String url) {
        mUrl = url;
    }

    /**
     * The layout type to apply in the notification.
     *
     * @return The layout type.
     */
    @Api(2.3)
    @Nullable
    @Layout
    public String getLayout() {
        return mLayout;
    }

    /**
     * Set the layout type of the notification.
     *
     * @param layout The layout type of the notification.
     */
    @Api(2.3)
    public void setLayout(String layout) {
        mLayout = layout;
    }


    protected PushImage(Parcel in) {
        mUrl = in.readString();
        mLayout = in.readString();
    }

    public static final Creator<PushImage> CREATOR = new Creator<PushImage>() {
        @Override
        public PushImage createFromParcel(Parcel in) {
            return new PushImage(in);
        }

        @Override
        public PushImage[] newArray(int size) {
            return new PushImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUrl);
        dest.writeString(mLayout);
    }

    /**
     * Parses a Halo content instance.
     *
     * @param pushImage The pushImage as string.
     * @param parser    The parser.
     * @return The pushImage parsed or an empty pushImage if the string passed is null.
     * @throws HaloParsingException Error parsing the item.
     */
    @Nullable
    @Keep
    public static PushImage deserialize(@Nullable String pushImage, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (pushImage != null && parser != null) {
            try {
                return ((Parser<InputStream, PushImage>) parser.deserialize(PushImage.class)).convert(new ByteArrayInputStream(pushImage.getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error while deserializing the halocontentInstance", e);
            }
        }
        return null;
    }
}
