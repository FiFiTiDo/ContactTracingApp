package edu.temple.contacttracer;

import android.app.Application;
import android.location.Location;

import edu.temple.contacttracer.database.AppDatabase;

public class App extends Application {
    public static AppDatabase db;
    public static Location lastLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();

        db = AppDatabase.getInstance(this);
    }
}
