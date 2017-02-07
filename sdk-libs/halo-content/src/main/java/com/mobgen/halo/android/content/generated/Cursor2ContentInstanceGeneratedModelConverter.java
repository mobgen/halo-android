package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.support.annotation.NonNull;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.PaginationInfo;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;

import java.util.List;

/**
 * @hide Parses the cursor to a generic model.
 */
public class Cursor2ContentInstanceGeneratedModelConverter<T> implements ISelectorConverter<Paginated<T>, Cursor> {

    /**
     * The class to convert.
     */
    private Class mClazz;

    /**
     * Creates a cursor to instance converter.
     *
     * @param clazz      The class to convert.
     */
    public Cursor2ContentInstanceGeneratedModelConverter(@NonNull Class clazz) {
        mClazz = clazz;
    }

    @NonNull
    @Override
    public HaloResultV2<Paginated<T>> convert(@NonNull HaloResultV2<Cursor> cursor) throws HaloStorageParseException, HaloStorageGeneralException {
        return new HaloResultV2<>(cursor.status(), parse(cursor.data()));
    }

    /**
     * Parses the cursor into a paginated generic model list.
     *
     * @param cursor The cursor.
     * @return The paginated instances.
     * @throws HaloStorageParseException   Error while parsing the data.
     * @throws HaloStorageGeneralException General storage error, for example produced when the database
     *                                     corrupts.
     */
    @NonNull
    private Paginated<T> parse(@NonNull Cursor cursor) throws HaloStorageParseException, HaloStorageGeneralException {
        try {
            //Bring gc instances
            List<T> instances = HaloContentHelper.createList(cursor, mClazz);

            //In case we find a search item bring the pagination info
            PaginationInfo info;
            info = new PaginationInfo(instances.size());
            //Just return
            return new Paginated<>(instances, info);
        } catch (Exception e) {
            throw new HaloStorageParseException("Error while creating list", e);
        }
    }
}