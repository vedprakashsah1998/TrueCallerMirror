package com.infinity8.truecallermirror.convertor

import androidx.room.TypeConverter
import java.util.Date

class Converter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}