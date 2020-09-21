package net.fifitido.contact.database;

import androidx.room.TypeConverter;

import java.util.UUID;

public class UuidConverter {
    @TypeConverter
    public static UUID fromTimestamp(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    @TypeConverter
    public static String dateToTimestamp(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }
}
