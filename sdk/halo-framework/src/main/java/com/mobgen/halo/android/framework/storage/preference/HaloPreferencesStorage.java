package com.mobgen.halo.android.framework.storage.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * The preferences storage manages the savings of preferences in an easier way that the default framework does and
 * also supports null values.
 */
public class HaloPreferencesStorage {

    /**
     * The preferences storage editor.
     */
    public class HaloPreferencesStorageEditor implements SharedPreferences.Editor {

        /**
         * The preference editor of the storage editor.
         */
        private final SharedPreferences.Editor mPreferencesEditor;

        /**
         * The shared preferences that will be edited in the future for halo.
         *
         * @param preferences The preferences.
         */
        @SuppressLint("CommitPrefEdits")
        public HaloPreferencesStorageEditor(SharedPreferences preferences) {
            mPreferencesEditor = preferences.edit();
        }

        @Api(1.0)
        @Override
        public SharedPreferences.Editor putString(@NonNull String key, @Nullable String value) {
            return mPreferencesEditor.putString(key, value);
        }

        @Api(1.0)
        @Override
        public SharedPreferences.Editor putStringSet(@NonNull String key, @Nullable Set<String> values) {
            return mPreferencesEditor.putStringSet(key, values);
        }

        @Api(1.0)
        @Override
        public SharedPreferences.Editor putInt(@NonNull String key, int value) {
            return mPreferencesEditor.putInt(key, value);
        }

        @Api(1.0)
        @Override
        public SharedPreferences.Editor putLong(@NonNull String key, long value) {
            return mPreferencesEditor.putLong(key, value);
        }

        @Api(1.0)
        @Override
        public SharedPreferences.Editor putFloat(@NonNull String key, float value) {
            return mPreferencesEditor.putFloat(key, value);
        }

        @Api(1.0)
        @Override
        public SharedPreferences.Editor putBoolean(@NonNull String key, boolean value) {
            return mPreferencesEditor.putBoolean(key, value);
        }

        /**
         * Stores in preferences a json object as a string.
         *
         * @param key   The key for the preferences.
         * @param value The value to store.
         * @return The current editor.
         */
        @Api(1.0)
        public SharedPreferences.Editor putJson(@NonNull String key, @Nullable JSONObject value) {
            String result = null;
            if (value != null) {
                result = value.toString();
            }
            return putString(key, result);
        }

        /**
         * Stores in preferences a json array as a string.
         *
         * @param key   The key for the preferences.
         * @param value The value to store.
         * @return The current editor.
         */
        @Api(1.0)
        public SharedPreferences.Editor putJsonArray(@NonNull String key, @Nullable JSONArray value) {
            String result = null;
            if (value != null) {
                result = value.toString();
            }
            return putString(key, result);
        }

        @Api(1.0)
        @Override
        public SharedPreferences.Editor remove(String key) {
            return mPreferencesEditor.remove(key);
        }

        @Api(1.0)
        @Override
        public SharedPreferences.Editor clear() {
            return mPreferencesEditor.clear();
        }

        @Api(1.0)
        @Override
        public boolean commit() {
            boolean result = mPreferencesEditor.commit();
            if (result) {
                mCurrentEditor = null;
            }
            return result;
        }

        @Api(1.0)
        @Override
        public void apply() {
            mPreferencesEditor.apply();
        }
    }

    /**
     * The context for the halo preferences.
     */
    private final Context mContext;

    /**
     * Joins all the modifications of the editor in the same object.
     */
    private HaloPreferencesStorageEditor mCurrentEditor;

    /**
     * The preferences id.
     */
    private final String mPreferencesId;

    /**
     * The application context
     *
     * @param ctx          The context.
     * @param preferenceId The preferences id.
     */
    @Api(1.0)
    public HaloPreferencesStorage(@NonNull Context ctx, @NonNull String preferenceId) {
        AssertionUtils.notNull(ctx, "context");
        AssertionUtils.notNull(preferenceId, "preferenceId");
        mContext = ctx;
        mPreferencesId = preferenceId;
    }

    /**
     * Creates an editor to hold all the modifications. Every call to edit should finish with a call to commit to persist
     * all the changes in the android storage. All the changes done to an edit instance will be cached until a call
     * to commit is done.
     *
     * @return The editor to hold all the modifications.
     */
    @Api(1.0)
    public HaloPreferencesStorageEditor edit() {
        if (mCurrentEditor == null) {
            mCurrentEditor = new HaloPreferencesStorageEditor(getSharedPreferences());
        }
        return mCurrentEditor;
    }

    /**
     * The id of the preferences file.
     *
     * @return The name of the preferences file id.
     */
    @Api(1.0)
    @Nullable
    public String getPreferenceId() {
        return mPreferencesId;
    }

    /**
     * Provides the shared preferences with the context initialized in executionMode private.
     *
     * @return The shared preferences.
     */
    @Api(1.0)
    @NonNull
    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(mPreferencesId, Context.MODE_PRIVATE);
    }

    /**
     * Provides a string from the shared preferences.
     *
     * @param propertyName The property name stored in the preferences.
     * @param defaultValue The default value to retrieve.
     * @return The value obtained.
     */
    @Api(1.0)
    @Nullable
    public String getString(@NonNull String propertyName, @Nullable String defaultValue) {
        return getSharedPreferences().getString(propertyName, defaultValue);
    }

    /**
     * Provides a set of strings from the shared preferences storage.
     *
     * @param propertyName The property name stored in the preferences.
     * @param value        The default value in case of this does not exists.
     * @return The value obtained.
     */
    @Api(1.0)
    @Nullable
    public Set<String> getStringSet(@NonNull String propertyName, @Nullable Set<String> value) {
        return getSharedPreferences().getStringSet(propertyName, value);
    }

    /**
     * Provides an integer value stored in shared preferences.
     *
     * @param propertyName The property name stored in the preferences.
     * @param value        The default value in case of this does not exist.
     * @return The integer value stored in preferences.
     */
    @Api(1.0)
    @Nullable
    public Integer getInteger(@NonNull String propertyName, @Nullable Integer value) {
        if (getSharedPreferences().contains(propertyName)) {
            int val = value != null ? value : 0;
            return getSharedPreferences().getInt(propertyName, val);
        }
        return value;
    }

    /**
     * Provides a long value stored in shared preferences.
     *
     * @param propertyName The property name stored in the preferences.
     * @param value        The default value in case of this does not exist.
     * @return The long value stored in preferences.
     */
    @Api(1.0)
    @Nullable
    public Long getLong(@NonNull String propertyName, @Nullable Long value) {
        if (getSharedPreferences().contains(propertyName)) {
            long val = value != null ? value : 0L;
            return getSharedPreferences().getLong(propertyName, val);
        }
        return value;
    }

    /**
     * Provides a boolean value stored in shared preferences.
     *
     * @param propertyName The property name stored in the preferences.
     * @param value        The default value in case of this does not exist.
     * @return The boolean value stored in preferences.
     */
    @Api(1.0)
    @Nullable
    public Boolean getBoolean(@NonNull String propertyName, @Nullable Boolean value) {
        if (getSharedPreferences().contains(propertyName)) {
            boolean val = value != null ? value : false;
            return getSharedPreferences().getBoolean(propertyName, val);
        }
        return value;
    }

    /**
     * Provides a float value stored in shared preferences.
     *
     * @param propertyName The property name stored in the preferences.
     * @param value        The default value in case of this does not exist.
     * @return The float value stored in preferences.
     */
    @Api(1.0)
    @Nullable
    public Float getFloat(@NonNull String propertyName, @Nullable Float value) {
        if (getSharedPreferences().contains(propertyName)) {
            float val = value != null ? value : 0f;
            return getSharedPreferences().getFloat(propertyName, val);
        }
        return value;
    }

    /**
     * Gets a json array from the preferences of the system.
     *
     * @param propertyName The property name to store.
     * @param defaultValue The default value to provide in case of this key does not exist.
     * @return The json array provided.
     * @throws JSONException Exception thrown because the parsing was not successful.
     */
    @Api(1.0)
    @Nullable
    public JSONArray getJsonArray(@NonNull String propertyName, @Nullable JSONArray defaultValue) throws JSONException {
        if (getSharedPreferences().contains(propertyName)) {
            String val = defaultValue != null ? defaultValue.toString() : null;
            return new JSONArray(getSharedPreferences().getString(propertyName, val));
        }
        return defaultValue;
    }

    /**
     * Gets a json object from the preferences of the system.
     *
     * @param propertyName The property name to store.
     * @param defaultValue The default value to provide in case of this key does not exist.
     * @return The json array provided.
     * @throws JSONException Exception thrown because the parsing was not successful.
     */
    @Api(1.0)
    @Nullable
    public JSONObject getJsonObject(@NonNull String propertyName, @Nullable JSONObject defaultValue) throws JSONException {
        if (getSharedPreferences().contains(propertyName)) {
            String val = defaultValue != null ? defaultValue.toString() : null;
            return new JSONObject(getSharedPreferences().getString(propertyName, val));
        }
        return defaultValue;
    }
}
