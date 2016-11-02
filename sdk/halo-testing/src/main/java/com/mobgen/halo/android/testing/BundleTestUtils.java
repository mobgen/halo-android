package com.mobgen.halo.android.testing;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Bundle helper to make test against the bundle api, that is not mocked by default.
 */
public class BundleTestUtils {

    /**
     * Bundle builder factory method.
     *
     * @return The bundle builder created.
     */
    public static BundleBuilder builder() {
        return new BundleBuilder();
    }

    /**
     * Bundle builder.
     */
    public static class BundleBuilder {

        /**
         * The internal bundle.
         */
        private Bundle mBundle;

        private BundleBuilder() {
            mBundle = mock(Bundle.class);
        }

        public BundleBuilder putBoolean(String key, boolean value) {
            when(mBundle.getBoolean(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putByte(String key, byte value) {
            when(mBundle.getByte(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putChar(String key, char value) {
            when(mBundle.getChar(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putShort(String key, short value) {
            when(mBundle.getShort(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putInt(String key, int value) {
            when(mBundle.getInt(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putLong(String key, long value) {
            when(mBundle.getLong(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putFloat(String key, float value) {
            when(mBundle.getFloat(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putDouble(String key, double value) {
            when(mBundle.getDouble(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putString(String key, String value) {
            when(mBundle.getString(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putCharSequence(String key, CharSequence value) {
            when(mBundle.getCharSequence(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putBooleanArray(String key, boolean[] value) {
            when(mBundle.getBooleanArray(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putIntArray(String key, int[] value) {
            when(mBundle.getIntArray(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putLongArray(String key, long[] value) {
            when(mBundle.getLongArray(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putDoubleArray(String key, double[] value) {
            when(mBundle.getDoubleArray(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putStringArray(String key, String[] value) {
            when(mBundle.getStringArray(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putParcelable(String key, Parcelable value) {
            when(mBundle.getParcelable(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putParcelableArray(String key, Parcelable[] value) {
            when(mBundle.getParcelableArray(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putIntegerArrayList(String key, ArrayList<Integer> value) {
            when(mBundle.getIntegerArrayList(key)).thenReturn(value);
            return this;
        }


        public BundleBuilder putStringArrayList(String key, ArrayList<String> value) {
            when(mBundle.getStringArrayList(key)).thenReturn(value);
            return this;
        }


        public BundleBuilder putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
            when(mBundle.getCharSequenceArrayList(key)).thenReturn(value);
            return this;
        }


        public BundleBuilder putSerializable(String key, Serializable value) {
            when(mBundle.getSerializable(key)).thenReturn(value);
            return this;
        }


        public BundleBuilder putByteArray(String key, byte[] value) {
            when(mBundle.getByteArray(key)).thenReturn(value);
            return this;
        }


        public BundleBuilder putShortArray(String key, short[] value) {
            when(mBundle.getShortArray(key)).thenReturn(value);
            return this;
        }

        public BundleBuilder putCharArray(String key, char[] value) {
            when(mBundle.getCharArray(key)).thenReturn(value);
            return this;
        }


        public BundleBuilder putFloatArray(String key, float[] value) {
            when(mBundle.getFloatArray(key)).thenReturn(value);
            return this;
        }


        public BundleBuilder putCharSequenceArray(String key, CharSequence[] value) {
            when(mBundle.getCharSequenceArray(key)).thenReturn(value);
            return this;
        }


        public BundleBuilder putBundle(String key, Bundle value) {
            when(mBundle.getBundle(key)).thenReturn(value);
            return this;
        }

        public Bundle build() {
            return mBundle;
        }
    }
}
