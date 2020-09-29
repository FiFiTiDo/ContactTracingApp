package edu.temple.contacttracer.support;

import android.util.Log;

import edu.temple.contacttracer.database.AppDatabase;
import edu.temple.contacttracer.database.UniqueId;
import edu.temple.contacttracer.database.UniqueIdDao;

public class GenerateIdRunnable implements Runnable {
    private AppDatabase db;
    private boolean regen;

    public GenerateIdRunnable(AppDatabase db, boolean regen) {
        this.db = db;
        this.regen = regen;
    }

    public GenerateIdRunnable(AppDatabase db) {
        this(db, false);
    }

    @Override
    public void run() {
        UniqueIdDao dao = db.uniqueIdDao();
        if (regen || dao.getToday() == null) {
            UniqueId uid = new UniqueId();
            dao.insert(uid);
            Log.d("UUID", "Generated new UUID: " + uid.uuid);
        }
    }
}
