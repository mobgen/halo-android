package com.mobgen.halo.android.framework.common.helpers.logger;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Logger class to help on the halo logging stuff.
 */
public class Halog {

    /**
     * Makes the log synchronized.
     */
    private static final Object SYNCHRONIZED_REFERENCE = new Object();

    /**
     * Determines if the log should print information.
     */
    private static boolean mPrintDebug;

    /**
     * The formatter.
     */
    private static LoggerFormatter mFormatter = new DefaultLogFormatter();

    /**
     * Constructor to avoid instances.
     */
    private Halog() {
        //Do nothing in this constructor
    }

    /**
     * Enables the log.
     *
     * @param enable Enables the log if needed.
     */
    @Api(1.0)
    public static void printDebug(boolean enable) {
        mPrintDebug = enable;
    }

    /**
     * Determines if the logger is printing.
     *
     * @return True if it is printing, false otherwise.
     */
    @Api(1.0)
    public static boolean isPrinting() {
        return mPrintDebug;
    }

    /**
     * Prints debug information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    public static void d(Class<?> clazz, String message) {
        synchronized (SYNCHRONIZED_REFERENCE) {
            if (mPrintDebug) {
                mFormatter.d(clazz, message);
            }
        }
    }

    /**
     * Prints verbose information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    public static void v(Class<?> clazz, String message) {
        synchronized (SYNCHRONIZED_REFERENCE) {
            if (mPrintDebug) {
                mFormatter.v(clazz, message);
            }
        }
    }

    /**
     * Prints info information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    public static void i(Class<?> clazz, String message) {
        synchronized (SYNCHRONIZED_REFERENCE) {
            mFormatter.i(clazz, message);
        }
    }

    /**
     * Prints warning information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    public static void w(Class<?> clazz, String message) {
        synchronized (SYNCHRONIZED_REFERENCE) {
            mFormatter.w(clazz, message);
        }
    }

    /**
     * Prints error information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    public static void e(Class<?> clazz, String message) {
        synchronized (SYNCHRONIZED_REFERENCE) {
            mFormatter.e(clazz, message);
        }
    }

    /**
     * Prints error information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     * @param e The exception.
     */
    @Api(1.4)
    public static void e(Class<?> clazz, String message, Exception e) {
        synchronized (SYNCHRONIZED_REFERENCE) {
            mFormatter.e(clazz, message, e);
        }
    }

    /**
     * Prints what the fuck information.
     *
     * @param clazz   The class used as context.
     * @param message The message to print.
     */
    @Api(1.0)
    public static void wtf(Class<?> clazz, String message) {
        synchronized (SYNCHRONIZED_REFERENCE) {
            if (mPrintDebug) {
                mFormatter.wtf(clazz, message);
            }
        }
    }

    /**
     * Overrides the default formatter to modify its behaviour.
     *
     * @param formatter The formatter.
     */
    @Api(1.0)
    public static void overrideFormatter(LoggerFormatter formatter) {
        mFormatter = formatter;
    }
}
