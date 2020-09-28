package edu.temple.contacttracer;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date trimDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static Date today() {
        return trimDate(new Date());
    }

    public static Long now() {
        return new Date().getTime();
    }
}
