package com.mobgen.halo.android.sdk.core.management.segmentation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class DefaultCollectorFactoryTest {

    @Test
    public void thatSystemTagsAreAvailable() {
        List<TagCollector> tags = DefaultCollectorFactory.getDefaultTags(true);
        assertThat(tags.size()).isEqualTo(12);
    }
}
