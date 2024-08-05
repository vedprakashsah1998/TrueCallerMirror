package com.infinity8.truecallermirror.repository

import com.infinity8.truecallermirror.dao.CallDao
import com.infinity8.truecallermirror.model.CallLogEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CallLogRepo @Inject constructor(private val callDao: CallDao) {

    suspend fun insertCallLog(callLog: CallLogEntry) = callDao.insertCallLog(callLog)
    suspend fun insertCallLog(callLog: List<CallLogEntry>) = callDao.insertCallLog(callLog)

     fun getAllCallLog(): Flow<List<CallLogEntry>> {
        return flow{emitAll(callDao.getAllCallLogs())}
    }

    suspend fun deleteCallLog(callLog: CallLogEntry) = callDao.deleteCallLog(callLog)
}