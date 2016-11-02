package com.mobgen.halo.android.framework.common.helpers.logger;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Interface to custom the formatting options of the framework.
 */
public interface LoggerFormatter {

    /**
     * Prints debug information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    void d(Class<?> clazz, String message);

    /**
     * Prints verbose information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    void v(Class<?> clazz, String message);

    /**
     * Prints info information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    void i(Class<?> clazz, String message);

    /**
     * Prints warning information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    void w(Class<?> clazz, String message);

    /**
     * Prints error information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    void e(Class<?> clazz, String message);

    /**
     * Prints error information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     * @param e The exception to log.
     */
    @Api(1.4)
    void e(Class<?> clazz, String message, Exception e);

    /**
     * Prints what the fuck information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    void wtf(Class<?> clazz, String message);

}
