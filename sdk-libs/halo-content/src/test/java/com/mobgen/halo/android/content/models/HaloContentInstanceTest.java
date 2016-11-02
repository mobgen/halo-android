package com.mobgen.halo.android.content.models;

import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloContentInstanceTest extends HaloRobolectricTest {

    @Test
    public void thatAParcelOperationKeepsTheSameData() throws JSONException {
        Date now = new Date();
        JSONObject json = new JSONObject("{foo: 'bar'}");
        HaloContentInstance instance = new HaloContentInstance("fakeId", "fakeModule", "fakeName", json, "fakeAuthor", now, now, now, now);
        HaloContentInstance parcelInstance = TestUtils.testParcel(instance, HaloContentInstance.CREATOR);
        assertThat(instance.getItemId()).isEqualTo(parcelInstance.getItemId());
        assertThat(instance.getModuleId()).isEqualTo(parcelInstance.getModuleId());
        assertThat(instance.getName()).isEqualTo(parcelInstance.getName());
        assertThat(instance.getValues().toString()).isEqualTo(parcelInstance.getValues().toString());
        assertThat(instance.getAuthor()).isEqualTo(parcelInstance.getAuthor());
        assertThat(instance.getCreatedDate()).isEqualTo(parcelInstance.getCreatedDate());
        assertThat(instance.getLastUpdate()).isEqualTo(parcelInstance.getLastUpdate());
        assertThat(instance.getPublishedDate()).isEqualTo(parcelInstance.getPublishedDate());
        assertThat(instance.getRemoveDate()).isEqualTo(parcelInstance.getRemoveDate());
        assertThat(instance.describeContents()).isEqualTo(0);
    }
}
