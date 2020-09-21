package net.fifitido.contact;

import android.util.Log;

import net.fifitido.contact.database.AppDatabase;
import net.fifitido.contact.database.UniqueId;
import net.fifitido.contact.database.UniqueIdDao;

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
