package com.infinity8.truecallermirror.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.infinity8.truecallermirror.dao.CallDao
import com.infinity8.truecallermirror.model.CallLogEntry
import javax.inject.Inject

class MissedCallLogSource @Inject constructor(private val callDao: CallDao):
    PagingSource<Int, CallLogEntry>() {
    override val jumpingSupported: Boolean = true

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CallLogEntry> {
        return try {
            val currentPage = params.key ?: 0
            val offset = currentPage * params.loadSize
            val callLogs = callDao.getMissedCallLogs(params.loadSize, offset)

            LoadResult.Page(
                data = callLogs,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (callLogs.isEmpty()) null else currentPage + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CallLogEntry>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
}