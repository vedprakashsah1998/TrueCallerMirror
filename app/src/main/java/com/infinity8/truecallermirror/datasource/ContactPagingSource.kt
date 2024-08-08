package com.infinity8.truecallermirror.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.infinity8.truecallermirror.dao.ContactDao
import com.infinity8.truecallermirror.model.Contacts
import javax.inject.Inject

class ContactPagingSource @Inject constructor(private val contactDao: ContactDao) :
    PagingSource<Int, Contacts>() {
    override val jumpingSupported: Boolean = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contacts> {
        return try {
            val currentPage = params.key ?: 0
            val offset = currentPage * params.loadSize
            val callLogs = contactDao.getContacts(params.loadSize, offset)

            LoadResult.Page(
                data = callLogs,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (callLogs.isEmpty()) null else currentPage + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Contacts>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }


}