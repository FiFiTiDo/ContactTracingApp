package edu.temple.contacttracer.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "contact_event")
public class ContactEvent {
    @NonNull
    public UUID targetId;

    @NonNull
    public Long latitude;

    @NonNull
    public Long longitude;

    @ColumnInfo(name = "created_at")
    @NonNull
    public Date createdAt;

    public ContactEvent(@NonNull UUID targetId, @NonNull Long latitude, @NonNull Long longitude) {
        this.targetId = targetId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = new Date();
    }
}
