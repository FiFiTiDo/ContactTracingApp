package edu.temple.contacttracer.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {UniqueId.class}, version = 1)
@TypeConverters({ DateConverter.class, UuidConverter.class })
public abstract class AppDatabase extends RoomDatabase {
    public abstract UniqueIdDao uniqueIdDao();
}
