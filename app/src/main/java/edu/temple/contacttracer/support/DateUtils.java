package edu.temple.contacttracer.support;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * Trims off the time of the date object
     *
     * @param date The normal date object
     * @return The start of the day
     */
    public static Date trimDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * Get the start of today
     *
     * @return The date object
     */
    public static Date today() {
        return trimDate(new Date());
    }

    /**
     * Get the current time (in millis)
     *
     * @return The current time
     */
    public static Long now() {
        return new Date().getTime();
    }
}
