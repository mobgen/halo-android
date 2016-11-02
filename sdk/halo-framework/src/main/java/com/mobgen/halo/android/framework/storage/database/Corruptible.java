package com.mobgen.halo.android.framework.storage.database;

/**
 * Corruptible interface that tells that something has been corrupted.
 */
interface Corruptible {

    /**
     * Notifies that the database is corrupted. Never call this method from outside, it will be called
     * by the HALO SDK in case this is needed.
     */
    void onCorrupted();
}
