package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.testing.HaloJUnitTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class PaginatedTest extends HaloJUnitTest {

    @Test
    public void thatGettersAndSettersProvideLogicValues() {
        List<Object> objects = new ArrayList<>();
        objects.add(new Object());
        objects.add(new Object());
        objects.add(new Object());

        PaginationInfo info = new PaginationInfo(2, 10, 30);
        Paginated<Object> paginatedList = new Paginated<>(objects, info);

        assertEquals(objects, paginatedList.data());
        assertEquals(10, paginatedList.getOffset());
        assertEquals(30, paginatedList.getCount());
        assertEquals(10, paginatedList.getLimit());
        assertEquals(2, paginatedList.getPage());
        assertEquals(3, paginatedList.getTotalPages());
        assertFalse(paginatedList.isFirstPage());
        assertFalse(paginatedList.isLastPage());
        assertFalse(paginatedList.isUniquePage());
    }

    @Test
    public void thatUniquePagesAreMarkedAsUnique() {
        List<Object> objects = new ArrayList<>();
        objects.add(new Object());
        objects.add(new Object());
        objects.add(new Object());
        Paginated<Object> paginatedList = new Paginated<>(objects);
        assertEquals(objects, paginatedList.data());
        assertEquals(0, paginatedList.getOffset());
        assertEquals(3, paginatedList.getCount());
        assertEquals(3, paginatedList.getLimit());
        assertEquals(1, paginatedList.getPage());
        assertEquals(1, paginatedList.getTotalPages());
        assertTrue(paginatedList.isFirstPage());
        assertTrue(paginatedList.isLastPage());
        assertTrue(paginatedList.isUniquePage());
    }
}
