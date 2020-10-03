package edu.temple.contacttracer.support.interfaces;

import android.location.Location;

import edu.temple.contacttracer.database.AppDatabase;

public interface GlobalStateManager {
    AppDatabase getDb();

    Location getLastLocation();

    void setLastLocation(Location location);

    boolean hasLastLocation();
}
