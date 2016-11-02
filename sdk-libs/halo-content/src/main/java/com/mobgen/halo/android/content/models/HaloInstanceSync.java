package com.mobgen.halo.android.content.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

import java.util.Date;
import java.util.List;

/**
 * This model represents the synchronization of a module.
 */
@Keep
@JsonObject
public class HaloInstanceSync {

    /**
     * Server timestamp of the synchronization.
     */
    @JsonField(name = "syncTimestamp")
    Date mSyncTimestamp;

    /**
     * Items created.
     */
    @JsonField(name = "created")
    List<HaloContentInstance> mCreated;

    /**
     * Items updated.
     */
    @JsonField(name = "updated")
    List<HaloContentInstance> mUpdated;

    /**
     * Ids of the deleted items.
     */
    @JsonField(name = "deleted")
    List<HaloContentInstance> mDeleted;

    /**
     * Instance sync constructor for parsers.
     */
    public HaloInstanceSync() {
        // Constructor for parsing
    }

    /**
     * Constructor for the instance execution.
     *
     * @param syncTimestamp The timestamp where it has been synced.
     * @param created       The created items instance.
     * @param updated       The updated item instances.
     * @param deleted       The deleted item instances.
     */
    public HaloInstanceSync(@NonNull Date syncTimestamp, @NonNull List<HaloContentInstance> created, @NonNull List<HaloContentInstance> updated, @NonNull List<HaloContentInstance> deleted) {
        mSyncTimestamp = syncTimestamp;
        mCreated = created;
        mUpdated = updated;
        mDeleted = deleted;
    }

    /**
     * Merges the current instance execution with the execution passed as parameter.
     *
     * @param sync The execution to merge.
     */
    public void mergeWith(@NonNull HaloInstanceSync sync) {
        AssertionUtils.notNull(sync, "sync == null");
        mSyncTimestamp = new Date(Math.max(mSyncTimestamp.getTime(), sync.mSyncTimestamp.getTime()));
        mCreated.addAll(sync.mCreated);
        mUpdated.addAll(sync.mUpdated);
        mDeleted.addAll(sync.mDeleted);
    }

    /**
     * Provides the synchronization timestamp.
     *
     * @return The date provided by the server for this execution.
     */
    public Date getSyncDate() {
        return mSyncTimestamp;
    }

    /**
     * Provides the created instances in the execution.
     *
     * @return The creations.
     */
    @NonNull
    public List<HaloContentInstance> getCreations() {
        return mCreated;
    }

    /**
     * Provides the updated instances in the execution.
     *
     * @return The updates.
     */
    @NonNull
    public List<HaloContentInstance> getUpdates() {
        return mUpdated;
    }

    /**
     * Provides the deletions in the execution.
     *
     * @return The deletions.
     */
    @NonNull
    public List<HaloContentInstance> getDeletions() {
        return mDeleted;
    }

    /**
     * Provides the number of items affected by this execution instance.
     *
     * @return The number of affected items.
     */
    public int getCount() {
        return mCreated.size() + mUpdated.size() + mDeleted.size();
    }
}
