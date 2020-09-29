package edu.temple.contacttracer.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "stationary_location")
public class StationaryLocation {
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

    public StationaryLocation(@NonNull UUID uuid, @NonNull Double latitude, @NonNull Double longitude, @NonNull Long sedentaryBegin, @NonNull Long sedentaryEnd) {
        this.uuid = uuid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sedentaryBegin = sedentaryBegin;
        this.sedentaryEnd = sedentaryEnd;
    }
}
