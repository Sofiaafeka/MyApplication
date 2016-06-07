package com.jobSearchApp.android.Common;

import java.util.Date;

/**
 * Created by סופי on 04/06/2016.
 */
public class ServerHelper {
    private static final long TICKS_AT_EPOCH = 621355968000000000L;
    private static final long TICKS_PER_MILLISECOND = 10000;

    public static Date dateFromTicks(long ticks) {
        Date date = new Date((ticks - TICKS_AT_EPOCH) / TICKS_PER_MILLISECOND);
        return date;
    }

    public static long ticksFromDate(Date date) {
        long ticks = date.getTime() * TICKS_PER_MILLISECOND + TICKS_AT_EPOCH;
        return ticks;
    }
}
