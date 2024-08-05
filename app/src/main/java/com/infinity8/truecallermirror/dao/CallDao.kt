package com.infinity8.truecallermirror.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.infinity8.truecallermirror.model.CallLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {
    @Upsert
    suspend fun insertCallLog(callLogEntry: CallLogEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLogEntry: List<CallLogEntry>)

    @Query("SELECT * FROM CALLLOGENTRY")
    fun getAllCallLogs(): Flow<List<CallLogEntry>>

    @Query("DELETE FROM CALLLOGENTRY WHERE id = :id")
    suspend fun deleteCallLogById(id: Int)

    @Delete
    suspend fun deleteCallLog(callLogEntry: CallLogEntry)
}