package com.mobgen.halo.android.analytics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;

/**
 * Transaction analytics such us add to cart. It adds some sugar syntax for transactions.
 */
public class TransactionAnalytic {

    /**
     * The analytic wrapped.
     */
    private Analytic mAnalytic;

    /**
     * Create the transaction analytics with the created analytic.
     *
     * @param analytic The analytic created.
     */
    private TransactionAnalytic(@NonNull Analytic analytic) {
        mAnalytic = analytic;
    }

    /**
     * Provides the analytic instance built with the transaction.
     *
     * @return The analytic instance.
     */
    @Api(2.0)
    @NonNull
    public Analytic getAnalytic() {
        return mAnalytic;
    }

    /**
     * Creates the builder for the transaction analytic.
     *
     * @param name The name of the transaction.
     * @return The builder created.
     */
    @Api(2.0)
    @NonNull
    public static TransactionAnalytic.Builder builder(@Size(min = 1, max = 32) @NonNull String name) {
        return new Builder(name);
    }

    /**
     * Internal builder to create the transaction analytic.
     */
    public static class Builder implements IBuilder<TransactionAnalytic> {

        /**
         * The name of the analytic.
         */
        @NonNull
        private final String mName;
        /**
         * The quantity value.
         */
        private Integer mQuantity;
        /**
         * The price.
         */
        private Float mPrice;
        /**
         * The product name.
         */
        private String mProductName;
        /**
         * The product category.
         */
        private String mProductCategory;
        /**
         * The product id.
         */
        private String mProductId;

        /**
         * The currency of the payment.
         */
        private String mCurrency;

        /**
         * Constructor for the analytic.
         *
         * @param name The name of the analytic created.
         */
        private Builder(@Size(min = 1, max = 32) @NonNull String name) {
            mName = name;
        }

        /**
         * The quantity of the product.
         *
         * @param quantity The number of items purchased.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder quantity(@Nullable Integer quantity) {
            mQuantity = quantity;
            return this;
        }

        /**
         * The price of the product.
         *
         * @param price The price of the product.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder price(@Nullable Float price) {
            mPrice = price;
            return this;
        }

        /**
         * The product name.
         *
         * @param productName The name of the product.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder productName(@Nullable String productName) {
            mProductName = productName;
            return this;
        }

        /**
         * The product category.
         *
         * @param productCategory The name of the product category.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder productCategory(@Nullable String productCategory) {
            mProductCategory = productCategory;
            return this;
        }

        /**
         * The product id.
         *
         * @param productId The product id.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder productId(@NonNull String productId) {
            mProductId = productId;
            return this;
        }

        /**
         * The currency type of the transaction.
         * @param currency The currency of the transaction.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder currency(@NonNull String currency){
            mCurrency = currency;
            return this;
        }

        @Api(2.0)
        @NonNull
        @Override
        public TransactionAnalytic build() {
            Analytic analytic = new Analytic(mName);
            if (mProductId != null) {
                analytic.addParam(Analytic.Param.PRODUCT_ID, mProductId);
            }
            if (mQuantity != null) {
                analytic.addParam(Analytic.Param.QUANTITY, mQuantity);
            }
            if (mPrice != null) {
                analytic.addParam(Analytic.Param.PRICE, mPrice);
            }
            if (mProductName != null) {
                analytic.addParam(Analytic.Param.PRODUCT_NAME, mProductName);
            }
            if (mProductCategory != null) {
                analytic.addParam(Analytic.Param.PRODUCT_CATEGORY, mProductCategory);
            }
            if(mCurrency != null){
                analytic.addParam(Analytic.Param.CURRENCY, mCurrency);
            }
            return new TransactionAnalytic(analytic);
        }
    }

}
