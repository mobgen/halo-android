package com.mobgen.halo.android.framework.storage.database;

import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

/**
 * The database error handle that notifies that the database has been corrupted.
 */
public class HaloDatabaseErrorHandler implements DatabaseErrorHandler {

    /**
     * This handler notifies that the database has been corrupted.
     */
    private Corruptible mCorruptible;

    /**
     * Empty constructor.
     */
    public HaloDatabaseErrorHandler() {
        //This constructor has nothing to do.
    }

    /**
     * Sets the database helper class. The helper can't be injected in the constructor given that
     * the helper has to be created after the database error handler.
     *
     * @param helper The helper.
     */
    @Api(2.0)
    public void setCorruptible(@NonNull Corruptible helper) {
        mCorruptible = helper;
    }

    /**
     * Notify when the corruption happens.
     *
     * @param dbObj The database object.
     */
    @Override
    public void onCorruption(SQLiteDatabase dbObj) {
        if (mCorruptible != null) {
            mCorruptible.onCorrupted();
        } else {
            Halog.d(getClass(), "The database has been corrupted and nobody has managed this situation. Please make sure the HALO SDK is configured properly.");
        }
    }
}