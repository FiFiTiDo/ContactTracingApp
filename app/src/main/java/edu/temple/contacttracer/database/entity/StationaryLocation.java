package edu.temple.contacttracer.database.entity;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "stationary_location")
public class StationaryLocation {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public UUID uuid;

    @NonNull
    public Double latitude;

    @NonNull
    public Double longitude;

    @ColumnInfo(name = "sedentary_begin")
    @NonNull
    public Long sedentaryBegin;

    @ColumnInfo(name = "sedentary_end")
    @NonNull
    public Long sedentaryEnd;

    public StationaryLocation(@NonNull UUID uuid, @NonNull Location location, @NonNull Long endTime) {
        this.uuid = uuid;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.sedentaryBegin = location.getTime();
        this.sedentaryEnd = endTime;
    }
}
