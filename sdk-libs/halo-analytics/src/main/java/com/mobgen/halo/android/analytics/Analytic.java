package com.mobgen.halo.android.analytics;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

/**
 * The event that can be logged into the
 */
public class Analytic implements Parcelable {

    /**
     * The name of the analytic.
     */
    @NonNull
    private final String mName;

    /**
     * The bundle with the params.
     */
    @NonNull
    private Bundle mBundle;

    /**
     * Creator of the analytic as a parcelable.
     */
    public static final Parcelable.Creator<Analytic> CREATOR = new Parcelable.Creator<Analytic>() {
        @Override
        public Analytic createFromParcel(Parcel source) {
            return new Analytic(source);
        }

        @Override
        public Analytic[] newArray(int size) {
            return new Analytic[size];
        }
    };

    /**
     * The name of the analytic.
     *
     * @param name The name of the analytic.
     */
    @Api(2.0)
    public Analytic(@Size(min = 1, max = 32) @NonNull String name) {
        AssertionUtils.notNull(name, "name");
        mName = name;
        mBundle = new Bundle();
    }

    /**
     * Parcelable analytic.
     *
     * @param in The parcel on which the analytic is written.
     */
    @SuppressWarnings("all")
    protected Analytic(Parcel in) {
        this.mName = in.readString();
        this.mBundle = in.readBundle();
    }

    /**
     * Adds a parameter into the analytic map to log it.
     *
     * @param name  The name of the parameter.
     * @param param The parameter of the analytic.
     * @return The current analytic.
     */
    @Api(2.0)
    @NonNull
    public Analytic addParam(@NonNull String name, @Nullable String param) {
        AssertionUtils.notNull(name, "name");
        if (mBundle.size() >= 25) {
            throw new IllegalStateException("The maximum number of parameters is 25.");
        }
        mBundle.putString(name, param);
        return this;
    }

    @Api(2.0)
    @NonNull
    public Analytic addParam(@NonNull String name, float param) {
        AssertionUtils.notNull(name, "name");
        if (mBundle.size() >= 25) {
            throw new IllegalStateException("The maximum number of parameters is 25.");
        }
        mBundle.putFloat(name, param);
        return this;
    }

    @Api(2.0)
    @NonNull
    public Analytic addParam(@NonNull String name, int param) {
        AssertionUtils.notNull(name, "name");
        if (mBundle.size() >= 25) {
            throw new IllegalStateException("The maximum number of parameters is 25.");
        }
        mBundle.putInt(name, param);
        return this;
    }

    /**
     * Provides the params of the analytic.
     *
     * @return The params.
     */
    @Api(2.0)
    @NonNull
    public Bundle params() {
        return mBundle;
    }

    /**
     * Provides the name of the analytic.
     * @return The name of the analytic.
     */
    @Api(2.0)
    @NonNull
    @Size(min = 1, max = 32)
    public String name(){
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeBundle(this.mBundle);
    }

    /**
     * The default params available for analytics.
     */
    public static class Param {
        /**
         * Optional param for achievement_id.
         */
        public static final String ACHIEVEMENT_ID = "achievement_id";
        /**
         * Optional param for character.
         */
        public static final String CHARACTER = "character";
        /**
         * Optional param for travel_class.
         */
        public static final String TRAVEL_CLASS = "travel_class";
        /**
         * Optional param for content_type.
         */
        public static final String CONTENT_TYPE = "content_type";
        /**
         * Optional param for currency.
         */
        public static final String CURRENCY = "currency";
        /**
         * Optional param for coupon.
         */
        public static final String COUPON = "coupon";
        /**
         * Optional param for start_date.
         */
        public static final String START_DATE = "start_date";
        /**
         * Optional param for end_date.
         */
        public static final String END_DATE = "end_date";
        /**
         * Optional param for flight_number.
         */
        public static final String FLIGHT_NUMBER = "flight_number";
        /**
         * Optional param for group_id.
         */
        public static final String GROUP_ID = "group_id";
        /**
         * Optional param for item_category.
         */
        public static final String ITEM_CATEGORY = "item_category";
        /**
         * Optional param for item_id.
         */
        public static final String ITEM_ID = "item_id";
        /**
         * Optional param for item_location_id.
         */
        public static final String ITEM_LOCATION_ID = "item_location_id";
        /**
         * Optional param for item_name.
         */
        public static final String ITEM_NAME = "item_name";
        /**
         * Optional param for location.
         */
        public static final String LOCATION = "location";
        /**
         * Optional param for level.
         */
        public static final String LEVEL = "level";
        /**
         * Optional param for sign_up_method.
         */
        public static final String SIGN_UP_METHOD = "sign_up_method";
        /**
         * Optional param for number_of_nights.
         */
        public static final String NUMBER_OF_NIGHTS = "number_of_nights";
        /**
         * Optional param for number_of_passengers.
         */
        public static final String NUMBER_OF_PASSENGERS = "number_of_passengers";
        /**
         * Optional param for number_of_rooms.
         */
        public static final String NUMBER_OF_ROOMS = "number_of_rooms";
        /**
         * Optional param for destination.
         */
        public static final String DESTINATION = "destination";
        /**
         * Optional param for origin.
         */
        public static final String ORIGIN = "origin";
        /**
         * Optional param for product_category.
         */
        public static final String PRODUCT_CATEGORY = "product_category";
        /**
         * Optional param for product_id.
         */
        public static final String PRODUCT_ID = "product_id";
        /**
         * Optional param for product_name.
         */
        public static final String PRODUCT_NAME = "product_name";
        /**
         * Optional param for price.
         */
        public static final String PRICE = "price";
        /**
         * Optional param for quantity.
         */
        public static final String QUANTITY = "quantity";
        /**
         * Parameter of the request.
         */
        public static final String REQUEST_METHOD = "request_method";
        /**
         * Optional param for score.
         */
        public static final String SCORE = "score";
        /**
         * Optional param for shipping.
         */
        public static final String SHIPPING = "shipping";
        /**
         * Optional param for search_term.
         */
        public static final String SEARCH_TERM = "search_term";
        /**
         * Optional param for tax.
         */
        public static final String TAX = "tax";
        /**
         * Optional param for time.
         */
        public static final String TIME = "time";
        /**
         * Optional param for transaction_id.
         */
        public static final String TRANSACTION_ID = "transaction_id";
        /**
         * Optional param for url
         */
        public static final String URL = "url";
        /**
         * Optional param for value.
         */
        public static final String VALUE = "value";
        /**
         * Optional param for virtual_currency_name.
         */
        public static final String VIRTUAL_CURRENCY_NAME = "virtual_currency_name";

        private Param() {
        }
    }

    /**
     * Type of analytics that can be defined.
     */
    public static class Type {
        /**
         * Analytic name type: add_payment_info.
         */
        public static final String ADD_PAYMENT_INFO = "add_payment_info";
        /**
         * Analytic name type: add_to_cart.
         */
        public static final String ADD_TO_CART = "add_to_cart";
        /**
         * Analytic name type: add_to_wishlist.
         */
        public static final String ADD_TO_WISHLIST = "add_to_wishlist";
        /**
         * Analytic name type: app_open.
         */
        public static final String APP_OPEN = "app_open";
        /**
         * Analytic name type: begin_checkout.
         */
        public static final String BEGIN_CHECKOUT = "begin_checkout";
        /**
         * Analytic name type: click.
         */
        public static final String CLICK = "click";
        /**
         * Analytic name type: ecommerce_purchase.
         */
        public static final String ECOMMERCE_PURCHASE = "ecommerce_purchase";
        /**
         * Analytic name type: generate_lead.
         */
        public static final String GENERATE_LEAD = "generate_lead";
        /**
         * Analytic name type: join_group.
         */
        public static final String JOIN_GROUP = "join_group";
        /**
         * Analytic name type: level_up.
         */
        public static final String LEVEL_UP = "level_up";
        /**
         * Analytic name type: login.
         */
        public static final String LOGIN = "login";
        /**
         * Analytic name type: post_score.
         */
        public static final String POST_SCORE = "post_score";
        /**
         * Analytic name type: present_offer.
         */
        public static final String PRESENT_OFFER = "present_offer";
        /**
         * Analytic name type: purchase_refund.
         */
        public static final String PURCHASE_REFUND = "purchase_refund";
        /**
         * Analytic name type: request.
         */
        public static final String REQUEST = "request";
        /**
         * Analytic name type: search.
         */
        public static final String SEARCH = "search";
        /**
         * Analytic name type: select_content.
         */
        public static final String SELECT_CONTENT = "select_content";
        /**
         * Analytic name type: share.
         */
        public static final String SHARE = "share";
        /**
         * Analytic name type: sign_up.
         */
        public static final String SIGN_UP = "sign_up";
        /**
         * Analytic name type: spend_virtual_currency.
         */
        public static final String SPEND_VIRTUAL_CURRENCY = "spend_virtual_currency";
        /**
         * Analytic name type: tutorial_begin.
         */
        public static final String TUTORIAL_BEGIN = "tutorial_begin";
        /**
         * Analytic name type: tutorial_complete.
         */
        public static final String TUTORIAL_COMPLETE = "tutorial_complete";
        /**
         * Analytic name type: unlock_achievement.
         */
        public static final String UNLOCK_ACHIEVEMENT = "unlock_achievement";
        /**
         * Analytic name type: view_item.
         */
        public static final String VIEW_ITEM = "view_item";
        /**
         * Analytic name type: view_item_list.
         */
        public static final String VIEW_ITEM_LIST = "view_item_list";
        /**
         * Analytic name type: view_search_results.
         */
        public static final String VIEW_SEARCH_RESULTS = "view_search_results";

        /**
         * Private constructor to avoid instances.
         */
        private Type() {
            //Private constructor to avoid instances.
        }
    }
}
