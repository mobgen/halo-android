package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.mobgen.halo.android.cache.Assert.notNull;

public final class Query {

    @NonNull
    private String mGroupId;
    @NonNull
    private List<Where> mWheres;

    private Query(Builder builder) {
        mGroupId = builder.mGroupId;
        mWheres = builder.mWheres;
    }

    @NonNull
    public String groupId() {
        return mGroupId;
    }

    @NonNull
    public List<Where> conditions() {
        return mWheres;
    }

    @NonNull
    public static Query.Builder builder(@NonNull String groupId) {
        return new Builder(groupId);
    }

    public static class Builder {

        @NonNull
        private String mGroupId;

        @NonNull
        private List<Where> mWheres;

        private Builder(@NonNull String groupId) {
            notNull(groupId, "groupId");
            mGroupId = groupId;
            mWheres = new ArrayList<>(1);
        }

        public Builder where(String fieldName, Op op, String value) {
            notNull(fieldName, "fieldName");
            mWheres.add(new Where(fieldName, op, value));
            return this;
        }

        public Query build() {
            return new Query(this);
        }
    }

    public enum Op {
        EQ
    }

    public static class Where {

        private String mFieldName;
        private Op mOp;
        private String mValue;

        public Where(String fieldName, Op op, String value) {
            mFieldName = fieldName;
            mOp = op;
            mValue = value;
        }

        public String getFieldName() {
            return mFieldName;
        }

        public Op getOp() {
            return mOp;
        }

        public String getValue() {
            return mValue;
        }
    }
}
