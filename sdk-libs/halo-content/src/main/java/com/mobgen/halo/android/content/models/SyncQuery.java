package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;

/**
 * Synchronization options for the sync web services.
 */
@Keep
public class SyncQuery implements Parcelable {

    /**
     * One day in seconds.
     */
    public static final int CACHE_ONE_DAY = 24 * 60 * 60;

    /**
     * The creator for parcelables.
     */
    public static final Creator<SyncQuery> CREATOR = new Creator<SyncQuery>() {
        @Override
        public SyncQuery createFromParcel(Parcel source) {
            return new SyncQuery(source);
        }

        @Override
        public SyncQuery[] newArray(int size) {
            return new SyncQuery[size];
        }
    };

    /**
     * The thread policy.
     */
    @Threading.Policy
    private int mThreadingMode;
    /**
     * The current locale.
     */
    @HaloLocale.LocaleDefinition
    private String mLocale;
    /**
     * The module id.
     */
    private String mModuleName;

    /**
     * The server cache.
     */
    private int mServerCache;

    /**
     * The private constructor for the sync options.
     *
     * @param moduleName   The module name.
     * @param locale       The locale.
     * @param threadPolicy The thread policy.
     */
    private SyncQuery(@NonNull String moduleName, @Nullable String locale, @Threading.Policy int threadPolicy, int serverCache) {
        mThreadingMode = threadPolicy;
        mLocale = locale;
        mModuleName = moduleName;
        mServerCache = serverCache;
    }

    @SuppressWarnings("all")
    protected SyncQuery(Parcel in) {
        this.mThreadingMode = in.readInt();
        this.mLocale = in.readString();
        this.mModuleName = in.readString();
        this.mServerCache = in.readInt();
    }

    /**
     * Creates the builder of the sync options.
     *
     * @param moduleName    The module name.
     * @param threadingMode The threadPolicy mode.
     * @return The builder.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public static SyncQuery create(@NonNull String moduleName, @Threading.Policy int threadingMode) {
        return create(moduleName, null, threadingMode);
    }

    /**
     * Creates the builder of the sync options.
     *
     * @param moduleName    The module name.
     * @param locale        The locale definition.
     * @param threadingMode The threadPolicy mode.
     * @return The builder.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public static SyncQuery create(@NonNull String moduleName, @Nullable @HaloLocale.LocaleDefinition String locale, @Threading.Policy int threadingMode) {
        return new SyncQuery(moduleName, locale, threadingMode, CACHE_ONE_DAY);
    }

    /**
     * Creates the builder of the sync options.
     *
     * @param moduleName    The module name.
     * @param threadingMode The threadPolicy mode.
     * @param serverCache   The server cache time in seconds.
     * @return The builder.
     */
    @Keep
    @Api(2.4)
    @NonNull
    public static SyncQuery create(@NonNull String moduleName, @Threading.Policy int threadingMode, int serverCache) {
        return create(moduleName, null, threadingMode, serverCache);
    }

    /**
     * @param moduleName    The module name.
     * @param locale        The locale definition.
     * @param threadingMode The threadPolicy mode.
     * @param serverCache   The server cache time in seconds.
     * @return
     */
    @Keep
    @Api(2.4)
    @NonNull
    public static SyncQuery create(@NonNull String moduleName, @Nullable @HaloLocale.LocaleDefinition String locale, @Threading.Policy int threadingMode, int serverCache) {
        return new SyncQuery(moduleName, locale, threadingMode, serverCache);
    }

    /**
     * Provides the locale.
     *
     * @return The locale.
     */
    @Api(2.0)
    @Nullable
    @HaloLocale.LocaleDefinition
    public String getLocale() {
        return mLocale;
    }

    /**
     * Sets the locale definition.
     *
     * @param locale The locale.
     */
    @Keep
    @Api(2.0)
    public void setLocale(@HaloLocale.LocaleDefinition @Nullable String locale) {
        mLocale = locale;
    }

    /**
     * Provides the module id.
     *
     * @return The module id.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public String getModuleName() {
        return mModuleName;
    }

    /**
     * Provides the action options.
     *
     * @return The action options.
     */
    @Keep
    @Api(2.0)
    @Threading.Policy
    public int getThreadingMode() {
        return mThreadingMode;
    }


    /**
     * Provides the server cache time.
     *
     * @return The locale.
     */
    @Api(2.4)
    @Nullable
    public int getServerCache() {
        return mServerCache;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mThreadingMode);
        dest.writeString(this.mLocale);
        dest.writeString(this.mModuleName);
        dest.writeInt(this.mServerCache);
    }
}
