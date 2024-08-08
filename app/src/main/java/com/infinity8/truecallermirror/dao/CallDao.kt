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


    @Query("SELECT * FROM CALLLOGENTRY ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getCallLogs(limit: Int, offset: Int): List<CallLogEntry>

    @Query("SELECT * FROM CALLLOGENTRY WHERE type = 'Missed' ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getMissedCallLogs(limit: Int, offset: Int): List<CallLogEntry>

    @Query("SELECT * FROM CALLLOGENTRY WHERE type = 'Outgoing' ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getOutGoingCallLogs(limit: Int, offset: Int): List<CallLogEntry>

    @Query("DELETE FROM CALLLOGENTRY WHERE id = :id")
    suspend fun deleteCallLogById(id: Int)

    @Delete
    suspend fun deleteCallLog(callLogEntry: CallLogEntry)

    @Query("UPDATE CALLLOGENTRY SET note = :note WHERE id = :id")
    suspend fun updateNoteById(id: Long, note: String)
}