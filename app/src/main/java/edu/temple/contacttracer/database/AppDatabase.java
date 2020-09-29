package edu.temple.contacttracer.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.Date;

import edu.temple.contacttracer.DailyUpdateRunnable;
import edu.temple.contacttracer.DateUtils;

@Database(entities = {UniqueId.class}, version = 1)
@TypeConverters({ DateConverter.class, UuidConverter.class })
public abstract class AppDatabase extends RoomDatabase {
    private static final String LAST_RUN = "last_run";

    public abstract UniqueIdDao uniqueIdDao();

    public void checkDaily(SharedPreferences prefs) {
        Date lastRun = new Date(prefs.getLong(LAST_RUN, 0));
        Date today = DateUtils.today();

        if (lastRun.before(today)) {
            new Thread(() -> {
                new DailyUpdateRunnable(this).run();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(LAST_RUN, DateUtils.now());
                editor.apply();
            }).start();
        }
    }
}
