package edu.temple.contacttracer;

import android.app.Application;

import edu.temple.contacttracer.database.AppDatabase;

public class App extends Application {
    public static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        db = AppDatabase.getInstance(this);
    }
}
