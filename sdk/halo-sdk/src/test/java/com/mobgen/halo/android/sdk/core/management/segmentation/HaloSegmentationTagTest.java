package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.os.Parcel;

import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloSegmentationTagTest extends HaloRobolectricTest {

    @Test
    public void thatCreateParcelable() {
        HaloSegmentationTag tag = new HaloSegmentationTag("dummy", "dummyData");
        HaloSegmentationTag tagParcelable = TestUtils.testParcel(tag, HaloSegmentationTag.CREATOR);
        assertThat(tag.getName()).isEqualTo(tagParcelable.getName());
        assertThat(tag.getValue()).isEqualTo(tagParcelable.getValue());
        assertThat(tag.describeContents()).isEqualTo(0);
    }

    @Test
    public void thatCreateTagWithParcelableCreator() {
        HaloSegmentationTag tag = new HaloSegmentationTag("dummy", "dummyData");
        HaloSegmentationTag destTag = givenATagFromParcelableCreator(tag);
        assertThat(tag.getName()).isEqualTo(destTag.getName());
        assertThat(tag.getValue()).isEqualTo(destTag.getValue());
        assertThat(HaloSegmentationTag.CREATOR.newArray(1).length).isEqualTo(1);
    }

    @Test
    public void thatCanCreateASegmentationTag() {
        HaloSegmentationTag tag = new HaloSegmentationTag("dummy", "dummyData");
        assertThat(tag.getValue()).isEqualTo("dummyData");
    }

    private static HaloSegmentationTag givenATagFromParcelableCreator(HaloSegmentationTag tag) {
        Parcel dest = Parcel.obtain();
        tag.writeToParcel(dest, 0);
        HaloSegmentationTag.CREATOR.newArray(1);
        dest.setDataPosition(0);
        return HaloSegmentationTag.CREATOR.createFromParcel(dest);
    }
}
