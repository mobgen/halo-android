package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SearchSortTest extends HaloRobolectricTest {

    private static Halo mHalo;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo("");
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatCanCreateASeatchSortInstance() {
        SearchSort instance = new SearchSort(SortField.DELETE, SortOrder.DESCENDING);
        String sort = instance.getSortQuery();
        assertThat(sort).contains(SortField.DELETE);
        assertThat(sort).contains(SortOrder.DESCENDING);
    }

    @Test
    public void thatAParcelOperationKeepsTheSameDataWithConstructor() {
        SearchSort instance = new SearchSort(SortField.DELETE, SortOrder.DESCENDING);
        SearchSort parcelInstance = TestUtils.testParcel(instance, SearchSort.CREATOR);
        assertThat(instance.getSortQuery()).isEqualTo(parcelInstance.getSortQuery());
    }



}
