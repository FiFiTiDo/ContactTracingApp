package edu.temple.contacttracer.support;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferenceUtils {
    public static SharedPreferences getDefaultSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private static int getInteger(Context ctx, String key, int defaultValue) {
        SharedPreferences pref = getDefaultSharedPreferences(ctx);
        String strVal = pref.getString(key, String.valueOf(defaultValue));
        return strVal == null ? defaultValue : Integer.parseInt(strVal);
    }

    public static int getTrackingDistance(Context ctx) {
        return getInteger(ctx, "tracking_distance", 2);
    }

    public static int getSedentaryLength(Context ctx) {
        return getInteger(ctx, "sedentary", 5);
    }
}
