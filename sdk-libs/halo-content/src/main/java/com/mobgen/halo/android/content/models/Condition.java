package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;

import java.util.ArrayList;
import java.util.List;

/**
 * The conditions that are available for the search queries.
 */
@Keep
@JsonObject
public class Condition implements SearchExpression {

    /**
     * And trigger constant.
     */
    private static final String AND = "and";
    /**
     * Or trigger constant.
     */
    private static final String OR = "or";

    /**
     * The trigger name.
     */
    @JsonField(name = "condition")
    String mCondition;
    /**
     * The query items.
     */
    @JsonField(name = "operands")
    List<SearchExpression> mItems;

    public static final Parcelable.Creator<Condition> CREATOR = new Parcelable.Creator<Condition>() {
        public Condition createFromParcel(Parcel source) {
            return new Condition(source);
        }

        public Condition[] newArray(int size) {
            return new Condition[size];
        }
    };

    /**
     * Constructor for the trigger that supports the trigger given and the new queries as
     * part of the composite.
     *
     * @param condition The trigger.
     */
    private Condition(@NonNull String condition) {
        mCondition = condition;
        mItems = new ArrayList<>();
    }

    /**
     * Parsing empty constructor.
     */
    public Condition() {
        //Empty constructor for parsing
    }

    protected Condition(Parcel in) {
        this.mCondition = in.readString();
        Parcelable[] parcelables = in.readParcelableArray(SearchExpression.class.getClassLoader());
        mItems = new ArrayList<>();
        for (Parcelable item : parcelables) {
            mItems.add((SearchExpression) item);
        }
    }

    /**
     * Creates an and trigger with the query items.
     *
     * @return The trigger provided.
     */
    @Keep
    @Api(2.0)
    public static Condition and() {
        return new Condition(AND);
    }

    /**
     * Creates an or trigger with the query items.
     *
     * @return The trigger.
     */
    @Keep
    @Api(2.0)
    public static Condition or() {
        return new Condition(OR);
    }

    /**
     * Adds the expression to the current items.
     *
     * @param expression The expression.
     */
    public void add(SearchExpression expression) {
        mItems.add(expression);
    }

    /**
     * Provides the name of the trigger.
     *
     * @return The name of the trigger.
     */
    public String getName() {
        return mCondition;
    }

    /**
     * Provides the items that belong to the trigger.
     *
     * @return The items.
     */
    @NonNull
    public List<SearchExpression> getItems() {
        return mItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCondition);
        dest.writeParcelableArray(this.mItems.toArray(new SearchExpression[mItems.size()]), 0);
    }
}
