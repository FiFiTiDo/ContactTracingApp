package edu.temple.contacttracer.support;

import android.util.Log;

import edu.temple.contacttracer.database.AppDatabase;
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
        UniqueIdDao dao = db.uniqueIdDao();
        new GenerateIdRunnable(db).run();

        List<UniqueId> oldIds = dao.getAllOld();
        Log.d("UUID", "Old Ids: " + oldIds.toString());
        dao.deleteAll(oldIds.toArray(new UniqueId[] {}));
        Log.d("UUID", "Deleted " + oldIds.size() + " old ids");
    }
}
