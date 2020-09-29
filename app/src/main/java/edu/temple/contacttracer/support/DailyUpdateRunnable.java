package edu.temple.contacttracer.support;

import android.util.Log;

import edu.temple.contacttracer.database.AppDatabase;
import edu.temple.contacttracer.database.StationaryLocation;
import edu.temple.contacttracer.database.LocationDao;
import edu.temple.contacttracer.database.UniqueId;
import edu.temple.contacttracer.database.UniqueIdDao;

import java.util.List;

public class DailyUpdateRunnable implements Runnable {
    private AppDatabase db;

    public DailyUpdateRunnable(AppDatabase db) {
        this.db = db;
    }

    @Override
    public void run() {
        UniqueIdDao uniqueIdDao = db.uniqueIdDao();
        new GenerateIdRunnable(db).run();

        List<UniqueId> oldIds = uniqueIdDao.getAllOld();
        Log.d("DailyTask", "Old Ids: " + oldIds.toString());
        uniqueIdDao.deleteAll(oldIds.toArray(new UniqueId[] {}));
        Log.d("DailyTask", "Deleted " + oldIds.size() + " old ids");

        LocationDao locationDao = db.locationDao();
        List<StationaryLocation> oldLocations = locationDao.getAllOld();
        locationDao.deleteAll(oldLocations.toArray(new StationaryLocation[] {}));
        Log.d("DailyTask", "Deleted " + oldLocations.size() + " old locations");
    }
}
