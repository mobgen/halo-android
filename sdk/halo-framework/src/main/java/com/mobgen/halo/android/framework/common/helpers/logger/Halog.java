package com.mobgen.halo.android.framework.common.helpers.logger;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

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
     * Determines if the print mode is enabled
     */
    private static int mPrintLogToFilePolicy = PrintLog.NO_FILE_POLICY;

    /**
     * The formatter.
     */
    private static LoggerFormatter mFormatter = new DefaultLogFormatter();

    /**
     * The filename to store logs
     */
    private static String filename = "/HALO_LOG_%s.txt";

    /**
     * The log file.
     */
    private static File mLogFile;

    /**
     * mHaloFramework
     */
    private static HaloFramework mHaloFramework;

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
                if(mPrintLogToFilePolicy!= PrintLog.NO_FILE_POLICY){
                    enqueuePrintLogToFile(clazz,message);
                }
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
                if(mPrintLogToFilePolicy!= PrintLog.NO_FILE_POLICY){
                    enqueuePrintLogToFile(clazz,message);
                }
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
            if(mPrintLogToFilePolicy!= PrintLog.NO_FILE_POLICY && mPrintDebug){
                enqueuePrintLogToFile(clazz,message);
            }
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
            if(mPrintLogToFilePolicy!= PrintLog.NO_FILE_POLICY && mPrintDebug){
                enqueuePrintLogToFile(clazz,message);
            }
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
            if(mPrintLogToFilePolicy!= PrintLog.NO_FILE_POLICY && mPrintDebug){
                enqueuePrintLogToFile(clazz,message);
            }
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
            if(mPrintLogToFilePolicy!= PrintLog.NO_FILE_POLICY && mPrintDebug) {
                enqueuePrintLogToFile(clazz,message);
            }
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
                if(mPrintLogToFilePolicy!= PrintLog.NO_FILE_POLICY){
                    enqueuePrintLogToFile(clazz,message);
                }
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

    /**
     * Setup the file if its enable
     * @param haloFramework The halo framework instance
     */
    @NonNull
    @Api(2.3)
    public static void setupPrintLogToFile(@NonNull HaloFramework haloFramework) {
        AssertionUtils.notNull(haloFramework,"haloFramework");
        if (mPrintDebug) {
            mHaloFramework = haloFramework;
            mPrintLogToFilePolicy = haloFramework.printToFilePolicy();
            if (mPrintLogToFilePolicy == PrintLog.SINGLE_FILE_POLICY) {
                filename = String.format(filename, "SINGLE");
            } else if (mPrintLogToFilePolicy == PrintLog.MULTIPLE_FILE_POLICY) {
                Date now = new Date();
                Timestamp timestamp = new Timestamp(now.getTime());
                filename = String.format(filename, timestamp.toString());
            }

            //run on background thread
            mHaloFramework.toolbox().queue().enqueue(Threading.SINGLE_QUEUE_POLICY, new Runnable() {
                @Override
                public void run() {
                    if (mPrintLogToFilePolicy != PrintLog.NO_FILE_POLICY) {
                        mLogFile = new File(mHaloFramework.context().getExternalFilesDir(null).getAbsolutePath() + filename);
                        try {
                            //create directory
                            File directory = mLogFile.getParentFile();
                            if (!directory.exists()) {
                                directory.mkdirs();
                            }
                            if (mLogFile.exists()) {
                                mLogFile.delete();
                            }
                            mLogFile.createNewFile();
                        } catch (IOException e) {
                        }
                    }
                }
            });
        }
    }

    /**
     * Get the path of the printed log file.
     *
     * @return The path to log file or the last modified file if multiple file policy.
     */
    @Nullable
    @Api (2.2)
    public static File getLogFilePath() {
        if (mPrintDebug) {
            if (mPrintLogToFilePolicy == PrintLog.SINGLE_FILE_POLICY) {
                return mLogFile;
            }
            else if (mPrintLogToFilePolicy == PrintLog.MULTIPLE_FILE_POLICY) {//return last modified file
                File[] files = mLogFile.getParentFile().listFiles();
                if (files.length > 0) {
                    File lastModifiedFile = files[0];
                    for (int i = 1; i < files.length; i++) {
                        if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                            lastModifiedFile = files[i];
                        }
                    }
                    return lastModifiedFile;
                }
            }
        }
        return null;
    }

    /**
     * Enqueue print task on a single thread.
     *
     * @param clazz The classname
     * @param message The message to print to file.
     */
    private static void enqueuePrintLogToFile(final Class<?> clazz,final String message)  {

        mHaloFramework.toolbox().queue().enqueue(Threading.SINGLE_QUEUE_POLICY, new Runnable() {
            @Override
            public void run() {
                printMessageToFile(clazz,message);
            }
        });
    }

    /**
     * Tries to print Halo log messages to file.
     * @param clazz The classname
     * @param message The message to print to file.
     */
    private static void printMessageToFile(Class<?> clazz,String message){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                Date now = new Date();
                String tag = now.toString() + "/" + clazz.getSimpleName();
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mLogFile, true));
                bufferedWriter.write(tag + "\t\t|" + message + "\r");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
            }

        }
    }
}
