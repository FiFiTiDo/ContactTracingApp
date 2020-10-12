package edu.temple.contacttracer;

import android.app.Application;
import android.location.Location;

import edu.temple.contacttracer.database.AppDatabase;
import edu.temple.contacttracer.support.ApiManager;
import edu.temple.contacttracer.support.interfaces.GlobalStateManager;

public class ContactTracerApplication extends Application implements GlobalStateManager {
    private Location lastLocation = null;
    private AppDatabase db = null;
    private boolean inForeground = false;
    private ApiManager api = null;

    @Override
    public AppDatabase getDb() {
        if (db == null)
            db = AppDatabase.getInstance(this);

        db.checkDaily(this);
        return db;
    }

    @Override
    public ApiManager getApiManager() {
        if (api == null)
            api = new ApiManager(this);

        return api;
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
    public boolean isInForeground() {
        return inForeground;
    }

    @Override
    public void setInForeground(boolean inForeground) {
        this.inForeground = inForeground;
    }

    @Override
    public boolean isDebugMode() {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MyFirebaseMessagingService.subscribeToTopics();
    }
}
