package edu.temple.contacttracer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM stationary_location WHERE stored_at > DATE('now', '-14 day')")
    List<StationaryLocation> getAllOld();

    @Insert
    void insert(StationaryLocation location);

    @Delete
    void deleteAll(StationaryLocation... locations);
}
