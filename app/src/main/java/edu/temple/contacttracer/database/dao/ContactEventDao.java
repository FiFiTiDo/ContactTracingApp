package edu.temple.contacttracer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.temple.contacttracer.database.entity.ContactEvent;

@Dao
public interface ContactEventDao {
    @Query("SELECT * FROM contact_event WHERE created_at > DATE('now', '-14 day')")
    List<ContactEvent> getAllOld();

    @Insert
    void insert(ContactEvent event);

    @Delete
    void deleteAll(ContactEvent... events);
}
