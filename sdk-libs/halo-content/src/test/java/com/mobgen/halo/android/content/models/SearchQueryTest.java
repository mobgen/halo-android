package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.sdk.core.internal.parser.LoganSquareParserFactory;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenAFullQuery;
import static com.mobgen.halo.android.content.models.SearchQuery.PARTIAL_MATCH;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class SearchQueryTest extends HaloRobolectricTest {

    @Test
    public void SearchQueryEmptyTest() {
        assertEmptyquery(SearchQuery.builder().build());
    }

    @Test
    public void fullNewBuilderTest() {
        assertFullquery(givenAFullQuery().newBuilder().build());
    }

    @Test
    public void emptyNewBuilderTest() {
        assertEmptyquery(SearchQuery.builder().build().newBuilder().build());
    }

    @Test
    public void thatSettersAndGettersAreWorkingInFullSearch() {
        SearchQuery query = givenAFullQuery();
        assertEquals(0, query.describeContents());
        assertFullquery(TestUtils.testParcel(query, SearchQuery.CREATOR));
    }

    @Test
    public void thatSearchObjectIsParcelable() {
        SearchQuery query = SearchQuery.builder().build();
        assertEquals(0, query.describeContents());
        assertEmptyquery(TestUtils.testParcel(query, SearchQuery.CREATOR));
    }

    @Test
    public void thatSegmentUserPropertyIsSet() {
        SearchQuery query = SearchQuery.builder().segmentWithDevice().build();
        assertTrue(query.isSegmentedWithDevice());
    }

    @Test
    public void thatEmptyQueryHasNoTags() {
        SearchQuery query = SearchQuery.builder().build();
        assertNull(query.getTags());
    }

    @Test
    public void thatPopulateAddsTheFieldsToTheQuery() {
        SearchQuery query = SearchQuery.builder().populate("field1", "field2").build();
        assertNotNull(query.getPopulateNames());
        assertEquals(2, query.getPopulateNames().size());
        assertEquals("field1", query.getPopulateNames().get(0));
        assertEquals("field2", query.getPopulateNames().get(1));
    }

    @Test
    public void thatASearchIsHashedProperly() throws HaloStorageGeneralException, HaloParsingException {
        SearchQuery query = SearchQuery.builder().build();
        Parser.Factory parser = LoganSquareParserFactory.create();
        assertNotNull(query.createHash(parser));
        SearchQuery query2 = SearchQuery.builder().moduleIds("myId").build();
        assertTrue(!query.createHash(parser).equals(query2.createHash(parser)));
    }

    @Test
    public void thatSkipPropertyJumpsPaginationInfo() {
        SearchQuery query = SearchQuery.builder().build();
        assertTrue(query.isPaginated());
        query = query.newBuilder().onePage(true).build();
        assertFalse(query.isPaginated());
        query = query.newBuilder().onePage(false).build();
        assertTrue(query.isPaginated());
    }

    @Test(expected = HaloConfigurationException.class)
    public void thatInvalidaPaginationOffsetParamThrowsException() {
        SearchQuery.builder().pagination(-1, 1);
    }

    @Test(expected = HaloConfigurationException.class)
    public void thatInvalidaPaginationPageParamThrowsException() {
        SearchQuery.builder().pagination(1, -1);
    }

    @Test
    public void thatAPaginatedSearchCanChangeTheParam() {
        SearchQuery query = SearchQuery.builder().build();
        assertNull(query.getPagination());
        query = query.newBuilder().onePage(true).build();
        assertNotNull(query.getPagination());
        assertTrue(!query.isPaginated());
        query = query.newBuilder().pagination(1, 10).onePage(false).build();
        assertNotNull(query.getPagination());
        assertTrue(query.isPaginated());
        assertEquals(1, query.getPagination().getPage());
        assertEquals(10, query.getPagination().getLimit());
    }

    @Test
    public void thatASimpleSearchWorks() {
        SearchQuery query = SearchQuery.builder()
                .beginSearch()
                .eq("field1", "value1")
                .and()
                .eq("field2", "value2")
                .and()
                .eq("field3", "value3")
                .end()
                .build();
        assertNotNull(query.getSearch());
        assertEquals(3, ((Condition) query.getSearch()).getItems().size());
        assertOperator(((Operator) ((Condition) query.getSearch()).getItems().get(0)), "field3", "value3", "=");
        assertOperator(((Operator) ((Condition) query.getSearch()).getItems().get(1)), "field2", "value2", "=");
        assertOperator(((Operator) ((Condition) query.getSearch()).getItems().get(2)), "field1", "value1", "=");
    }

    @Test
    public void thatOpenCloseParenthesisWithNoOpIsNullSearch() {
        SearchQuery query = SearchQuery.builder()
                .beginSearch()
                .beginGroup()
                .endGroup()
                .end()
                .build();
        assertThat(query.getSearch()).isNull();
    }

    @Test(expected = HaloConfigurationException.class)
    public void thatDoubleConditionInSearchThrowsException() {
        SearchQuery.builder()
                .beginSearch()
                .and()
                .and()
                .end();
    }

    @Test(expected = HaloConfigurationException.class)
    public void thatDoubleOperationInSearchThrowsException() {
        SearchQuery.builder()
                .beginSearch()
                .eq("field1", "value1")
                .eq("field2", "value2")
                .end();
    }

    @Test(expected = HaloConfigurationException.class)
    public void thatBadAmountOfParenthesisInSearchThrowsException() {
        SearchQuery.builder()
                .beginSearch()
                .beginGroup()
                .beginGroup()
                .endGroup()
                .end();
    }

    @Test(expected = HaloConfigurationException.class)
    public void thatNoOperationInSearchThrowsException() {
        SearchQuery.builder()
                .beginSearch()
                .endGroup()
                .end();
    }

    @Test(expected = HaloConfigurationException.class)
    public void thatABadOrderThrowsException() {
        SearchQuery.builder()
                .beginSearch()
                .and()
                .eq("field1", "value1")
                .end();
    }

    @Test(expected = HaloConfigurationException.class)
    public void thatABadExpressionThrowsException() {
        SearchQuery.builder()
                .beginSearch()
                .or()
                .lt("field1", new Date())
                .end();
    }

    @Test
    public void thatDifferentSearchOperatorsInteract() {
        SearchQuery query = SearchQuery.builder()
                .beginSearch()
                .eq("field1", "value1")
                .and()
                .gt("field1", 3)
                .and()
                .gte("field1", 3)
                .and()
                .lt("field1", 3)
                .or()
                .lte("field1", 3)
                .and()
                .neq("field1", "value1")
                .and()
                .nin("field1", Arrays.asList("value1", "value2"))
                .and()
                .in("field1", Arrays.asList("value1", "value2"))
                .end()
                .build();
        assertNotNull(query.getSearch());
        Condition and = (Condition) query.getSearch();
        assertEquals(4, and.getItems().size());
        assertOperator((Operator) and.getItems().get(0), "field1", ((Operator) and.getItems().get(0)).getValue(), "in");
        assertOperator((Operator) and.getItems().get(1), "field1", ((Operator) and.getItems().get(1)).getValue(), "!in");
        assertOperator((Operator) and.getItems().get(2), "field1", "value1", "!=");
        assertTrue(and.getItems().get(3) instanceof Condition);
        Condition or = (Condition) and.getItems().get(3);
        assertEquals(2, or.getItems().size());
        assertOperator((Operator) or.getItems().get(0), "field1", 3, "<=");
        Condition secondAnd = (Condition) or.getItems().get(1);
        assertOperator((Operator) secondAnd.getItems().get(0), "field1", 3, "<");
        assertOperator((Operator) secondAnd.getItems().get(1), "field1", 3, ">=");
        assertOperator((Operator) secondAnd.getItems().get(2), "field1", 3, ">");
        assertOperator((Operator) secondAnd.getItems().get(3), "field1", "value1", "=");
    }

    @Test
    public void thatQueryWithParenthesisCreatesGoodQuery() {
        SearchQuery query = SearchQuery.builder()
                .beginSearch()
                .eq("field1", "value1")
                .and()
                .beginGroup()
                .neq("field1", "value1")
                .or()
                .eq("field1", "value2")
                .endGroup()
                .end()
                .build();
        assertNotNull(query.getSearch());
        Condition or = (Condition) ((Condition) query.getSearch()).getItems().get(0);
        assertEquals(2, or.getItems().size());
        assertOperator((Operator) or.getItems().get(0), "field1", "value2", "=");
        assertOperator((Operator) or.getItems().get(1), "field1", "value1", "!=");
        assertOperator((Operator) ((Condition) query.getSearch()).getItems().get(1), "field1", "value1", "=");
    }

    @Test
    public void thatCanSearchToFilterTargetRelationships() {
        SearchQuery query = SearchQuery.builder()
                .addRelatedInstances(Relationship.create("fieldname","1"))
                .addRelatedInstances(Relationship.create("fieldname","2"))
                .build();
        assertThat(query.mRelationships.size()).isEqualTo(2);
        assertTrue(query.mRelationships.get(0).getFieldName().equals("fieldname"));
    }

    @Test
    public void thatCanSearchToFilterTargetRelationshipsWithAArrayGiven() {
        SearchQuery query = SearchQuery.builder()
                .relatedInstances(new Relationship[]{Relationship.create("fieldname","1"),Relationship.create("fieldname","2")})
                .build();
        assertThat(query.mRelationships.size()).isEqualTo(2);
        assertTrue(query.mRelationships.get(0).getFieldName().equals("fieldname"));
    }

    @Test
    public void thatCanSearchToFilterAllTargetRelationships() {
        SearchQuery query = SearchQuery.builder()
                .allRelatedInstances("fieldname")
                .build();
        assertThat(query.mRelationships.size()).isEqualTo(1);
        assertTrue(query.mRelationships.get(0).getFieldName().equals("fieldname"));
        assertTrue(query.mRelationships.get(0).getInstanceIds().get(0).equals("*"));
    }

    @Test
    public void thatCanSearchWithSegmentMode() {
        SearchQuery query = SearchQuery.builder()
                .segmentMode(PARTIAL_MATCH)
                .build();
        assertThat(query.getSegmentMode()).isEqualTo(PARTIAL_MATCH);
    }

    @Test
    public void thatCanSetADevice() {
        SearchQuery query = SearchQuery.builder()
                .segmentWithDevice()
                .build();
        Device device = new Device("alias","1","a@mobgen.com","token","5");
        List<HaloSegmentationTag> listTags = new ArrayList<HaloSegmentationTag>();
        listTags.add(new HaloSegmentationTag("name","value"));
        device.addTags(listTags);
        query.setDevice(device);
        assertThat(query.getTags().get(0).getName()).isEqualTo(listTags.get(0).getName());
        assertThat(query.getTags().get(0).getValue()).isEqualTo(listTags.get(0).getValue());
    }

    @Test
    public void thatCanSortSearch() {
        SearchSort[] searchSorts = new SearchSort[]{new SearchSort(SortField.UPDATED, SortOrder.ASCENDING),
                new SearchSort(SortField.UPDATED_BY, SortOrder.ASCENDING)};
        SearchQuery query = SearchQuery.builder()
                .sort(new SearchSort(SortField.ARCHIVED,SortOrder.DESCENDING))
                .sort(new SearchSort(SortField.CREATED,SortOrder.ASCENDING))
                .sort(new SearchSort(SortField.CREATED_BY,SortOrder.ASCENDING))
                .sort(new SearchSort(SortField.DELETED,SortOrder.DESCENDING))
                .sort(new SearchSort(SortField.DELETED_BY,SortOrder.ASCENDING))
                .sort(new SearchSort(SortField.NAME,SortOrder.DESCENDING))
                .sort(new SearchSort(SortField.PUBLISHED,SortOrder.ASCENDING))
                .sort(new SearchSort(SortField.REMOVED,SortOrder.DESCENDING))
                .sort(searchSorts)
                .build();

        assertThat(query.getSort()).contains(SortField.ARCHIVED);
        assertThat(query.getSort()).contains(SortField.CREATED);
        assertThat(query.getSort()).contains(SortField.CREATED_BY);
        assertThat(query.getSort()).contains(SortField.DELETED);
        assertThat(query.getSort()).contains(SortField.DELETED_BY);
        assertThat(query.getSort()).contains(SortField.NAME);
        assertThat(query.getSort()).contains(SortField.PUBLISHED);
        assertThat(query.getSort()).contains(SortField.REMOVED);
        assertThat(query.getSort()).contains(SortField.UPDATED);
        assertThat(query.getSort()).contains(SortField.UPDATED_BY);
    }


    private void assertFullquery(SearchQuery query) {
        // query
        assertNotNull(query);
        // ModuleIds
        assertNotNull(query.getModuleIds());
        assertEquals(2, query.getModuleIds().size());
        assertEquals("module1", query.getModuleIds().get(0));
        assertEquals("module2", query.getModuleIds().get(1));
        // InstanceIds
        assertNotNull(query.getInstanceIds());
        assertEquals(2, query.getInstanceIds().size());
        assertEquals("instance1", query.getInstanceIds().get(0));
        assertEquals("instance2", query.getInstanceIds().get(1));
        // Fields
        assertNotNull(query.getFieldNames());
        assertEquals(2, query.getFieldNames().size());
        assertEquals("values.field1", query.getFieldNames().get(0));
        assertEquals("values.field2", query.getFieldNames().get(1));
        //Tags
        assertNotNull(query.getTags());
        assertEquals(1, query.getTags().size());
        assertEquals(new HaloSegmentationTag("platform", "android"), query.getTags().get(0));
        // Populate
        assertNotNull(query.getPopulateNames());
        assertEquals(1, query.getPopulateNames().size());
        assertEquals("all", query.getPopulateNames().get(0));
        // Search
        assertTrue(query.getSearch() instanceof Condition);
        assertEquals(2, ((Condition) query.getSearch()).getItems().size());
        assertOperator(((Operator) ((Condition) query.getSearch()).getItems().get(0)), "field2", "value2", "=");
        assertOperator(((Operator) ((Condition) query.getSearch()).getItems().get(1)), "field1", "value1", "=");
        // Meta search
        assertTrue(query.getMetaSearch() instanceof Condition);
        assertEquals(2, ((Condition) query.getMetaSearch()).getItems().size());
        assertOperator(((Operator) ((Condition) query.getMetaSearch()).getItems().get(0)), "field2", "value2", "=");
        assertOperator(((Operator) ((Condition) query.getMetaSearch()).getItems().get(1)), "field1", "value1", "=");
        // Locale
        assertNotNull(query.getLocale());
        assertEquals(HaloLocale.ENGLISH_BELGIUM, query.getLocale());
        // Pagination
        assertNotNull(query.getPagination());
        assertEquals(1, query.getPagination().getPage());
        assertEquals(10, query.getPagination().getLimit());
        // TTL
        assertEquals(TimeUnit.HOURS.toMillis(1), query.getTTL());
    }

    private void assertEmptyquery(SearchQuery query) {
        assertNull(query.getModuleIds());
        assertNull(query.getInstanceIds());
        assertNull(query.getFieldNames());
        assertNull(query.getPopulateNames());
        assertNull(query.getPagination());
        assertNull(query.getLocale());
        assertTrue(query.isPaginated());
        assertNull(query.getTags());
        assertNull(query.getSearch());
        assertNull(query.getMetaSearch());
        assertEquals(TimeUnit.DAYS.toMillis(2), query.getTTL());
    }

    private void assertOperator(Operator operator, String property, Object value, String symbol) {
        assertEquals(property, operator.getProperty());
        assertEquals(value, operator.getValue());
        assertEquals(symbol, operator.getOperator());
    }
}
