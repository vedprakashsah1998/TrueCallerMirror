package com.infinity8.truecallermirror.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.infinity8.truecallermirror.convertor.Converter
import com.infinity8.truecallermirror.dao.CallDao
import com.infinity8.truecallermirror.dao.ContactDao
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.model.Contacts

@Database(entities = [CallLogEntry::class,Contacts::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callListDao(): CallDao
    abstract fun contactList():ContactDao
}