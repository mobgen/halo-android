package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class RelationshipTest extends HaloRobolectricTest {

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
    public void thatObjectAreTheSameWithBuilderOrConstructor() {
        Relationship relationship = Relationship.builder()
                .addInstanceIds("one")
                .addInstanceIds("two")
                .fieldName("fieldname")
                .build();
        Relationship relationship1 =  Relationship.create("fieldname", new String[]{"one","two"});
        assertThat(relationship1.getFieldName()).isEqualTo(relationship.getFieldName());
        assertThat(relationship1.getInstanceIds().size()).isEqualTo(relationship.getInstanceIds().size());
    }

    @Test
    public void thatAParcelOperationKeepsTheSameDataWithConstructor() {
        Relationship instance = Relationship.builder()
                .instanceIds(new String[]{"one","two"})
                .fieldName("fieldname")
                .build();
        Relationship parcelInstance = TestUtils.testParcel(instance, Relationship.CREATOR);
        assertThat(instance.getFieldName()).isEqualTo(parcelInstance.getFieldName());
        assertThat(instance.getInstanceIds().get(1)).isNotEqualTo(parcelInstance.getInstanceIds().get(0));
    }
}
