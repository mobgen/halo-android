package com.mobgen.halo.android.sdk.core.management.models;

import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created by f.souto.gonzalez on 16/06/2017.
 */

public class HaloModuleQueryTest extends HaloRobolectricTest {

    @Test
    public void thatAParcelOperationKeepsTheSameDataWithBuilder() throws JSONException {
       HaloModuleQuery moduleQuery = HaloModuleQuery.builder()
               .serverCache(555)
               .withFields(false)
               .build();
        HaloModuleQuery parcelInstance = TestUtils.testParcel(moduleQuery, HaloModuleQuery.CREATOR);
        assertThat(moduleQuery.serverCahe()).isEqualTo(parcelInstance.serverCahe());
        assertThat(moduleQuery.withFields()).isEqualTo(parcelInstance.withFields());
    }
}
