package edu.temple.contacttracer.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.Date;

import edu.temple.contacttracer.database.converter.DateConverter;
import edu.temple.contacttracer.database.converter.UuidConverter;
import edu.temple.contacttracer.database.dao.ContactEventDao;
import edu.temple.contacttracer.database.dao.StationaryLocationDao;
import edu.temple.contacttracer.database.dao.UniqueIdDao;
import edu.temple.contacttracer.database.entity.UniqueId;
import edu.temple.contacttracer.support.DailyUpdateRunnable;
import edu.temple.contacttracer.support.DateUtils;

@Database(entities = {UniqueId.class}, version = 1)
@TypeConverters({ DateConverter.class, UuidConverter.class })
public abstract class AppDatabase extends RoomDatabase {
    private static final String LAST_RUN = "last_run";

    public abstract UniqueIdDao uniqueIdDao();
    public abstract StationaryLocationDao locationDao();
    public abstract ContactEventDao eventDao();

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

    private static AppDatabase instance;
    public static AppDatabase getInstance(final Context ctx){
        if(instance == null){
            instance = Room.databaseBuilder(
                    ctx.getApplicationContext(),
                    AppDatabase.class,
                    "contact-tracing"
            ).build();
        }
        return instance;
    }
}
