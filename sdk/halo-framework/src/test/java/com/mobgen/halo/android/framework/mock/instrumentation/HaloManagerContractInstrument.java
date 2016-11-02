package com.mobgen.halo.android.framework.mock.instrumentation;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Column;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Table;


/**
 * Contract used for the content to create the table
 */
@Keep
public final class HaloManagerContractInstrument {


    /**
     * The database version.
     */
    public static final int VERSION = 1;

    /**
     * Private constructor to avoid new instances.
     */
    private HaloManagerContractInstrument() {
    }

    /**
     * The contract for the remote modules items.
     */
    @Keep
    @Table("halotable")
    public interface HaloTableContentTest extends HaloTable {
        /**
         * The id of the instance.
         */
        @Keep
        @Column(type = Column.Type.INTEGER, isPrimaryKey = true )
        String id = "id";
        /**
         * The name of the instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String halo = "halo";
        /**
         * The ref of the instance.
         */
        @Keep
        @Column(type = Column.Type.REAL)
        String halo_ref = "halo_ref";
        /**
         * The count of the instance.
         */
        @Keep
        @Column(type = Column.Type.NUMERIC)
        String halo_count = "halo_count";
        /**
         * The ref of the instance.
         */
        @Keep
        @Column(type = Column.Type.BLOB)
        String halo_data = "halo_data";
    }
}

