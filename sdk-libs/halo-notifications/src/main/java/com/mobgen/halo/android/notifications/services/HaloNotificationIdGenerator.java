package com.mobgen.halo.android.notifications.services;


import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Halo id generator to provide unique notification ids.
 */
public class HaloNotificationIdGenerator implements NotificationIdGenerator {

    @Override
    public int getNextNotificationId(@NonNull Bundle data, int currentId) {
        return currentId;
    }
}
