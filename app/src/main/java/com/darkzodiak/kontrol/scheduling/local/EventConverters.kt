package com.darkzodiak.kontrol.scheduling.local

import androidx.room.TypeConverter

class EventConverters {
    @TypeConverter
    fun toEventType(value: String): EventType {
        return EventType.valueOf(value)
    }

    @TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }
}