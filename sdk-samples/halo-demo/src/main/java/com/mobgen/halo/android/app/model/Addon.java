package com.mobgen.halo.android.app.model;

import com.mobgen.halo.android.app.R;

/**
 * Addon representation.
 */
public class Addon {

    /**
     * The addon types available.
     */
    public enum AddonType {
        /**
         * Segmentation addon type.
         */
        SEGMENTATION(R.string.segmentation),
        /**
         * Analytics addon
         */
        ANALYTICS(R.string.analytics_addon),
        /**
         * Social login addon.
         */
        SOCIAL_LOGIN(R.string.social_login_addon),

        /**
         * Market Segmentation
         */
        MARKET_SEGMENTATION(R.string.market_segmentation);

        /**
         * Resource with the name of the type.
         */
        private int mStringResource;

        /**
         * Constructor of the addon types.
         *
         * @param resource The resource with the name of the addon.
         */
        AddonType(int resource) {
            mStringResource = resource;
        }

        /**
         * Provides the resource name of the type.
         *
         * @return The resource value.
         */
        public int getStringResource() {
            return mStringResource;
        }
    }

    /**
     * The addon type.
     */
    private AddonType mType;

    /**
     * Constructor of the addon.
     *
     * @param type The type for this addon.
     */
    public Addon(AddonType type) {
        this.mType = type;
    }

    /**
     * Provides the internal type of the addon.
     *
     * @return The addon type.
     */
    public AddonType getType() {
        return mType;
    }
}
