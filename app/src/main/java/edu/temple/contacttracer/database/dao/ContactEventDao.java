package edu.temple.contacttracer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.temple.contacttracer.database.entity.ContactEvent;

@Dao
public interface ContactEventDao {
    @Query("SELECT * FROM contact_event WHERE sedentary_end > DATE('now', '-14 day')")
    List<ContactEvent> getAllOld();

    @Query("SELECT * FROM contact_event WHERE sedentary_end <= DATE('now', '-14 day')")
    List<ContactEvent> getAll();

    @Insert
    void insert(ContactEvent event);

    @Delete
    void deleteAll(ContactEvent... events);
}
