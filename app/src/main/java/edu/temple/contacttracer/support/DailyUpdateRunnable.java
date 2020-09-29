package edu.temple.contacttracer.support;

import android.util.Log;

import edu.temple.contacttracer.database.AppDatabase;
import edu.temple.contacttracer.database.dao.ContactEventDao;
import edu.temple.contacttracer.database.entity.ContactEvent;
import edu.temple.contacttracer.database.entity.StationaryLocation;
import edu.temple.contacttracer.database.dao.StationaryLocationDao;
import edu.temple.contacttracer.database.entity.UniqueId;
import edu.temple.contacttracer.database.dao.UniqueIdDao;

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

        StationaryLocationDao locationDao = db.locationDao();
        List<StationaryLocation> oldLocations = locationDao.getAllOld();
        locationDao.deleteAll(oldLocations.toArray(new StationaryLocation[] {}));
        Log.d("DailyTask", "Deleted " + oldLocations.size() + " old locations");

        ContactEventDao eventDao = db.eventDao();
        List<ContactEvent> oldEvents = eventDao.getAllOld();
        eventDao.deleteAll(oldEvents.toArray(new ContactEvent[] {}));
        Log.d("DailyTask", "Deleted " + oldEvents.size() + " old contact events");
    }
}
