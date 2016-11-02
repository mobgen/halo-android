package com.mobgen.halo.android.translations.spec;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.storage.database.dsl.HaloTable;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Column;
import com.mobgen.halo.android.framework.storage.database.dsl.annotations.Table;

/**
 * Contract for the translations database.
 */
@Keep
public final class HaloTranslationsContract {

    /**
     * The current version of the storage.
     */
    public static final int CURRENT_VERSION = TranslationsMigration2$0$0.VERSION;

    /**
     * Translations database name.
     */
    public static final String HALO_TRANSLATIONS_STORAGE = "com.mobgen.halo.android.content.halo-translations";

    /**
     * Private constructor for the storage contract.
     */
    private HaloTranslationsContract() {
        //Do not allow instances for this contract
    }

    /**
     * Translations database contract.
     */
    @Keep
    @Table(value = "HALO_TRANSLATIONS")
    public interface Translations extends HaloTable {
        /**
         * Id of the related general content instance.
         */
        @Keep
        @Column(type = Column.Type.TEXT, isPrimaryKey = true)
        String ITEM_ID = "INSTANCE_ID";
        /**
         * The module id for the translations.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String MODULE_NAME = "MODULE_NAME";
        /**
         * The locale for the given translation.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String LOCALE = "LOCALE";
        /**
         * The translation key.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String KEY = "TRANSLATION_KEY";
        /**
         * The translation value.
         */
        @Keep
        @Column(type = Column.Type.TEXT)
        String VALUE = "TRANSLATION_VALUE";
    }
}
