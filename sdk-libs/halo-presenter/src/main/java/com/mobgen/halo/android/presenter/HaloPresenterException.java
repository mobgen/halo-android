package com.mobgen.halo.android.presenter;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Exception thrown by an error in the presenter.
 */
@Keep
public class HaloPresenterException extends Exception {

    /**
     * Exception raised to the view layer when something happens in the presenter.
     *
     * @param message   The message of the exception.
     * @param exception The error raised to the view layer.
     */
    @Keep
    @Api(2.0)
    public HaloPresenterException(String message, Throwable exception) {
        super(message, exception);
    }

    /**
     * Exception raised to the view layer when something happens in the presenter.
     *
     * @param message The message to be raised.
     */
    @Keep
    @Api(2.0)
    public HaloPresenterException(String message) {
        super(message);
    }

    /**
     * Exception raised to the view layer when something happens in the presenter.
     *
     * @param exception The exception raised.
     */
    @Keep
    @Api(2.0)
    public HaloPresenterException(Throwable exception) {
        super(exception);
    }
}
