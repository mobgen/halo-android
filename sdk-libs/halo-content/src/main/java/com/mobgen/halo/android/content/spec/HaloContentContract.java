package com.mobgen.halo.android.content.spec;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Column;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Table;

/**
 * The storage contract for the content plugin.
 */
@Keep
public final class HaloContentContract {

    /**
     * Preferences contract name. This is where the preferences will be stored for the
     * HALO SDK.
     */
    public static final String HALO_CONTENT_STORAGE = "com.mobgen.halo.android.content.halo-content";

    /**
     * The database version for the HALO cache.
     */
    public static final int CURRENT_VERSION = HaloContentMigration2$0$0.VERSION;

    /**
     * Private constructor that does not allow instances.
     */
    private HaloContentContract() {
        // Does not allow instances
    }

    /**
     * Search table used to keep the search queries in the database.
     */
    @Keep
    @Table("HALO_GC_SEARCH")
    public interface ContentSearchQuery extends HaloTable {

        /**
         * Query hash to differentiate between different queries.
         */
        @Keep
        @Column(type = Column.Type.TEXT, isPrimaryKey = true)
        String QUERY_ID = "GCS_QUERY_ID";

        /**
         * The full query that will be used to debug the query done.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String DEBUG_QUERY = "GCS_QUERY_DEBUG";

        /**
         * The hash+id of the instances stored separated by comma.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String INSTANCE_IDS = "GCS_HASH_INSTANCE_IDS";

        /**
         * The number of the page for the pagination.
         */
        @Keep
        @Column(type = Column.Type.NUMERIC)
        String PAGINATION_PAGE = "GCS_QUERY_PAGE";

        /**
         * The limit of the pagination.
         */
        @Keep
        @Column(type = Column.Type.NUMERIC)
        String PAGINATION_LIMIT = "GCS_QUERY_LIMIT";

        /**
         * The count of the pagination items.
         */
        @Keep
        @Column(type = Column.Type.NUMERIC)
        String PAGINATION_COUNT = "GCS_QUERY_COUNT";
        /**
         * Clears the ttl for the search so it can be removed when it is not needed local anymore.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String EXPIRES_ON = "GCS_EXPIRES_ON";
    }

    /**
     * General content instance table that stores the full entities for the General Content information.
     * This is also used as a small copy for local use. This is the base class for general content based
     * tables.
     */
    @Keep
    public interface Content extends HaloTable {
        /**
         * The id of the instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT, isPrimaryKey = true)
        String ID = "GC_ID";
        /**
         * The middleware to which this instance belongs to.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String MODULE_ID = "GC_MODULE_ID";
        /**
         * The name of the middleware.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String NAME = "GC_NAME";
        /**
         * The values as a JSON String. This string can be used to build the
         * full entity.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String VALUES = "GC_CONTENT";
        /**
         * The author of the current instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String AUTHOR = "GC_AUTHOR";
        /**
         * Publication date.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String PUBLISHED = "GC_PUBLISHED_DATE";
        /**
         * Date of removal.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String REMOVED = "GC_REMOVED_DATE";
        /**
         * Determines what is the creation date of an entity.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String CREATED_AT = "GC_CREATION_DATE";
        /**
         * Determines what is the update date of an entity.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String UPDATED_AT = "GC_UPDATE_DATE";
    }


    /**
     * Contains the general content instances that has been cached using the search web service.
     */
    @Keep
    @Table(value = "HALO_GC", multipleKeys = true)
    public interface ContentSearch extends Content {
        /**
         * It is a concat between the hash and the id of the instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT, isPrimaryKey = true)
        String HASH_ID = "GC_HASH_ID";
        /**
         * Time during the instance will be alive.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String EXPIRES_ON = "GC_EXPIRES_ON";
    }

    /**
     * General content execution table that represents a synced module.
     */
    @Table("HALO_GC_SYNC")
    public interface ContentSync extends Content {
        /**
         * The date when the item was last updated.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String LAST_SYNCED = "GC_SYNC_DATE";

        /**
         * The module name of the synced instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String MODULE_NAME = "GC_SYNC_MODULE_NAME";
    }

    /**
     * Sync log table where the synchronization log will be stored.
     */
    @Keep
    @Table("HALO_GC_SYNC_LOG")
    public interface ContentSyncLog extends HaloTable {

        /**
         * Id of the item for the execution log.
         */
        @Keep
        @Column(type = Column.Type.INTEGER, isPrimaryKey = true)
        String ID = "SYNC_LOG_ID";

        /**
         * The module id for this synchronization.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String MODULE_NAME = "SYNC_MODULE_NAME";

        /**
         * The value of the locale.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String LOCALE = "SYNC_LOCALE_VALUE";

        /**
         * The sync date for the log.
         */
        @Keep
        @Column(type = Column.Type.INTEGER)
        String SYNC_DATE = "SYNC_DATE";

        /**
         * The creations produced during the execution.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String CREATIONS = "SYNC_LOG_CREATIONS";

        /**
         * The updates produced during the execution.
         */
        @Keep
        @Column(type = Column.Type.INTEGER)
        String UPDATES = "SYNC_LOG_UPDATES";

        /**
         * The deletions produced during the execution.
         */
        @Keep
        @Column(type = Column.Type.INTEGER)
        String DELETIONS = "SYNC_LOG_DELETIONS";
    }
}
