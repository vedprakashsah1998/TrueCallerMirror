package com.infinity8.truecallermirror.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.infinity8.truecallermirror.model.Contacts
@Dao
interface ContactDao {

    @Upsert
    suspend fun insertContacts(contacts: Contacts)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(callLogEntry: List<Contacts>)


    @Query("SELECT * FROM CALLLOGENTRY ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getContacts(limit: Int, offset: Int): List<Contacts>
}