package net.fifitido.contact.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface UniqueIdDao {
    @Query("SELECT * FROM unique_id WHERE generated_at == DATE('now') LIMIT 1")
    UniqueId getToday();

    @Query("SELECT * FROM unique_id WHERE generated_at <= DATE('now', '-14 day')")
    List<UniqueId> getRecent();

    @Query("SELECT * FROM unique_id WHERE generated_at > DATE('now', '-14 day')")
    List<UniqueId> getAllOld();

    @Query("SELECT * FROM unique_id WHERE generated_at = :date")
    List<UniqueId> getByDate(Date date);

    @Insert
    void insert(UniqueId uniqueId);

    @Delete
    void deleteAll(UniqueId... uniqueIds);
}
