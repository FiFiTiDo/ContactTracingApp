package edu.temple.contacttracer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.UUID;

import edu.temple.contacttracer.database.entity.UniqueId;

@Dao
public interface UniqueIdDao {
    @Query("SELECT * FROM unique_id WHERE generated_at == DATE('now') ORDER BY generated_at DESC LIMIT 1")
    UniqueId getToday();

    @Query("SELECT * FROM unique_id ORDER BY generated_at DESC LIMIT 1")
    UniqueId getMostRecent();

    @Query("SELECT * FROM unique_id WHERE generated_at > DATE('now', '-14 day')")
    List<UniqueId> getAllOld();

    @Query("SELECT EXISTS(SELECT * FROM unique_id WHERE uuid = :uuid)")
    Boolean hasById(UUID uuid);

    @Insert
    void insert(UniqueId uniqueId);

    @Delete
    void deleteAll(UniqueId... uniqueIds);
}
