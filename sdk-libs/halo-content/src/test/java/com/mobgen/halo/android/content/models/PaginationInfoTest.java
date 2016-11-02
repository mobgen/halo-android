package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.testing.HaloJUnitTest;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PaginationInfoTest extends HaloJUnitTest {

    @Test
    public void thatGettersAndSettersProvideCorrectValues() {
        PaginationInfo info = new PaginationInfo(1, 10, 15);
        assertEquals(1, info.getPage());
        assertEquals(10, info.getLimit());
        assertEquals(2, info.getTotalPages());
        assertEquals(0, info.getOffset());
        assertEquals(15, info.getTotalItems());
    }

    @Test
    public void thatValuesAreProperlyCalculated() {
        PaginationInfo info = new PaginationInfo(15);
        assertEquals(1, info.getPage());
        assertEquals(15, info.getLimit());
        assertEquals(1, info.getTotalPages());
        assertEquals(0, info.getOffset());
        assertEquals(15, info.getTotalItems());
    }
}
