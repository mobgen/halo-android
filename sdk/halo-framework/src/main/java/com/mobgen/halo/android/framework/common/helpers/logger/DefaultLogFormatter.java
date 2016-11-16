package com.mobgen.halo.android.framework.common.helpers.logger;

import android.util.Log;

/**
 * Default formatter used by halo to format all the log actions.
 */
public class DefaultLogFormatter implements LoggerFormatter {

    /**
     * The default name of the framework.
     */
    private static final String SDK_NAME = "HALO";

    /**
     * The maximum size of the class name.
     */
    private static final int DEFAULT_CLASS_SIZE = 30;

    /**
     * Prints debug information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Override
    public void d(Class<?> clazz, String message) {
        Log.d(SDK_NAME, wrap(clazz.getSimpleName()) + " | " + message);
    }

    /**
     * Prints verbose information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Override
    public void v(Class<?> clazz, String message) {
        Log.v(SDK_NAME, wrap(clazz.getSimpleName()) + " | " + message);
    }

    /**
     * Prints info information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Override
    public void i(Class<?> clazz, String message) {
        Log.i(SDK_NAME, wrap(clazz.getSimpleName()) + " | " + message);
    }

    /**
     * Prints warning information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Override
    public void w(Class<?> clazz, String message) {
        Log.w(SDK_NAME, wrap(clazz.getSimpleName()) + " | " + message);
    }

    /**
     * Prints error information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Override
    public void e(Class<?> clazz, String message) {
        Log.e(SDK_NAME, wrap(clazz.getSimpleName()) + " | " + message);
    }

    @Override
    public void e(Class<?> clazz, String message, Exception e) {
        Log.e(SDK_NAME, wrap(clazz.getSimpleName()) + " | " + message, e);
    }

    /**
     * Prints what the fuck information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Override
    public void wtf(Class<?> clazz, String message) {
        Log.wtf(SDK_NAME, wrap(clazz.getSimpleName()) + " | " + message);
    }

    /**
     * Wraps the names of the class names in a DEFAULT_CLASS_SIZE to keep the logs on the same size.
     *
     * @param simpleName The simple class name.
     * @return The wrapped value.
     */
    private String wrap(String simpleName) {
        return String.format("%1$-" + DEFAULT_CLASS_SIZE + "s", simpleName);
    }
}
