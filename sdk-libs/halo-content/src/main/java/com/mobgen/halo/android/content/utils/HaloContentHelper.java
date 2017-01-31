package com.mobgen.halo.android.content.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper for the content instance. It helps creating and retrieving data related
 * to content instances.
 */
public final class HaloContentHelper {

    /**
     * Constructor to avoid the instances from the utils.
     */
    private HaloContentHelper() {
        // Avoid instances
    }

    /**
     * Creates the hash of the item.
     *
     * @param instance The content instance.
     * @return The hash of the item.
     * @throws HaloStorageGeneralException Error while creating the hash.
     */
    public static String createDatabaseId(@NonNull HaloContentInstance instance) throws HaloStorageGeneralException {
        try {
            return HaloUtils.sha1(instance.toString()) + "_" + instance.getItemId();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new HaloStorageGeneralException("Error while creating hash of the general content instance: " + instance.getItemId(), e);
        }
    }


    /**
     * Creates the default general content instance values for the database based on an instance.
     *
     * @param instance The instance to wrap;
     * @return The content values.
     * @throws HaloStorageGeneralException Error while creating the hash.
     */
    @NonNull
    private static ContentValues createContentValues(@NonNull HaloContentInstance instance, ContentValues values) throws HaloStorageGeneralException {
        values.put(HaloContentContract.Content.ID, instance.getItemId());
        values.put(HaloContentContract.Content.MODULE_ID, instance.getModuleId());
        values.put(HaloContentContract.Content.NAME, instance.getName());
        if (instance.getValues() != null) {
            values.put(HaloContentContract.Content.VALUES, instance.getValues().toString());
        }
        values.put(HaloContentContract.Content.AUTHOR, instance.getAuthor());
        values.put(HaloContentContract.Content.CREATED_AT, instance.getCreatedDate() != null ? instance.getCreatedDate().getTime() : null);
        values.put(HaloContentContract.Content.UPDATED_AT, instance.getLastUpdate() != null ? instance.getLastUpdate().getTime() : null);
        values.put(HaloContentContract.Content.PUBLISHED, instance.getPublishedDate() != null ? instance.getPublishedDate().getTime() : null);
        values.put(HaloContentContract.Content.REMOVED, instance.getRemoveDate() != null ? instance.getRemoveDate().getTime() : null);
        return values;
    }

    /**
     * Creates the content values for the search queries.
     *
     * @param instance   The instance.
     * @param values     The values to fromCursor the content.
     * @param expireDate The expire date.
     * @return The content values created.
     * @throws HaloStorageGeneralException The exception produced.
     */
    @NonNull
    public static ContentValues createSearchContentValues(@NonNull HaloContentInstance instance, ContentValues values, long expireDate) throws HaloStorageGeneralException {
        createContentValues(instance, values);
        values.put(HaloContentContract.ContentSearch.HASH_ID, createDatabaseId(instance));
        values.put(HaloContentContract.ContentSearch.EXPIRES_ON, expireDate);
        return values;
    }

    /**
     * Factory to fromCursor a general content instance from a raw item.
     *
     * @param cursor The raw used to fromCursor the item.
     * @param close  Close the raw.
     * @return The item generated.
     * @throws HaloStorageParseException while parsing the content as json.
     */
    public static HaloContentInstance fromCursor(Cursor cursor, boolean close) throws HaloStorageParseException {
        String itemId = cursor.getString(cursor.getColumnIndex(HaloContentContract.ContentSearch.ID));
        String moduleId = cursor.getString(cursor.getColumnIndex(HaloContentContract.ContentSearch.MODULE_ID));
        String name = cursor.getString(cursor.getColumnIndex(HaloContentContract.ContentSearch.NAME));
        JSONObject values = null;
        try {
            if (!cursor.isNull(cursor.getColumnIndex(HaloContentContract.ContentSearch.VALUES))) {
                values = new JSONObject(cursor.getString(cursor.getColumnIndex(HaloContentContract.ContentSearch.VALUES)));
            }
        } catch (JSONException e) {
            throw new HaloStorageParseException("Error parsing the general content values.", e);
        }
        String author = cursor.getString(cursor.getColumnIndex(HaloContentContract.ContentSearch.AUTHOR));
        Date publishedAt = null;
        if (!cursor.isNull(cursor.getColumnIndex(HaloContentContract.ContentSearch.PUBLISHED))) {
            publishedAt = new Date(cursor.getLong(cursor.getColumnIndex(HaloContentContract.ContentSearch.PUBLISHED)));
        }
        Date removedAt = null;
        if (!cursor.isNull(cursor.getColumnIndex(HaloContentContract.ContentSearch.REMOVED))) {
            removedAt = new Date(cursor.getLong(cursor.getColumnIndex(HaloContentContract.ContentSearch.REMOVED)));
        }
        Date createdAt = null;
        if (!cursor.isNull(cursor.getColumnIndex(HaloContentContract.ContentSearch.CREATED_AT))) {
            createdAt = new Date(cursor.getLong(cursor.getColumnIndex(HaloContentContract.ContentSearch.CREATED_AT)));
        }
        Date updatedAt = null;
        if (!cursor.isNull(cursor.getColumnIndex(HaloContentContract.ContentSearch.UPDATED_AT))) {
            updatedAt = new Date(cursor.getLong(cursor.getColumnIndex(HaloContentContract.ContentSearch.UPDATED_AT)));
        }
        if (close) {
            cursor.close();
        }
        //TODO store modulename, archivedAt and segmentation tags on database migration
        return new HaloContentInstance(itemId, null, moduleId, name, values, author, null, createdAt, updatedAt, publishedAt, removedAt, null);
    }

    /**
     * Creates a list of elements from a raw.
     *
     * @param cursor      The raw that will be used to fromCursor the items.
     * @param shouldClose Tells if the raw should be closed.
     * @return The list of elements.
     * @throws HaloStorageParseException while parsing the content as json.
     */
    @NonNull
    public static List<HaloContentInstance> createList(Cursor cursor, boolean shouldClose) throws HaloStorageParseException {
        List<HaloContentInstance> instances = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                instances.add(fromCursor(cursor, false));
            } while (cursor.moveToNext());
        }
        if (shouldClose) {
            cursor.close();
        }
        return instances;
    }

    /**
     * Creates a list parsed to some given content from a cursor that refers to {@link HaloContentInstance}.
     *
     * @param cursor      The cursor reference.
     * @param shouldClose True if this cursor should be closed.
     * @param clazz       The class to parse.
     * @param parser      The parser.
     * @return The
     */
    @NonNull
    public static <T> List<T> createList(@NonNull Cursor cursor, boolean shouldClose, @NonNull Class<T> clazz, @NonNull Parser.Factory parser) throws HaloStorageParseException, HaloParsingException {
        List<T> instances = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                instances.add(from(fromCursor(cursor, false), clazz, parser));
            } while (cursor.moveToNext());
        }
        if (shouldClose) {
            cursor.close();
        }
        return instances;
    }


    /**
     * Parses the values to a different class annotated with @SerializedName
     *
     * @param instance  The instance to parse.
     * @param typeToken The Gson type token.
     * @param parser    The parser used to parse the generic values.
     * @return The instance of the element.
     */
    @SuppressWarnings("all")
    @Keep
    @Api(2.0)
    @Nullable
    public static <T> T from(@NonNull HaloContentInstance instance, TypeReference<T> typeToken, Parser.Factory parser) throws HaloParsingException {
        if (instance.getValues() != null) {
            try {
                return (T) parser.deserialize(typeToken.getType()).convert(new ByteArrayInputStream(instance.getValues().toString().getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error parsing the values object.", e);
            }
        }
        return null;
    }

    /**
     * Parses the values to a different class annotated with @SerializedName
     *
     * @param instance The instance to parse.
     * @param clazz    The class on which this will be parsed.
     * @param parser   The parser used to parse the generic values.
     * @return The instance of the element.
     */
    @SuppressWarnings("all")
    @Keep
    @Api(2.0)
    @Nullable
    public static <T> T from(@NonNull HaloContentInstance instance, @NonNull Class<T> clazz, @NonNull Parser.Factory parser) throws HaloParsingException {
        if (instance.getValues() != null) {
            try {
                return (T) parser.deserialize(clazz).convert(new ByteArrayInputStream(instance.getValues().toString().getBytes()));
            } catch (IOException e) {
                throw new HaloParsingException("Error parsing the values object.", e);
            }
        }
        return null;
    }

    /**
     * Provides a single instance or null given the result.
     *
     * @param list The data list.
     * @return The item or null if not available.
     */
    @Nullable
    public static <P> P getSingleItem(@Nullable List<P> list) {
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * Unwraps a paginated list.
     *
     * @param data The data to unwrap.
     * @return The result obtained when unwrapping.
     */
    @NonNull
    public static <P, T extends Paginated<P>> HaloResultV2<List<P>> toUnPageResult(@NonNull HaloResultV2<T> data) {
        AssertionUtils.notNull(data, "haloResult == null");
        Paginated<P> pages = data.data();
        List<P> items = null;
        if (pages != null) {
            items = pages.data();
        }
        return new HaloResultV2<>(data.status(), items);
    }
}
