package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.testing.HaloJUnitTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SearchQueryBuilderFactoryTest extends HaloJUnitTest {

    @Test
    public void thatConstructorIsPrivate() throws Exception {
        TestUtils.testPrivateConstructor(SearchQueryBuilderFactory.class);
    }

    @Test
    public void thatFactoryProvidesNonNullQueries() {
        String moduleId = "fakeId";
        String fakeTag = "fakeTag";
        assertThat(SearchQueryBuilderFactory.getArchivedItems(moduleId, fakeTag)).isNotNull();
        assertThat(SearchQueryBuilderFactory.getDraftItems(moduleId, fakeTag)).isNotNull();
        assertThat(SearchQueryBuilderFactory.getExpiredItems(moduleId, fakeTag)).isNotNull();
        assertThat(SearchQueryBuilderFactory.getLastUpdatedItems(moduleId, fakeTag, 1000)).isNotNull();
        assertThat(SearchQueryBuilderFactory.getPublishedItems(moduleId, fakeTag)).isNotNull();
    }
}
