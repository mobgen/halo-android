package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;

import java.util.List;

/**
 * Created by fernandosouto on 05/04/17.
 */

/**
 * Results from remote data source for a batch operation.
 */
@JsonObject
public class BatchOperationResults implements Parcelable {

    @JsonField(name = "operations")
    List<BatchOperationResult> mOperations;

    public BatchOperationResults() {

    }

    public static final Creator<BatchOperationResults> CREATOR = new Creator<BatchOperationResults>() {
        @Override
        public BatchOperationResults createFromParcel(Parcel in) {
            return new BatchOperationResults(in);
        }

        @Override
        public BatchOperationResults[] newArray(int size) {
            return new BatchOperationResults[size];
        }
    };

    /**
     * Get all result from batch operation.
     *
     * @return The content result.
     */
    @NonNull
    @Api(2.3)
    public List<BatchOperationResult> getContentResult() {
        return mOperations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected BatchOperationResults(Parcel in) {
        this.mOperations = in.createTypedArrayList(BatchOperationResult.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mOperations);
    }
}
