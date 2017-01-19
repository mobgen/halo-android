package com.mobgen.halo.android.content.search;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

import java.util.Date;

/**
 * Factory for common search options. This helps to get items that
 * has been published or removed or so.
 */
@Keep
public final class SearchQueryBuilderFactory {

    /**
     * Empty constructor for the query builder factory.
     */
    private SearchQueryBuilderFactory() {
        //Do nothing in this constructor
    }

    /**
     * Brings all the published items for the given module.
     *
     * @param moduleName  The module name.
     * @param searchTag The tag for this search.
     * @return The options built.
     */
    @NonNull
    @Api(2.0)
    @Keep
    public static SearchQuery.Builder getPublishedItems(@NonNull String moduleName, @NonNull String searchTag) {
        AssertionUtils.notNull(moduleName, "moduleName");
        AssertionUtils.notNull(searchTag, "searchTag");
        Date now = new Date();
        return SearchQuery.builder()
                .moduleName(moduleName)
                .beginMetaSearch()
                .lte("publishedAt", now)
                .and()
                .eq("deletedAt", null)
                .and()
                .beginGroup()
                .gt("removedAt", now)
                .or()
                .eq("removedAt", null)
                .endGroup()
                .end()
                .searchTag(searchTag);
    }

    /**
     * Brings all the published items for the given module filtered by name
     *
     * @param moduleName  The module name.
     * @param searchTag The tag for this search.
     * @param queryPattern The instance query pattern to search.
     * @return The options built.
     */
    @NonNull
    @Api(2.2)
    @Keep
    public static SearchQuery.Builder getPublishedItemsByName(@NonNull String moduleName, @NonNull String searchTag, @Nullable String queryPattern) {
        AssertionUtils.notNull(moduleName, "moduleName");
        AssertionUtils.notNull(searchTag, "searchTag");
        Date now = new Date();
        if(queryPattern!=null && !queryPattern.isEmpty()) {
            return SearchQuery.builder()
                    .moduleName(moduleName)
                    .beginMetaSearch()
                    .lte("publishedAt", now)
                    .and()
                    .eq("deletedAt", null)
                    .and()
                    .beginGroup()
                    .gt("removedAt", now)
                    .or()
                    .eq("removedAt", null)
                    .endGroup()
                    .and()
                    .beginGroup()
                    .like("name", queryPattern)
                    .endGroup()
                    .end()
                    .searchTag(searchTag);
        } else {
            return SearchQuery.builder()
                    .moduleName(moduleName)
                    .beginMetaSearch()
                    .lte("publishedAt", now)
                    .and()
                    .eq("deletedAt", null)
                    .and()
                    .beginGroup()
                    .gt("removedAt", now)
                    .or()
                    .eq("removedAt", null)
                    .endGroup()
                    .end()
                    .searchTag(searchTag);
        }
    }

    /**
     * Provides the expired items.
     *
     * @param moduleName  The module name.
     * @param searchTag The tag for this search.
     * @return The options created.
     */
    @NonNull
    @Api(2.0)
    @Keep
    public static SearchQuery.Builder getExpiredItems(@NonNull String moduleName, @NonNull String searchTag) {
        AssertionUtils.notNull(moduleName, "moduleName");
        AssertionUtils.notNull(searchTag, "searchTag");
        Date now = new Date();
        return SearchQuery.builder()
                .moduleIds(moduleName)
                .beginMetaSearch()
                .lte("removedAt", now)
                .and()
                .eq("deletedAt", null)
                .end();
    }

    /**
     * Provides the archived items.
     *
     * @param moduleName  The module name.
     * @param searchTag The tag for this search.
     * @return The options created
     */
    @NonNull
    @Api(2.0)
    @Keep
    public static SearchQuery.Builder getArchivedItems(@NonNull String moduleName, @NonNull String searchTag) {
        AssertionUtils.notNull(moduleName, "moduleName");
        AssertionUtils.notNull(searchTag, "searchTag");
        Date now = new Date();
        return SearchQuery.builder()
                .moduleIds(moduleName)
                .beginMetaSearch()
                .lte("archivedAt", now)
                .and()
                .eq("deletedAt", null)
                .end();
    }

    /**
     * The updated items during the given millis from now.
     *
     * @param moduleName     The module name.
     * @param searchTag    The tag for this search.
     * @param updateMillis The update millis that will be substracted to the current date.
     * @return The options created.
     */
    @NonNull
    @Api(2.0)
    @Keep
    public static SearchQuery.Builder getLastUpdatedItems(@NonNull String moduleName, @NonNull String searchTag, long updateMillis) {
        AssertionUtils.notNull(moduleName, "moduleName");
        AssertionUtils.notNull(searchTag, "searchTag");
        Date now = new Date();
        Date from = new Date(now.getTime() - updateMillis);
        return SearchQuery.builder()
                .moduleIds(moduleName)
                .beginMetaSearch()
                .gte("updatedAt", from)
                .and()
                .lte("updatedAt", now)
                .and()
                .eq("deletedAt", null)
                .end();
    }


    /**
     * Provides the draft items for the given module.
     *
     * @param moduleName  The module name.
     * @param searchTag The tag for this search.
     * @return The search options.
     */
    @NonNull
    @Api(2.0)
    @Keep
    public static SearchQuery getDraftItems(@NonNull String moduleName, @NonNull String searchTag) {
        AssertionUtils.notNull(moduleName, "moduleName");
        AssertionUtils.notNull(searchTag, "searchTag");
        return SearchQuery.builder()
                .moduleIds(moduleName)
                .beginMetaSearch()
                .eq("publishedAt", null)
                .and()
                .eq("archivedAt", null)
                .and()
                .eq("removedAt", null)
                .and()
                .eq("deletedAt", null)
                .end()
                .build();
    }
}
