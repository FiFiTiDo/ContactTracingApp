package edu.temple.contacttracer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.temple.contacttracer.database.entity.StationaryLocation;

@Dao
public interface StationaryLocationDao {
    @Query("SELECT * FROM stationary_location WHERE sedentary_end > DATE('now', '-14 day')")
    List<StationaryLocation> getAllOld();

    @Insert
    void insert(StationaryLocation location);

    @Delete
    void deleteAll(StationaryLocation... locations);
}
