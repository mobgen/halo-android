package com.mobgen.halo.android.sdk.core.management.models;

import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class DeviceTest extends HaloRobolectricTest {

    @Test
    public void thatMakeAnonymousDevice() {
        Device user = new Device("alias", "id", null, null);
        assertThat(user.getAlias()).isEqualTo("alias");
        assertThat(user.getId()).isEqualTo("id");
    }

    @Test
    public void thatCanAddTags() {
        Device user = new Device("alias", "id", null, null);
        HaloSegmentationTag tag = new HaloSegmentationTag("example", null);
        user.addTag(tag);
        assertThat(user.getTags().size()).isEqualTo(1);
    }

    @Test
    public void thatAreTheSameTag() {
        Device user = new Device("alias", "id", null, null);
        HaloSegmentationTag tag = new HaloSegmentationTag("example", null);
        user.addTag(tag);
        assertThat(user.getTags().size()).isEqualTo(1);
        assertThat(user.getTags().get(0)).isEqualTo(new HaloSegmentationTag("example", null));
    }

    @Test
    public void thatCanSetToken() {
        String token = "myToken";
        Device user = new Device();
        user.setNotificationsToken(token);
        assertThat(user.getDevices()).isNotNull();
        assertThat(user.getNotificationsToken()).isEqualTo(token);
    }

    @Test
    public void thatCanUnsetToken() {
        String token = "myToken";
        Device user = new Device();
        user.setNotificationsToken(token);
        user.getDevices().clear();
        assertThat(user.getNotificationsToken()).isNull();
    }


    @Test
    public void thatSetemail() {
        Device user = new Device(null, null, "mobgen@mobgen.com", null);
        assertThat(user.getEmail()).isEqualTo("mobgen@mobgen.com");
    }

    @Test
    public void thatAddAListOfTags() {
        Device user = new Device(null, null, "mobgen@mobgen.com", null);
        List<HaloSegmentationTag> tags = new ArrayList<>();
        tags.add(new HaloSegmentationTag("example", null));
        tags.add(new HaloSegmentationTag("example2", null));
        user.addTags(tags);
        assertThat(user.getTags().size()).isEqualTo(2);
        assertThat(user.getTags().get(0)).isEqualTo(new HaloSegmentationTag("example", null));
        assertThat(user.getTags().get(1)).isEqualTo(new HaloSegmentationTag("example2", null));
    }

    @Test
    public void thatNewUserIsNotAnonymous() {
        Device user = new Device("alias", "id", "mobgen@mobgen.com", null);
        assertThat(user.isAnonymous()).isFalse();
    }

    @Test
    public void thatAUserCanBeAnonymous() {
        Device user = new Device("alias", "id", "mobgen@mobgen.com", null);
        user.makeAnonymous();
        assertThat(user.isAnonymous()).isTrue();
    }

    @Test
    public void thatCheckParcelUser() {
        Device user = new Device("alias", "id", "mobgen@mobgen.com", "token");
        Device newUser = TestUtils.testParcel(user, Device.CREATOR);
        assertThat(user.describeContents()).isEqualTo(0);
        assertThat(newUser.getId()).isEqualTo(user.getId());
        assertThat(newUser.getAlias()).isEqualTo(user.getAlias());
        assertThat(newUser.getNotificationsToken()).isEqualTo(user.getNotificationsToken());
        assertThat(newUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(newUser.getTags().size()).isEqualTo(user.getTags().size());
    }

    @Test
    public void thatRemoveATag() {
        Device user = new Device("alias", "id", null, null);
        user.addTag(new HaloSegmentationTag("example", null));
        user.removeTag(new HaloSegmentationTag("example", null));
        assertThat(user.getTags().size()).isEqualTo(0);
    }

    @Test
    public void thatEnsureDevicesSize() {
        Device user = new Device("alias", "id", "mobgen@mobgen.com", "token");
        user.setNotificationsToken("newToken");
        assertThat(user.getDevices()).isNotNull();
        assertThat(user.getNotificationsToken()).isEqualTo("newToken");
        assertThat(user.getDevices().size()).isEqualTo(1);
    }
}
