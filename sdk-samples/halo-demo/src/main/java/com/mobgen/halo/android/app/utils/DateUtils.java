package com.mobgen.halo.android.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The date utils helper class.
 */
public class DateUtils {

    private static SimpleDateFormat mDateFormatter;

    public static String formatDate(Date date) {
        if(date != null) {
            if (mDateFormatter == null) {
                mDateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
            }
            return mDateFormatter.format(date);
        }else{
            return "No update date";
        }
    }
}
