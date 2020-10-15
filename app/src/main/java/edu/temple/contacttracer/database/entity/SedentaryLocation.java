package edu.temple.contacttracer.database.entity;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

import edu.temple.contacttracer.support.interfaces.GlobalStateManager;

@Entity(tableName = "sedentary_location")
public class SedentaryLocation {
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

    public SedentaryLocation(int id, @NonNull UUID uuid, @NonNull Double latitude, @NonNull Double longitude, @NonNull Long sedentaryBegin, @NonNull Long sedentaryEnd) {
        this.id = id;
        this.uuid = uuid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sedentaryBegin = sedentaryBegin;
        this.sedentaryEnd = sedentaryEnd;
    }

    public SedentaryLocation(@NonNull UUID uuid, @NonNull Location location, @NonNull Long endTime) {
        this.uuid = uuid;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.sedentaryBegin = location.getTime();
        this.sedentaryEnd = endTime;
    }

    public static SedentaryLocation makeFrom(Location lastLocation, UniqueId mostRecentId, Long endTime) {
        return new SedentaryLocation(mostRecentId.uuid, lastLocation, endTime);
    }
}
