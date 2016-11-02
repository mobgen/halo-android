package com.mobgen.halo.android.sdk.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Pair;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Url cloudinary helper to add params to the url.
 * See <a href="http://cloudinary.com/documentation/image_transformation_reference">this link</a> for a full reference.
 */
@Keep
public class HaloCloudinary {

    @StringDef({CROP_MODE_SCALE,
            CROP_MODE_FIT,
            CROP_MODE_LIMIT,
            CROP_MODE_SCALE_FIT,
            CROP_MODE_FILL,
            CROP_MODE_LARGE_FILL,
            CROP_MODE_PAD,
            CROP_MODE_LARGE_PAD,
            CROP_MODE_SCALE_PAD,
            CROP_MODE_CROP,
            CROP_MODE_THUMB,
            CROP_MODE_AOI_CROP,
            CROP_MODE_AOI_SCALE
    })
    @Retention(value = RetentionPolicy.SOURCE)
    /**
     * Crop executionMode static analysis flag.
     */
    public @interface CropMode {
    }

    /**
     * Change the size of the image exactly to the given width and height without necessarily retaining the original aspect ratio: all original image parts are visible but might be stretched or shrunk.
     */
    @Api(1.1)
    public static final String CROP_MODE_SCALE = "scale";
    /**
     * The image is resized so that it takes up as much space as possible within a bounding box defined by the given width and height parameters. The original aspect ratio is retained and all of the original image is visible.
     */
    @Api(1.1)
    public static final String CROP_MODE_FIT = "fit";
    /**
     * Same as the 'fit' executionMode but only if the original image is larger than the given limit (width and height), in which case the image is scaled down so that it takes up as much space as possible within a bounding box defined by the given width and height parameters. The original aspect ratio is retained and all of the original image is visible.
     */
    @Api(1.1)
    public static final String CROP_MODE_LIMIT = "limit";
    /**
     * Same as the 'fit' executionMode but only if the original image is smaller than the given minimum (width and height), in which case the image is scaled up so that it takes up as much space as possible within a bounding box defined by the given width and height parameters. The original aspect ratio is retained and all of the original image is visible.
     */
    @Api(1.1)
    public static final String CROP_MODE_SCALE_FIT = "mfit";
    /**
     * Create an image with the exact given width and height while retaining the original aspect ratio, using only part of the image that fills the given dimensions if necessary (only part of the original image might be visible if the requested aspect ratio is different from the original aspect ratio).
     */
    @Api(1.1)
    public static final String CROP_MODE_FILL = "fill";
    /**
     * Same as the 'fill' executionMode but only if the original image is larger than the given limit (width and height), in which case the image is scaled down to fill the given width and height while retaining the original aspect ratio, using only part of the image that fills the given dimensions if necessary (only part of the original image might be visible if the requested aspect ratio is different from the original aspect ratio).
     */
    @Api(1.1)
    public static final String CROP_MODE_LARGE_FILL = "lfill";
    /**
     * Resize the image to fill the given width and height while retaining the original aspect ratio. If the proportions of the original image do not match the given width and height, padding is added to the image to reach the required size.
     */
    @Api(1.1)
    public static final String CROP_MODE_PAD = "pad";
    /**
     * Same as the 'pad' executionMode but only if the original image is larger than the given limit (width and height), in which case the image is scaled down to fill the given width and height while retaining the original aspect ratio. If the proportions of the original image do not match the given width and height, padding is added to the image to reach the required size.
     */
    @Api(1.1)
    public static final String CROP_MODE_LARGE_PAD = "lpad";
    /**
     * Same as the 'pad' executionMode but only if the original image is smaller than the given minimum (width and height), in which case the image is scaled up to fill the given width and height while retaining the original aspect ratio. If the proportions of the original image do not match the given width and height, padding is added to the image to reach the required size.
     */
    @Api(1.1)
    public static final String CROP_MODE_SCALE_PAD = "mpad";
    /**
     * Used to extract a given width & height out of the original image. The original proportions are retained and so is the size of the graphics.
     */
    @Api(1.1)
    public static final String CROP_MODE_CROP = "crop";
    /**
     * Generate a thumbnail using face detection in combination with the 'face' or 'faces' gravity.
     */
    @Api(1.1)
    public static final String CROP_MODE_THUMB = "thumb";
    /**
     * Crop your image based on automatically calculated areas of interest within each specific photo. See the <a href="/documentation/imagga_crop_and_scale_addon#smartly_crop_images">Imagga Crop and Scale</a> add-on documentation for more information.
     */
    @Api(1.1)
    public static final String CROP_MODE_AOI_CROP = "imagga_crop";
    /**
     * Scale your image based on automatically calculated areas of interest within each specific photo. See the <a href="/documentation/imagga_crop_and_scale_addon#smartly_scale_images">Imagga Crop and Scale</a> add-on documentation for more information.
     */
    @Api(1.1)
    public static final String CROP_MODE_AOI_SCALE = "imagga_scale";

    @StringDef({
            IMAGE_GRAVITY_NORTH_WEST,
            IMAGE_GRAVITY_NORTH,
            IMAGE_GRAVITY_NORTH_EAST,
            IMAGE_GRAVITY_WEST,
            IMAGE_GRAVITY_CENTER,
            IMAGE_GRAVITY_EAST,
            IMAGE_GRAVITY_SOUTH_WEST,
            IMAGE_GRAVITY_SOUTH,
            IMAGE_GRAVITY_SOUTH_EAST,
            IMAGE_GRAVITY_XY_CENTER,
            IMAGE_GRAVITY_FACE,
            IMAGE_GRAVITY_FACE_CENTER,
            IMAGE_GRAVITY_FACES,
            IMAGE_GRAVITY_FACES_CENTER,
            IMAGE_GRAVITY_LARGEST_FACE,
            IMAGE_GRAVITY_ALL_FACES,
            IMAGE_GRAVITY_LARGEST_EYES,
            IMAGE_GRAVITY_CUSTOM,
            IMAGE_GRAVITY_CUSTOM_FACE,
            IMAGE_GRAVITY_CUSTOM_FACES,
            IMAGE_GRAVITY_CUSTOM_LARGEST_FACE,
            IMAGE_GRAVITY_CUSTOM_ALL_FACES
    })
    @Retention(value = RetentionPolicy.SOURCE)
    /**
     * Gravity executionMode static analysis flag.
     */
    public @interface ImageGravity {
    }

    /**
     * North west corner (top left).
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_NORTH_WEST = "north_west";
    /**
     * North center part (top center).
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_NORTH = "north";
    /**
     * North east corner (top right).
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_NORTH_EAST = "north_east";
    /**
     * Middle west part (left).
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_WEST = "west";
    /**
     * The center of the image.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_CENTER = "center";
    /**
     * Middle east part (right).
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_EAST = "east";
    /**
     * South west corner (bottom left).
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_SOUTH_WEST = "south_west";
    /**
     * South center part (bottom center).
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_SOUTH = "south";
    /**
     * South east corner (bottom right).
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_SOUTH_EAST = "south_east";
    /**
     * Set the crop's center of gravity to the given x & y coordinates"
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_XY_CENTER = "xy_center";
    /**
     * Automatically detect the largest face in an image and aim to make it the center of the cropped image. Alternatively, use face coordinates specified by API if available. Defaults to the 'north' gravity if no face was detected.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_FACE = "face";
    /**
     * Same as the 'face' gravity, but defaults to 'center' gravity instead of 'north' if no face is detected.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_FACE_CENTER = "face:center";
    /**
     * Automatically detect multiple faces in an image and aim to make them the center of the cropped image.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_FACES = "faces";
    /**
     * Same as the 'faces' gravity, but defaults to 'center' gravity instead of 'north' if no faces are detected.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_FACES_CENTER = "faces:center";
    /**
     * Automatically detect the largest face in an image with the Advanced Facial Attribute Detection add-on and make it the focus of the transformation.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_LARGEST_FACE = "adv_face";
    /**
     * Automatically detect all the faces in an image with the Advanced Facial Attribute Detection add-on and make them the focus of the transformation.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_ALL_FACES = "adv_faces";
    /**
     * Automatically detect the largest pair of eyes in an image with the Advanced Facial Attribute Detection add-on and make them the focus of the transformation.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_LARGEST_EYES = "adv_eyes";
    /**
     * Use custom coordinates that were specified by the upload or admin API and aim to make it the center of the cropped image. Defaults to 'center' gravity if no custom coordinates are available.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_CUSTOM = "custom";
    /**
     * Same as the 'custom' gravity, but defaults to 'face' gravity if no custom coordinates are available.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_CUSTOM_FACE = "custom:face";
    /**
     * Same as the 'custom' gravity, but defaults to 'faces' gravity if no custom coordinates are available.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_CUSTOM_FACES = "custom:faces";
    /**
     * Same as the 'custom' gravity, but defaults to 'adv_face' gravity if no custom coordinates are available.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_CUSTOM_LARGEST_FACE = "custom:adv_face";
    /**
     * Same as the 'custom' gravity, but defaults to 'adv_faces' gravity if no custom coordinates are available.
     */
    @Api(1.1)
    public static final String IMAGE_GRAVITY_CUSTOM_ALL_FACES = "custom:adv_faces";

    @StringDef({
            IMAGE_FLAG_KEEP_IPTC,
            IMAGE_FLAG_ATTACHMENT,
            IMAGE_FLAG_RELATIVE,
            IMAGE_FLAG_REGION_RELATIVE,
            IMAGE_FLAG_PROGRESSIVE,
            IMAGE_FLAG_PNG_8,
            IMAGE_FLAG_FORCE_STRIP,
            IMAGE_FLAG_CUTTER,
            IMAGE_FLAG_CLIP,
            IMAGE_FLAG_AWEBP,
            IMAGE_FLAG_LAYER_APPLY,
            IMAGE_FLAG_IGNORE_ASPECT_RATIO,
            IMAGE_FLAG_TILED,
            IMAGE_FLAG_LOSSY,
            IMAGE_FLAG_STRIP_PROFILE,
            IMAGE_FLAG_RASTERIZE,
            IMAGE_FLAG_TEXT_NO_TRIM
    })
    @Retention(value = RetentionPolicy.SOURCE)
    /**
     * Image flag static analysis flag.
     */
    public @interface ImageFlag {
    }

    /**
     * Without this flag, Cloudinary's default behavior is to strip all meta-data when generating new image transformations.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_KEEP_IPTC = "keep_iptc";
    /**
     * Deliver the image as an attachment. When the image's URL is accessed, tells the browser to save the image instead of embedding it in a page.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_ATTACHMENT = "attachment";
    /**
     * Modify percentage-based width & height parameters of overlays and underlays (e.g., 1.0) to be relative to the containing image instead of the added layer.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_RELATIVE = "relative";
    /**
     * Modify percentage-based width & height parameters of overlays and underlays (e.g., 1.0) to be relative to the overlaid region. Currently regions are only defined when using gravity 'face', 'faces' or 'custom'.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_REGION_RELATIVE = "region_relative";
    /**
     * Generate a JPG image using the progressive (interlaced) JPG format. This format allows the browser to quickly show a low-quality rendering of the image until the full-quality image is loaded.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_PROGRESSIVE = "progressive";
    /**
     * Generate PNG images in the PNG8 format instead of the default PNG24 format.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_PNG_8 = "png8";
    /**
     * Tells Cloudinary to clear all image meta-data (IPTC, Exif and XMP) while applying an incoming transformation.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_FORCE_STRIP = "force_strip";
    /**
     * Trim pixels according to the transparency levels of a given overlay image. Whenever the overlay image is opaque, the original is shown, and wherever the overlay is transparent, the result will be transparent as well.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_CUTTER = "cutter";
    /**
     * Trim pixels according to a clipping path included in the original image (e.g., manually created using PhotoShop).
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_CLIP = "clip";
    /**
     * When converting animated GIF images to the WebP format, generate an Animated WebP from all the frames in the animated GIF file instead of only from the first still frame of the GIF.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_AWEBP = "awebp";
    /**
     * Apply all chained transformations, until a transformation component that includes this flag, on the last added overlay or underlay instead of applying on the the containing image.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_LAYER_APPLY = "layer_apply";
    /**
     * Allow specifying only either width or height so the value of the second axis remains as is and is not calculated to maintain aspect ratio of the original image.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_IGNORE_ASPECT_RATIO = "ignore_aspect_ratio";
    /**
     * Tile the added overlay over the entire image.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_TILED = "tiled";
    /**
     * Automatically use lossy compression when delivering animated GIF files. This flag can also be used as a conditional flag for delivering PNG files: it tells Cloudinary to deliver the image in PNG format (as requested) unless there is no transparency channel - in which case deliver in JPEG format.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_LOSSY = "lossy";
    /**
     * Tells Cloudinary to clear all ICC color profile data included with the image.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_STRIP_PROFILE = "strip_profile";
    /**
     * Reduces the image to one flat pixelated layer (as opposed to the default vector based graphic) in order to enable PDF resizing and overlay manipulations.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_RASTERIZE = "rasterize";
    /**
     * Tells Cloudinary not to automatically trim the excess space from around a dynamic text string.
     */
    @Api(1.1)
    public static final String IMAGE_FLAG_TEXT_NO_TRIM = "text_no_trim";

    /**
     * The url generated based on the transformation params.
     */
    private final String mTransformedUrl;

    /**
     * The private constructor builder for the url.
     *
     * @param url The url.
     */
    HaloCloudinary(@NonNull final String url) {
        mTransformedUrl = url;
    }

    /**
     * Builder factory to generate a builder.
     *
     * @return The builder created.
     */
    @Api(1.1)
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Effect representation. Use factory Effects to generate each one.
     */
    @Keep
    public static final class Effect implements Parcelable {

        /**
         * The name of the effect.
         */
        @NonNull
        private String mEffect;

        /**
         * The creator.
         */
        public static final Parcelable.Creator<Effect> CREATOR = new Parcelable.Creator<Effect>() {
            /**
             * Parcel creation.
             * @param source The parcel source.
             * @return The effect created.
             */
            public Effect createFromParcel(Parcel source) {
                return new Effect(source);
            }

            /**
             * Creates the array of effects.
             * @param size The size of the new array.
             * @return The effects.
             */
            public Effect[] newArray(int size) {
                return new Effect[size];
            }
        };

        /**
         * Definition of the effect.
         *
         * @param effect The definition.
         */
        Effect(@NonNull String effect) {
            AssertionUtils.notNull(effect, "effect == null");
            mEffect = effect;
        }

        /**
         * Parcel creation.
         *
         * @param in The parcel.
         */
        protected Effect(Parcel in) {
            this.mEffect = in.readString();
        }

        @Override
        public String toString() {
            return "e_" + mEffect;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mEffect);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Effect effect = (Effect) o;

            return mEffect.equals(effect.mEffect);

        }

        @Override
        public int hashCode() {
            return mEffect.hashCode();
        }
    }

    /**
     * Factory for the effects.
     */
    @Keep
    public static final class Effects {

        /**
         * Private constructor for the effects.
         */
        Effects() {
        }

        /**
         * Adjust the image's hue (Range: -100 to 100, Default: 80).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect hue(Integer value) {
            if (value == null || !inRage(value, -100, 100)) {
                return new Effect("hue");
            }
            return new Effect("hue:" + value);
        }

        /**
         * Adjust the image's red channel (Range: -100 to 100, Default: 0).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect red(Integer value) {
            if (value == null || !inRage(value, -100, 100)) {
                return new Effect("red");
            }
            return new Effect("red:" + value);
        }

        /**
         * Adjust the image's green channel (Range: -100 to 100, Default: 0).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect green(Integer value) {
            if (value == null || !inRage(value, -100, 100)) {
                return new Effect("green");
            }
            return new Effect("green:" + value);
        }

        /**
         * Adjust the image's blue channel (Range: -100 to 100, Default: 0).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect blue(Integer value) {
            if (value == null || !inRage(value, -100, 100)) {
                return new Effect("blue");
            }
            return new Effect("blue:" + value);
        }

        /**
         * Negate image colors (negative).
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect negate() {
            return new Effect("negate");
        }

        /**
         * Adjust image brightness (Range: -99 to 100, Default: 80)
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect brightness(Integer value) {
            if (value == null || !inRage(value, -99, 100)) {
                return new Effect("brightness");
            }
            return new Effect("brightness:" + value);
        }

        /**
         * Change the color scheme of the image to sepia (Range: 1 to 100, Default: 80).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect sepia(Integer value) {
            if (value == null || !inRage(value, 1, 100)) {
                return new Effect("sepia");
            }
            return new Effect("sepia:" + value);
        }

        /**
         * Convert image to gray-scale (multiple shades of gray).
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect grayscale() {
            return new Effect("grayscale");
        }

        /**
         * Covert image to black and white.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect blackAndWhite() {
            return new Effect("blackwhite");
        }

        /**
         * Adjust the image's color saturation (Range: -100 to 100, Default: 80).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect saturation(Integer value) {
            if (value == null || !inRage(value, -100, 100)) {
                return new Effect("saturation");
            }
            return new Effect("saturation:" + value);
        }

        /**
         * Colorizes the image in grey by default (a different color can be specified by the color parameter). The extra value determines how strongly to apply the color (Range: 0 to 100, Default: 100).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect colorize(Integer value) {
            if (value == null || !inRage(value, 0, 100)) {
                return new Effect("colorize");
            }
            return new Effect("colorize:" + value);
        }

        /**
         * Adjust image contrast (Range: -100 to 100, Default: 0).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect contrast(Integer value) {
            if (value == null || !inRage(value, -100, 100)) {
                return new Effect("contrast");
            }
            return new Effect("contrast:" + value);
        }

        /**
         * Automatically adjust contrast.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect autoContrast() {
            return new Effect("auto_contrast");
        }

        /**
         * Apply a vibrance filter on the image (Range: -100 to 100, Default: 20).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect vibrance(Integer value) {
            if (value == null || !inRage(value, -100, 100)) {
                return new Effect("vibrance");
            }
            return new Effect("vibrance:" + value);
        }

        /**
         * Automatically adjust color balance.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect autoColor() {
            return new Effect("auto_color");
        }

        /**
         * Automatically adjust image colors, contrast and lightness.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect improve() {
            return new Effect("improve");
        }

        /**
         * Automatically adjust brightness.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect autoBrightness() {
            return new Effect("auto_brightness");
        }

        /**
         * Adjust the fill light of an image (Range: -100 to 100, Default: 0).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect fillLight(Integer value) {
            if (value == null || !inRage(value, -100, 100)) {
                return new Effect("fill_light");
            }
            return new Effect("fill_light:" + value);
        }

        /**
         * Automatically enhance an image to its best visual quality with the Viesus Automatic Image Enhancement add-on.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect viesusCorrect() {
            return new Effect("viesus_correct");
        }

        /**
         * Adjust the gamma level (Range: -50 to 150, Default: 0).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect gamma(Integer value) {
            if (value == null || !inRage(value, -50, 150)) {
                return new Effect("gamma");
            }
            return new Effect("gamma:" + value);
        }

        /**
         * Add an overlay image blended using the 'screen' blend executionMode. In this executionMode, each pixel of the image is made brighter according to the pixel value of the overlayed image.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect screen() {
            return new Effect("screen");
        }

        /**
         * Add an overlay image blended using the 'screen' blend executionMode. In this executionMode, each pixel of the image is made darker according to the pixel value of the overlayed image.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect multiply() {
            return new Effect("multiply");
        }

        /**
         * Add an overlay image blended using the 'overlay' blend executionMode. In this executionMode, each pixel of the image is made darker or brighter according to the pixel value of the overlayed image.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect overlay() {
            return new Effect("overlay");
        }

        /**
         * Make the background of the image transparent (or solid white for JPGs). The background is determined as all pixels that resemble the pixels on the edges of the image (Range: 0 to 100, Default: 10).
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect makeTransparent() {
            return new Effect("make_transparent");
        }

        /**
         * Detect and remove image edges whose color is similar to corner pixels (color similarity tolerance range: 0 to 100, Default: 10).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect trim(Integer value) {
            if (value == null || !inRage(value, 0, 100)) {
                return new Effect("trim");
            }
            return new Effect("trim:" + value);
        }

        /**
         * Add a bottom right gray shadow. The shadow can be customized with the color, x and y parameters. (Strength range: 0 to 100, Default: 40)
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect shadow(Integer value) {
            if (value == null || !inRage(value, 0, 100)) {
                return new Effect("shadow");
            }
            return new Effect("shadow:" + value);
        }

        /**
         * Distorts the image to a new shape according to 8 values separated by colons (:), representing the new coordinates for each of the image's 4 corners, in clockwise order from the top-left corner.
         *
         * @param topLeft     Top left corner.
         * @param topRight    Top right corner.
         * @param bottomRight Bottom right corner.
         * @param bottomLeft  Botton left corner.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect distort(Pair<Integer, Integer> topLeft, Pair<Integer, Integer> topRight, Pair<Integer, Integer> bottomRight, Pair<Integer, Integer> bottomLeft) {
            return new Effect("distort:" +
                    topLeft.first + ":" + topLeft.second + ":" +
                    topRight.first + ":" + topRight.second + ":" +
                    bottomRight.first + ":" + bottomRight.second + ":" +
                    bottomLeft.first + ":" + bottomLeft.second);
        }

        /**
         * Skews the image accordingHaloCloudinary.Effects.distort(new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1)).toString() to two specified values in degrees separated by a colon (:), representing how much to skew the image on the x-axis and y-axis respectively. Negative values skew the image in the opposite direction.
         *
         * @param xAxis X axis.
         * @param yAxis Y axis.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect shear(float xAxis, float yAxis) {
            return new Effect("shear:" + xAxis + ":" + yAxis);
        }

        /**
         * The pixels in the image are displaced according to the intensity of the pixels in another specified image (a gradient map specified with the overlay parameter). The stronger the intensity of each pixel in the gradient map, the bigger the displacement of the corresponding pixel in the base image. The amount of displacement in the horizontal and vertical directions is controlled by using the 'x' and 'y' parameters respectively.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect displace() {
            return new Effect("displace");
        }

        /**
         * Apply an oil painting effect (Range: 0 to 100, Default: 30).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect oilPaint(Integer value) {
            if (value == null || !inRage(value, 0, 100)) {
                return new Effect("oil_paint");
            }
            return new Effect("oil_paint:" + value);
        }

        /**
         * Automatically remove red eyes in an image.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect redEye() {
            return new Effect("redeye");
        }

        /**
         * Automatically remove red eyes with the Advanced Facial Attribute Detection add-on.
         *
         * @return The effect.
         */
        @Api(1.1)
        public static Effect advancedRedEye() {
            return new Effect("adv_redeye");
        }

        /**
         * Apply a vignette effect (Range: 0 to 100, Default: 20).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect vignette(Integer value) {
            if (value == null || !inRage(value, 0, 100)) {
                return new Effect("vignette");
            }
            return new Effect("vignette:" + value);
        }

        /**
         * Apply a gradient fade effect on the image (Range: 0 to 100, Default: 20).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect gradientFade(Integer value) {
            if (value == null || !inRage(value, 1, 100)) {
                return new Effect("gradient_fade");
            }
            return new Effect("gradient_fade:" + value);
        }

        /**
         * Apply a pixelation effect. The additional value determines the width in pixels of each pixelation square (Range: 1 to 200, Default: 5).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect pixelate(Integer value) {
            if (value == null || !inRage(value, 1, 200)) {
                return new Effect("pixelate");
            }
            return new Effect("pixelate:" + value);
        }

        /**
         * Automatically pixelate all detected faces in the image. The additional value determines the width in pixels of each pixelation square (Range: 1 to 200, Default: 5).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect pixelateFaces(Integer value) {
            if (value == null || !inRage(value, 1, 200)) {
                return new Effect("pixelate_faces");
            }
            return new Effect("pixelate_faces:" + value);
        }

        /**
         * Apply a blurring filter on the image (Range: 1 to 2000, Default: 100).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect blur(Integer value) {
            if (value == null || !inRage(value, 1, 2000)) {
                return new Effect("blur");
            }
            return new Effect("blur:" + value);
        }

        /**
         * Automatically blur all detected faces in the image (Range: 1 to 2000, Default: 500).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect blurFaces(Integer value) {
            if (value == null || !inRage(value, 1, 2000)) {
                return new Effect("blur_faces");
            }
            return new Effect("blur_faces:" + value);
        }

        /**
         * Apply a sharpening filter (Range: 1 to 2000, Default: 100).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect sharpen(Integer value) {
            if (value == null || !inRage(value, 1, 2000)) {
                return new Effect("sharpen");
            }
            return new Effect("sharpen:" + value);
        }

        /**
         * Apply an unsharp mask filter (Range: 1 to 2000, Default: 100).
         *
         * @param value The value.
         * @return The effect.
         */
        @Api(1.1)
        public static Effect unsharpMask(Integer value) {
            if (value == null || !inRage(value, 1, 2000)) {
                return new Effect("unsharp_mask");
            }
            return new Effect("unsharp_mask:" + value);
        }

        /**
         * Checks if a value is in range.
         *
         * @param value The value to check.
         * @param from  From range value.
         * @param to    To range value.
         * @return True if the value is in range.
         */
        private static boolean inRage(@NonNull Integer value, int from, int to) {
            return value >= Math.min(from, to) && value <= Math.max(from, to);
        }
    }

    /**
     * Rotation factory.
     */
    @Keep
    public static final class Rotations {

        /**
         * Rotation private constructor for the factory.
         */
        Rotations() {
        }

        /**
         * Rotate image 90 degrees clockwise only if the requested aspect ratio does not match the image's aspect ratio.
         *
         * @return The rotation.
         */
        @NonNull
        @Api(1.1)
        public static Rotation autoRight() {
            return new Rotation("auto_right");
        }

        /**
         * Rotate image 90 degrees counterclockwise only if the requested aspect ratio does not match the image's aspect ratio.
         *
         * @return The rotation.
         */
        @NonNull
        @Api(1.1)
        public static Rotation autoLeft() {
            return new Rotation("auto_left");
        }

        /**
         * By default, the image is automatically rotated according to the EXIF data stored by the camera when the image was taken. Set the angle to 'ignore' if you do not want the image to be automatically rotated.
         *
         * @return The rotation.
         */
        @NonNull
        @Api(1.1)
        public static Rotation ignore() {
            return new Rotation("ignore");
        }

        /**
         * Vertical mirror flip of the image.
         *
         * @return The rotation.
         */
        @NonNull
        @Api(1.1)
        public static Rotation verticalFlip() {
            return new Rotation("vflip");
        }

        /**
         * Horizontal mirror flip of the image.
         *
         * @return The rotation.
         */
        @NonNull
        @Api(1.1)
        public static Rotation horizontalFlip() {
            return new Rotation("hflip");
        }

        /**
         * Custom degrees. Ex. 90 or -90
         *
         * @param degrees The number of degrees in the rotation.
         * @return The rotation.
         */
        @NonNull
        @Api(1.1)
        public static Rotation custom(float degrees) {
            return new Rotation(String.valueOf(degrees));
        }
    }

    /**
     * Rotation representation class.
     */
    @Keep
    public static final class Rotation implements Parcelable {

        /**
         * The rotation value.
         */
        @NonNull
        private String mRotation;

        /**
         * The creator.
         */
        public static final Parcelable.Creator<Rotation> CREATOR = new Parcelable.Creator<Rotation>() {
            /**
             * Creates from parcel the rotation object.
             * @param source The source.
             * @return The rotation created.
             */
            public Rotation createFromParcel(Parcel source) {
                return new Rotation(source);
            }

            /**
             * Creates an array as parcelanele for the rotations.
             * @param size The size.
             * @return The rotation array.
             */
            public Rotation[] newArray(int size) {
                return new Rotation[size];
            }
        };

        /**
         * The rotation private constructor. Use Rotations factory to create
         * them.
         *
         * @param rotation The rotation id value.
         */
        Rotation(@NonNull String rotation) {
            AssertionUtils.notNull(rotation, "rotation == null");
            mRotation = rotation;
        }

        protected Rotation(Parcel in) {
            this.mRotation = in.readString();
        }

        @Override
        public String toString() {
            return "a_" + mRotation;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mRotation);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Rotation rotation = (Rotation) o;

            return mRotation.equals(rotation.mRotation);

        }

        @Override
        public int hashCode() {
            return mRotation.hashCode();
        }
    }

    /**
     * The builder class to generate transformations.
     */
    @Keep
    public static class Builder implements Parcelable {

        /**
         * The cloudinary definition endpoint.
         */
        private static final String CLOUDINARY = "cloudinary.com";

        /**
         * The part of the url taht will be used to insert the transformation
         * parameters.
         */
        private static final String URL_DETECTOR = "/upload/";

        /**
         * The params that will be inserted into the url.
         */
        @NonNull
        private List<String> mParams;

        /**
         * The creator.
         */
        public static final Parcelable.Creator<Builder> CREATOR = new Parcelable.Creator<Builder>() {
            public Builder createFromParcel(Parcel source) {
                return new Builder(source);
            }

            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };

        /**
         * The builder with the url.
         */
        Builder() {
            mParams = new ArrayList<>();
        }

        protected Builder(Parcel in) {
            this.mParams = in.createStringArrayList();
        }

        /**
         * The required width of a transformed image or an overlay. Can be specified separately or together with the height value.
         *
         * @param pixels The width in pixels.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder width(int pixels) {
            mParams.add("w_" + pixels);
            return this;
        }

        /**
         * The required width of a transformed image or an overlay. Can be specified separately or together with the height value.
         *
         * @param percentage The width in percentage.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder width(float percentage) {
            mParams.add("w_" + percentage);
            return this;
        }

        /**
         * The required height of a transformed image or an overlay. Can be specified separately or together with the width value.
         *
         * @param pixels The height in pixels.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder height(int pixels) {
            mParams.add("h_" + pixels);
            return this;
        }

        /**
         * The required height of a transformed image or an overlay. Can be specified separately or together with the width value.
         *
         * @param percentage The height in percentage.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder height(float percentage) {
            mParams.add("h_" + percentage);
            return this;
        }

        /**
         * A crop executionMode that determines how to transform the image for fitting into the desired width & height dimensions.
         *
         * @param cropMode The crop executionMode.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder crop(@Nullable @CropMode String cropMode) {
            if (cropMode != null) {
                mParams.add("c_" + cropMode);
            }
            return this;
        }

        /**
         * Resize or crop the image to a new aspect ratio. This parameter is used together with a specified crop executionMode that determines how the image is adjusted to the new dimensions.
         * 1.5 -> Crop the image to an aspect ratio of 1.5
         *
         * @param aspectRatio The aspect ratio value.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder aspectRatio(@Nullable String aspectRatio) {
            if (aspectRatio != null) {
                mParams.add("ar_" + aspectRatio);
            }
            return this;
        }

        /**
         * Resize or crop the image to a new aspect ratio. This parameter is used together with a specified crop executionMode that determines how the image is adjusted to the new dimensions.
         * 16:9 -> Crop the image to an aspect ratio of 16:9
         *
         * @param percentage The aspect ratio percentage.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder aspectRatio(float percentage) {
            mParams.add("ar_" + percentage);
            return this;
        }

        /**
         * Decides which part of the image to keep while 'crop', 'pad' and 'fill' crop modes are used. For overlays, this decides where to place the overlay.
         *
         * @param gravity The gravity value.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder gravity(@Nullable @ImageGravity String gravity) {
            if (gravity != null) {
                mParams.add("g_" + gravity);
            }
            return this;
        }

        /**
         * Control how much of the original image surrounding the face to keep when using either the 'crop' or 'thumb' cropping modes with face detection (Default: 1.0).
         *
         * @param percent The percentage.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder zoom(float percent) {
            mParams.add("z_" + percent);
            return this;
        }

        /**
         * Horizontal position for custom-coordinates based cropping, overlay placement and certain region related effects.
         *
         * @param pixels The x displacement in pixels.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder xPos(int pixels) {
            mParams.add("x_" + pixels);
            return this;
        }

        /**
         * Horizontal position for custom-coordinates based cropping, overlay placement and certain region related effects.
         *
         * @param percentage The x percentage displacement.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder xPos(float percentage) {
            mParams.add("x_" + percentage);
            return this;
        }

        /**
         * Vertical position for custom-coordinates based cropping and overlay placement.
         *
         * @param pixels The y displacement pixels.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder yPos(int pixels) {
            mParams.add("y_" + pixels);
            return this;
        }

        /**
         * Vertical position for custom-coordinates based cropping and overlay placement.
         *
         * @param percentage The y percentage.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder yPos(float percentage) {
            mParams.add("y_" + percentage);
            return this;
        }

        /**
         * Control the JPEG, WebP, GIF, JPEG XR and JPEG 2000 compression quality. 1 is the lowest quality and 100 is the highest. Reducing quality generates JPG images much smaller in file size. The default values are:
         * JPEG: 90
         * WebP: 80 (100 quality for WebP is lossless)
         * GIF: lossless by default. 80 if the `lossy` flag is added
         * JPEG XR and JPEG 2000: 70
         *
         * @param percentage The percentage.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder quality(float percentage) {
            mParams.add("q_" + percentage);
            return this;
        }

        /**
         * Round the corners of an image or make it completely circular or oval (ellipse).
         *
         * @param pixels The radius.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder radius(int pixels) {
            mParams.add("r_" + pixels);
            return this;
        }

        /**
         * Generate an image with a circular crop using the 'max' radius value.
         *
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder maxRadius() {
            mParams.add("r_max");
            return this;
        }

        /**
         * Rotate or flip an image by the given degrees or automatically according to its orientation or available meta-data. Multiple modes can be applied by concatenating their names with a dot.
         *
         * @param rotation The rotation.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder rotate(@Nullable Rotation rotation) {
            if (rotation != null) {
                mParams.add(rotation.toString());
            }
            return this;
        }

        /**
         * Apply a filter or an effect on an image. The value includes the name of the effect and an additional parameter that controls the behavior of the specific effect (the range and default value are given where this parameter is relevant).
         *
         * @param effects The effects.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder addEffects(@NonNull Effect... effects) {
            AssertionUtils.notNull(effects, "effects == null");
            for (Effect effect : effects) {
                if (effect != null) {
                    mParams.add(effect.toString());
                }
            }
            return this;
        }

        /**
         * Adjust the opacity of the image and make it semi-transparent. 100 means opaque, while 0 is completely transparent.
         *
         * @param opacity The opacity.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder opacity(int opacity) {
            mParams.add("o_" + opacity);
            return this;
        }

        /**
         * Add a solid border around the image. The value has a CSS-like format: width_style_color. When using Cloudinary's client integration libraries, you can set the border values programmatically (e.g., :border => { :width => 4, :color => 'black' })
         *
         * @param border The border.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder border(@Nullable String border) {
            if (border != null) {
                mParams.add("bo_" + border);
            }
            return this;
        }

        /**
         * Defines the background color to use instead of transparent background areas when converting to JPG format or using the pad crop executionMode. The background color can be set as an RGB hex triplet (e.g. 'b_rgb:3e2222'), a 3 character RGB hex (e.g. 'b_rgb:777') or a named color (e.g. 'b_green').
         *
         * @param colorName The color hex.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder backgroundColor(@Nullable String colorName) {
            if (colorName != null) {
                mParams.add("b_" + colorName);
            }
            return this;
        }

        /**
         * Defines the background color to use instead of transparent background areas when converting to JPG format or using the pad crop executionMode. The background color can be set as an RGB hex triplet (e.g. 'b_rgb:3e2222'), a 3 character RGB hex (e.g. 'b_rgb:777') or a named color (e.g. 'b_green').
         *
         * @param color The color rgb.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder backgroundColorRGB(@Nullable String color) {
            if (color != null) {
                mParams.add("b_rgb:" + color);
            }
            return this;
        }

        /**
         * Add an overlay over the base image. You can control the dimension and position of the overlay using the width, height, x, y and gravity parameters. The overlay can take one of the following forms: identifier can be a public ID of an uploaded image or a specific image kind, public ID and settings.
         * l_<public ID of image> to add an overlay of an image.
         * l_text:<public ID of text image>:<text string> to add a text overlay with styling based on the public ID of an existing text image.
         * l_text:<style settings>:<text string> to add a text overlay using the given style settings.
         *
         * @param id The id of the overlay.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder overlay(@Nullable String id) {
            if (id != null) {
                mParams.add("l_" + id);
            }
            return this;
        }

        /**
         * Add an overlay over the base image. You can control the dimension and position of the overlay using the width, height, x, y and gravity parameters. The overlay can take one of the following forms: identifier can be a public ID of an uploaded image or a specific image kind, public ID and settings.
         * l_<public ID of image> to add an overlay of an image.
         * l_text:<public ID of text image>:<text string> to add a text overlay with styling based on the public ID of an existing text image.
         * l_text:<style settings>:<text string> to add a text overlay using the given style settings.
         *
         * @param text The text of the overlay.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder overlayText(@Nullable String text) {
            if (text != null) {
                mParams.add("l_text:" + text);
            }
            return this;
        }

        /**
         * Add an underlay image below a base partially-transparent image. You can control the dimensions and position of the underlay using the width, height, x, y and gravity parameters. The identifier can be a public ID of an uploaded image or a specific image kind, public ID and settings. The underlay parameter shares the same features as the overlay parameter.
         *
         * @param id The id.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder underlay(@Nullable String id) {
            if (id != null) {
                mParams.add("u_" + id);
            }
            return this;
        }

        /**
         * Specify the public ID of a placeholder image to use if the requested image or social network picture does not exist.
         *
         * @param id The id of the image.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder defaultImage(@Nullable String id) {
            if (id != null) {
                mParams.add("d_" + id);
            }
            return this;
        }

        /**
         * Controls the time delay between the frames of an animated image, in milliseconds.
         *
         * @param delay The delay.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder delay(int delay) {
            mParams.add("dl_" + delay);
            return this;
        }

        /**
         * Customize the color to use together with: text captions, the shadow effect and the colorize effect. The color can be set as an RGB hex triplet (e.g. 'co_rgb:3e2222'), a 3 character RGB hex (e.g. 'co_rgb:777') or a named color (e.g. 'co_green').
         *
         * @param colorId The color id.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder color(@Nullable String colorId) {
            if (colorId != null) {
                mParams.add("co_" + colorId);
            }
            return this;
        }

        /**
         * Customize the color to use together with: text captions, the shadow effect and the colorize effect. The color can be set as an RGB hex triplet (e.g. 'co_rgb:3e2222'), a 3 character RGB hex (e.g. 'co_rgb:777') or a named color (e.g. 'co_green').
         *
         * @param colorRGB The color rgb.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder colorRGB(@Nullable String colorRGB) {
            if (colorRGB != null) {
                mParams.add("co_rgb:" + colorRGB);
            }
            return this;
        }

        /**
         * Deliver the image in the specified device pixel ratio.
         *
         * @param ratio The ration.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder devicePixelRatio(float ratio) {
            mParams.add("dpr_" + ratio);
            return this;
        }

        /**
         * Given a multi-page file (PDF, animated GIF, TIFF), generate an image of a single page using the given index.
         *
         * @param page The page.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder page(int page) {
            mParams.add("pg_" + page);
            return this;
        }

        /**
         * Control the density to use while converting a PDF document to images. (range: 50-300, default: 150)
         *
         * @param dpi The dpis.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder dpi(int dpi) {
            mParams.add("dn_" + dpi);
            return this;
        }

        /**
         * Set one or more flags that alter the default transformation behavior. Separate multiple flags with a dot ('.').
         *
         * @param flags The flags.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder addFlags(@NonNull @ImageFlag String... flags) {
            AssertionUtils.notNull(flags, "flags == null");
            if (flags.length > 0) {
                String flagsConcat = TextUtils.join(".", flags);
                mParams.add("fl_" + flagsConcat);
            }
            return this;
        }

        /**
         * The named transformation stored in cloudinary.
         *
         * @param transform The transformation name.
         * @return The current builder.
         */
        @NonNull
        @Api(1.1)
        public Builder namedTransformation(@Nullable String transform) {
            if (transform != null) {
                mParams.add("t_" + transform);
            }
            return this;
        }

        /**
         * Builds the cloudinary item to load images.
         *
         * @param url The url.
         * @return The cloudinary object.
         */
        @NonNull
        @Api(1.1)
        public HaloCloudinary build(@NonNull String url) {
            //If the url supports transformations
            AssertionUtils.notNull(url, "url == null");
            String finalUrl = url;
            if (url.contains(CLOUDINARY)) {
                finalUrl = buildTransformation(url);
            }
            return new HaloCloudinary(finalUrl);
        }

        /**
         * Builds the transformation based on the parameters provided.
         *
         * @return The parameters provided.
         */
        private String buildTransformation(@NonNull String url) {
            StringBuilder transformedUrl = new StringBuilder(url);
            //Join params
            String params = TextUtils.join(",", mParams);
            //Insert the params if possible
            if (!params.isEmpty()) {
                params += "/";
                int index = url.indexOf(URL_DETECTOR);
                if (index != -1) {
                    transformedUrl.insert(index + URL_DETECTOR.length(), params);
                }
            }
            return transformedUrl.toString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(this.mParams);
        }
    }

    /**
     * Provides the transformed url.
     *
     * @return The transformed url.
     */
    @NonNull
    @Api(1.1)
    public String url() {
        return mTransformedUrl;
    }

    @Override
    public String toString() {
        return url();
    }
}
