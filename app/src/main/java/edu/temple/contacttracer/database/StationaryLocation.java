package edu.temple.contacttracer.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "stationary_location")
public class StationaryLocation {
    @NonNull
    public Long latitude;

    @NonNull
    public Long longitude;

    @ColumnInfo(name = "stored_at")
    @NonNull
    public Date storedAt;

    public StationaryLocation(long latitude, long longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.storedAt = new Date();
    }
}
