package net.fifitido.contact;

import android.util.Log;

import net.fifitido.contact.database.AppDatabase;
import net.fifitido.contact.database.UniqueId;
import net.fifitido.contact.database.UniqueIdDao;

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
