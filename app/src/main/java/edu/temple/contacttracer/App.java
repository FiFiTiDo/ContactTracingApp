package edu.temple.contacttracer;

import android.app.Application;
import android.location.Location;

import edu.temple.contacttracer.database.AppDatabase;
import edu.temple.contacttracer.support.interfaces.GlobalStateManager;

public class App extends Application implements GlobalStateManager {
    public Location lastLocation = null;
    public AppDatabase db = null;

    @Override
    public AppDatabase getDb() {
        if (db == null) {
            db = AppDatabase.getInstance(this);
            db.checkDaily(this);
        }

        return db;
    }

    @Override
    public Location getLastLocation() {
        return lastLocation;
    }

    @Override
    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    @Override
    public boolean hasLastLocation() {
        return this.lastLocation != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        db = AppDatabase.getInstance(this);
        db.checkDaily(this);

        MyFirebaseMessagingService.subscribeToTopic();
    }
}
