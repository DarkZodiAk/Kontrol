package com.darkzodiak.kontrol.profile.data.local

import androidx.room.TypeConverter

class ProfileConverters {
    @TypeConverter
    fun toEditRestrictionType(value: String): EditRestrictionType {
        return EditRestrictionType.valueOf(value)
    }

    @TypeConverter
    fun fromEditRestrictionType(value: EditRestrictionType): String {
        return value.name
    }

    @TypeConverter
    fun toProfileStateType(value: String): ProfileStateType {
        return ProfileStateType.valueOf(value)
    }

    @TypeConverter
    fun fromProfileStateType(value: ProfileStateType): String {
        return value.name
    }
}