package edu.temple.contacttracer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.temple.contacttracer.database.entity.SedentaryLocation;

@Dao
public interface SedentaryLocationDao {
    @Query("SELECT * FROM sedentary_location WHERE sedentary_end > DATE('now', '-14 day')")
    List<SedentaryLocation> getAllOld();

    @Query("SELECT * FROM sedentary_location WHERE sedentary_end <= DATE('now', '-14 day')")
    List<SedentaryLocation> getRecent();

    @Insert
    void insert(SedentaryLocation location);

    @Delete
    void deleteAll(SedentaryLocation... locations);

}
