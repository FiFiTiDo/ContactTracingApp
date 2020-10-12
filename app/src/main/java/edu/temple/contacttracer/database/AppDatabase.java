package edu.temple.contacttracer.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

import edu.temple.contacttracer.database.converter.DateConverter;
import edu.temple.contacttracer.database.converter.UuidConverter;
import edu.temple.contacttracer.database.dao.ContactEventDao;
import edu.temple.contacttracer.database.dao.SedentaryLocationDao;
import edu.temple.contacttracer.database.dao.UniqueIdDao;
import edu.temple.contacttracer.database.entity.ContactEvent;
import edu.temple.contacttracer.database.entity.SedentaryLocation;
import edu.temple.contacttracer.database.entity.UniqueId;
import edu.temple.contacttracer.support.DateUtils;

@Database(entities = {UniqueId.class, SedentaryLocation.class, ContactEvent.class}, version = 1)
@TypeConverters({DateConverter.class, UuidConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String LAST_RUN = "last_run";
    private static AppDatabase instance;

    public static AppDatabase getInstance(final Context ctx) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    ctx.getApplicationContext(),
                    AppDatabase.class,
                    "contact-tracing"
            ).build();
        }
        return instance;
    }

    public abstract UniqueIdDao uniqueIdDao();

    public abstract SedentaryLocationDao locationDao();

    public abstract ContactEventDao eventDao();

    public void generateId(boolean regen) {
        UniqueIdDao dao = uniqueIdDao();
        if (regen || dao.getToday() == null) {
            UniqueId uid = new UniqueId();
            dao.insert(uid);
            Log.d("UUID", "Generated new UUID: " + uid.uuid);
        }
    }

    public void generateId() {
        generateId(false);
    }

    public void checkDaily(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Date lastRun = new Date(prefs.getLong(LAST_RUN, 0));
        Date today = DateUtils.today();

        if (lastRun.before(today)) {
            Log.d("DailyTask", "Running daily task...");
            // Update last run
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(LAST_RUN, DateUtils.now());
            editor.apply();

            new Thread(() -> {
                generateId();

                UniqueIdDao uniqueIdDao = uniqueIdDao();
                List<UniqueId> oldIds = uniqueIdDao.getAllOld();
                Log.d("DailyTask", "Old Ids: " + oldIds.toString());
                uniqueIdDao.deleteAll(oldIds.toArray(new UniqueId[]{}));
                Log.d("DailyTask", "Deleted " + oldIds.size() + " old ids");

                SedentaryLocationDao locationDao = locationDao();
                List<SedentaryLocation> oldLocations = locationDao.getAllOld();
                locationDao.deleteAll(oldLocations.toArray(new SedentaryLocation[]{}));
                Log.d("DailyTask", "Deleted " + oldLocations.size() + " old locations");

                ContactEventDao eventDao = eventDao();
                List<ContactEvent> oldEvents = eventDao.getAllOld();
                eventDao.deleteAll(oldEvents.toArray(new ContactEvent[]{}));
                Log.d("DailyTask", "Deleted " + oldEvents.size() + " old contact events");
                Log.d("DailyTask", "Daily task complete.");
            }).start();
        }
    }
}
