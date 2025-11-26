package com.darkzodiak.kontrol.profile.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    fun toAppRestrictionType(value: String): AppRestrictionType {
        return AppRestrictionType.valueOf(value)
    }

    @TypeConverter
    fun fromAppRestrictionType(value: AppRestrictionType): String {
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

    @TypeConverter
    fun toUntilDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }

    @TypeConverter
    fun fromUntilDateTime(date: LocalDateTime?): String? {
        return date?.format(dateTimeFormatter)
    }

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}