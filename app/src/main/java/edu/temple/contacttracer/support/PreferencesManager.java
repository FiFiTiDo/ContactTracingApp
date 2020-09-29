package edu.temple.contacttracer.support;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferencesManager {
    public static SharedPreferences getDefaultSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static int getTrackingDistance(Context ctx) {
        SharedPreferences pref = getDefaultSharedPreferences(ctx);
        int defVal = 2;
        String strVal = pref.getString("tracking_distance", String.valueOf(defVal));
        return strVal == null ? defVal : Integer.parseInt(strVal);
    }

    public static int getSedentaryLength(Context ctx) {
        SharedPreferences pref = getDefaultSharedPreferences(ctx);
        String defVal = "medium";
        String timeSel = pref.getString("sedentary", defVal);
        if (timeSel == null) timeSel = defVal;

        switch (timeSel) {
            case "short": return 5;
            case "long": return 45;
            default: return 15;
        }
    }
}
