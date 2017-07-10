package com.mobgen.locationpoc.utils;

import android.content.Context;

import com.mobgen.locationpoc.R;

import java.util.Date;

/**
 * Created by f.souto.gonzalez on 15/06/2017.
 */

public class DateUtils {

    public static String timeBetween(Context context, Date d1, Date d2) {
        if (daysBetween(d1, d2) > 0) {
            return daysBetween(d1, d2) + " " + context.getString(R.string.date_days);
        } else if (hoursBetween(d1, d2) > 0) {
            return hoursBetween(d1, d2) + " " + context.getString(R.string.date_hours);
        } else if (minutesBetween(d1, d2) > 0) {
            return minutesBetween(d1, d2) + " " + context.getString(R.string.date_minutes);
        } else {
            return context.getString(R.string.date_now);
        }
    }

    public static int daysBetween(Date d1, Date d2) {
        return Math.abs((int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)));
    }

    public static int hoursBetween(Date d1, Date d2) {
        return Math.abs((int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60)));
    }

    public static int minutesBetween(Date d1, Date d2) {
        return Math.abs((int) ((d2.getTime() - d1.getTime()) / (1000 * 60)));
    }
}
