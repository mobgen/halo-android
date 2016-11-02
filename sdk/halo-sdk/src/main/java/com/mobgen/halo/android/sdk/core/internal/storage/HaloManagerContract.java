package com.mobgen.halo.android.sdk.core.internal.storage;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Column;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Table;

/**
 * Contract used for the content providers to create the tables and access the dataLocal data.
 */
@Keep
public final class HaloManagerContract {

    /**
     * Preferences contract name. This is where the preferences will be stored for the
     * HALO SDK.
     */
    public static final String HALO_MANAGER_STORAGE = "com.mobgen.halo.android.sdk.halo-manager";

    /**
     * The database version for the HALO cache.
     */
    public static final int CURRENT_VERSION = HaloMigration2$0$0.VERSION;

    /**
     * Private constructor to avoid new instances.
     */
    private HaloManagerContract() {
    }

    /**
     * The contract for the remote modules items.
     */
    @Keep
    @Table("HALO_REMOTE_MODULES")
    public interface RemoteModules extends HaloTable {
        /**
         * The id of the instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT, isPrimaryKey = true)
        String ID = "RM_MODULE_ID";
        /**
         * The name of the instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String NAME = "RM_NAME";
        /**
         * The customer for this instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String CUSTOMER = "RM_CUSTOMER";
        /**
         * This instance is enabled.
         */
        @Keep
        @Column(type = Column.Type.BOOLEAN)
        String ENABLED = "RM_ENABLED";
        /**
         * The internal id of the middleware.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String INTERNAL_ID = "RM_INTERNAL_ID";
        /**
         * This middleware is a single item type or not.
         */
        @Column(type = Column.Type.BOOLEAN)
        String SINGLE = "RM_SINGLE";
        /**
         * Determines what is the creation date of an entity.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String CREATED_AT = "RM_CREATION_DATE";
        /**
         * Determines what is the update date of an entity.
         */
        @Keep
        @Column(type = Column.Type.DATE)
        String UPDATED_AT = "RM_UPDATE_DATE";
    }
}
