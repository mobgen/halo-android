package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the default tag collector list.
 */
public final class DefaultCollectorFactory {

    /**
     * Provides the default tag collector list used by default in the framework.
     *
     * @param isTestingDevice True if it should add the testing device tag. False otherwise.
     * @return The default tag collector list. Cannot be null.
     */
    @NonNull
    public static List<TagCollector> getDefaultTags(boolean isTestingDevice) {
        List<TagCollector> tagList = new ArrayList<>();
        tagList.add(new PlatformNameCollector());
        tagList.add(new PlatformVersionCollector());
        tagList.add(new ApplicationNameCollector());
        tagList.add(new ApplicationVersionCollector());
        tagList.add(new DeviceManufacturerCollector());
        tagList.add(new DeviceModelCollector());
        tagList.add(new DeviceTypeCollector());
        tagList.add(new Bluetooth4SupportCollector());
        tagList.add(new NFCSupportCollector());
        tagList.add(new ScreenSizeCollector());
        tagList.add(new SdkVersionCollector());
        tagList.add(new TestDeviceCollector(isTestingDevice));
        return tagList;
    }
}
