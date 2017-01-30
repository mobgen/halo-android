package com.mobgen.halo.android.framework.common.helpers.logger;

import android.support.annotation.IntDef;
import com.mobgen.halo.android.framework.common.annotations.Api;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PrintLog {
    /**
     * Determines the policy to use when printing log to file
     */
    @IntDef({NO_FILE_POLICY,SINGLE_FILE_POLICY, MULTIPLE_FILE_POLICY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Policy {
    }

    /**
     * Not store any file.
     */
    @Api(2.2)
    public static final int NO_FILE_POLICY = 0;
    /**
     * Store on a single file every execution so you can retrieve the file.
     */
    @Api(2.2)
    public static final int SINGLE_FILE_POLICY = 1;
    /**
     * Handles multiple files stored in device.
     */
    @Api(2.2)
    public static final int MULTIPLE_FILE_POLICY = 2;
}
