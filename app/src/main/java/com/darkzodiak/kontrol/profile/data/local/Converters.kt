package com.darkzodiak.kontrol.profile.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun toEditRestrictionType(value: String): EditRestrictionType {
        return EditRestrictionType.valueOf(value)
    }

    @TypeConverter
    fun fromEditRestrictionType(value: EditRestrictionType): String {
        return value.name
    }
}