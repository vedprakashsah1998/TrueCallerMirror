package com.infinity8.truecallermirror.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.infinity8.truecallermirror.dao.CallDao
import com.infinity8.truecallermirror.datasource.CallLogPagingSource
import com.infinity8.truecallermirror.datasource.MissedCallLogSource
import com.infinity8.truecallermirror.datasource.OutgoingCallPagingSource
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

    fun getAllCallLogs(): Flow<PagingData<CallLogEntry>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                jumpThreshold = 20,
                prefetchDistance = 20,
                pageSize = 20,  // Number of items per page
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CallLogPagingSource(callDao) }
        ).flow
    }

    fun getMissedCallLogs(): Flow<PagingData<CallLogEntry>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                jumpThreshold = 20,
                prefetchDistance = 20,
                pageSize = 20,  // Number of items per page
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MissedCallLogSource(callDao) }
        ).flow
    }

    fun getOutgoingCallLogs(): Flow<PagingData<CallLogEntry>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                jumpThreshold = 20,
                prefetchDistance = 20,
                pageSize = 20,  // Number of items per page
                enablePlaceholders = false
            ),
            pagingSourceFactory = { OutgoingCallPagingSource(callDao) }
        ).flow
    }

    suspend fun deleteCallLog(callLog: CallLogEntry) = callDao.deleteCallLog(callLog)
}