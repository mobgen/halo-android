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
        Device user = new Device("alias", "id", null, null, "5");
        assertThat(user.getAlias()).isEqualTo("alias");
        assertThat(user.getId()).isEqualTo("id");
    }

    @Test
    public void thatCanAddTags() {
        Device user = new Device("alias", "id", null, null, "5");
        HaloSegmentationTag tag = new HaloSegmentationTag("example", null);
        user.addTag(tag);
        assertThat(user.getTags().size()).isEqualTo(1);
    }

    @Test
    public void thatCanAddSameKeyAndDifferentValueTag() {
        Device user = new Device("alias", "id", null, null, "5");
        HaloSegmentationTag tag = new HaloSegmentationTag("wine", "rose");
        HaloSegmentationTag tag2 = new HaloSegmentationTag("wine", "white");
        user.addRepeatedKeyTags(new HaloSegmentationTag[]{tag, tag2});
        assertThat(user.getTags().size()).isEqualTo(2);
        assertThat(user.getTags().get(0).getName()).isEqualTo(user.getTags().get(1).getName());
        assertThat(user.getTags().get(0).getValue()).isNotEqualTo(user.getTags().get(1).getValue());
    }

    @Test
    public void thatATagWithSameKeyGetOverride() {
        Device user = new Device("alias", "id", null, null, "5");
        HaloSegmentationTag tag = new HaloSegmentationTag("wine", "rose");
        HaloSegmentationTag tag2 = new HaloSegmentationTag("wine", "red");
        HaloSegmentationTag tag3 = new HaloSegmentationTag("wine", "yellow");
        HaloSegmentationTag tag4 = new HaloSegmentationTag("wine", "white");
        user.addTag(tag);
        user.addTag(tag2);
        user.addTag(tag3);
        user.addTag(tag4);
        assertThat(user.getTags().size()).isEqualTo(1);
        assertThat(user.getTags().get(0).getValue()).isEqualTo("white");
    }

    @Test
    public void thatCanKeepOnlyOneTagAfterHaveRepeatedKeys() {
        Device user = new Device("alias", "id", null, null, "5");
        HaloSegmentationTag tag = new HaloSegmentationTag("wine", "rose");
        HaloSegmentationTag tag2 = new HaloSegmentationTag("wine", "red");
        HaloSegmentationTag tag3 = new HaloSegmentationTag("wine", "yellow");
        HaloSegmentationTag tag4 = new HaloSegmentationTag("wine", "white");
        user.addRepeatedKeyTags(new HaloSegmentationTag[]{tag, tag2, tag3});
        user.addTag(tag4);
        assertThat(user.getTags().size()).isEqualTo(1);
        assertThat(user.getTags().get(0).getValue()).isEqualTo("white");
    }

    @Test
    public void thatAreTheSameTag() {
        Device user = new Device("alias", "id", null, null, "5");
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
        Device user = new Device(null, null, "mobgen@mobgen.com", null, "5");
        assertThat(user.getEmail()).isEqualTo("mobgen@mobgen.com");
    }

    @Test
    public void thatAddAListOfTags() {
        Device user = new Device(null, null, "mobgen@mobgen.com", null, "5");
        List<HaloSegmentationTag> tags = new ArrayList<>();
        tags.add(new HaloSegmentationTag("example", null));
        tags.add(new HaloSegmentationTag("example2", null));
        user.addTags(tags, true);
        assertThat(user.getTags().size()).isEqualTo(2);
        assertThat(user.getTags().get(0)).isEqualTo(new HaloSegmentationTag("example", null));
        assertThat(user.getTags().get(1)).isEqualTo(new HaloSegmentationTag("example2", null));
    }

    @Test
    public void thatNewUserIsNotAnonymous() {
        Device user = new Device("alias", "id", "mobgen@mobgen.com", null, "5");
        assertThat(user.isAnonymous()).isFalse();
    }

    @Test
    public void thatAUserCanBeAnonymous() {
        Device user = new Device("alias", "id", "mobgen@mobgen.com", null, "5");
        user.makeAnonymous();
        assertThat(user.isAnonymous()).isTrue();
    }

    @Test
    public void thatCheckParcelUser() {
        Device user = new Device("alias", "id", "mobgen@mobgen.com", "token", "5");
        Device newUser = TestUtils.testParcel(user, Device.CREATOR);
        assertThat(user.describeContents()).isEqualTo(0);
        assertThat(newUser.getId()).isEqualTo(user.getId());
        assertThat(newUser.getAlias()).isEqualTo(user.getAlias());
        assertThat(newUser.getNotificationsToken()).isEqualTo(user.getNotificationsToken());
        assertThat(newUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(newUser.getTags().size()).isEqualTo(user.getTags().size());
        assertThat(newUser.getAppId()).isEqualTo(user.getAppId());
    }

    @Test
    public void thatRemoveATag() {
        Device user = new Device("alias", "id", null, null, "5");
        user.addTag(new HaloSegmentationTag("example", null));
        user.removeTag(new HaloSegmentationTag("example", null));
        assertThat(user.getTags().size()).isEqualTo(0);
    }

    @Test
    public void thatEnsureDevicesSize() {
        Device user = new Device("alias", "id", "mobgen@mobgen.com", "token", "5");
        user.setNotificationsToken("newToken");
        assertThat(user.getDevices()).isNotNull();
        assertThat(user.getNotificationsToken()).isEqualTo("newToken");
        assertThat(user.getDevices().size()).isEqualTo(1);
    }
}
