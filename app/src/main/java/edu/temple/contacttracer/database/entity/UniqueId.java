package edu.temple.contacttracer.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "unique_id")
public class UniqueId {
    @PrimaryKey
    @NonNull
    public UUID uuid;

    @ColumnInfo(name = "generated_at")
    @NonNull
    public Date generatedAt;

    public UniqueId() {
        uuid = UUID.randomUUID();
        generatedAt = new Date();
    }

    @NonNull
    @Override
    public String toString() {
        return "UniqueId{" +
                "uuid=" + uuid +
                ", generatedAt=" + generatedAt +
                '}';
    }
}
