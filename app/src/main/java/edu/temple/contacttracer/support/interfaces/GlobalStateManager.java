package edu.temple.contacttracer.support.interfaces;

import android.location.Location;

import edu.temple.contacttracer.database.AppDatabase;
import edu.temple.contacttracer.support.ApiManager;

public interface GlobalStateManager {
    AppDatabase getDb();

    ApiManager getApiManager();

    Location getLastLocation();

    void setLastLocation(Location location);

    boolean hasLastLocation();

    boolean isInForeground();

    void setInForeground(boolean inForeground);

    boolean isDebugMode();
}
