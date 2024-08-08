package com.infinity8.truecallermirror.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.repository.CallLogRepo
import com.infinity8.truecallermirror.uitls.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CallLogViewModel @Inject constructor(
    private val callLogRepo: CallLogRepo,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _callLogFlow: MutableStateFlow<Outcome<List<CallLogEntry>>> =
        MutableStateFlow(Outcome.Progress(true))
    val callLogs: StateFlow<Outcome<List<CallLogEntry>>> = _callLogFlow

    private val _callLogListFlow: MutableStateFlow<Outcome<PagingData<CallLogEntry>>> =
        MutableStateFlow(Outcome.Progress(true))
    val callListLogs: StateFlow<Outcome<PagingData<CallLogEntry>>> = _callLogListFlow

    private val _callLogMissedFlow: MutableStateFlow<Outcome<PagingData<CallLogEntry>>> =
        MutableStateFlow(Outcome.Progress(true))
    val callMissedLogs: StateFlow<Outcome<PagingData<CallLogEntry>>> = _callLogMissedFlow


    private val _callLogOutgoingFlow: MutableStateFlow<Outcome<PagingData<CallLogEntry>>> =
        MutableStateFlow(Outcome.Progress(true))
    val callOutgoingLogs: StateFlow<Outcome<PagingData<CallLogEntry>>> = _callLogOutgoingFlow


    init {
        insertCallLogs()
        getCallLogs()
        getPaginatedCallLogs()
        getPaginatedOutgoingCallLogs()
        getPaginatedMissedCallLogs()
    }

    private fun getCallLogs() {
        viewModelScope.launch {
            callLogRepo.getAllCallLog().collectLatest { log ->
                _callLogFlow.value = Outcome.Success(log)
            }
        }
    }

    private fun getPaginatedCallLogs() {
        viewModelScope.launch {
            try {
                callLogRepo.getAllCallLogs()
                    .cachedIn(viewModelScope)
                    .distinctUntilChanged()
                    .onStart { _callLogListFlow.value = Outcome.Progress(true) }
                    .onCompletion { _callLogListFlow.value = Outcome.Progress(false) }
                    .catch { e ->
                        _callLogListFlow.value = Outcome.Failure(e)
                    }
                    .collectLatest { pagingData ->
                        _callLogListFlow.value = Outcome.Success(pagingData)
                    }
            } catch (e: Exception) {
                _callLogListFlow.value = Outcome.Failure(e)
            }
        }
    }

    private fun getPaginatedMissedCallLogs() {
        viewModelScope.launch {
            try {
                callLogRepo.getMissedCallLogs()
                    .cachedIn(viewModelScope)
                    .distinctUntilChanged()
                    .onStart { _callLogMissedFlow.value = Outcome.Progress(true) }
                    .onCompletion { _callLogMissedFlow.value = Outcome.Progress(false) }
                    .catch { e ->
                        _callLogMissedFlow.value = Outcome.Failure(e)
                    }
                    .collectLatest { pagingData ->
                        _callLogMissedFlow.value = Outcome.Success(pagingData)
                    }
            } catch (e: Exception) {
                _callLogMissedFlow.value = Outcome.Failure(e)
            }
        }
    }

    private fun getPaginatedOutgoingCallLogs() {
        viewModelScope.launch {
            try {
                callLogRepo.getOutgoingCallLogs()
                    .cachedIn(viewModelScope)
                    .distinctUntilChanged()
                    .onStart { _callLogOutgoingFlow.value = Outcome.Progress(true) }
                    .onCompletion { _callLogOutgoingFlow.value = Outcome.Progress(false) }
                    .catch { e ->
                        _callLogOutgoingFlow.value = Outcome.Failure(e)
                    }
                    .collectLatest { pagingData ->
                        _callLogOutgoingFlow.value = Outcome.Success(pagingData)
                    }
            } catch (e: Exception) {
                _callLogOutgoingFlow.value = Outcome.Failure(e)
            }
        }
    }
    fun updateNote(id: Long, note: String) {
        viewModelScope.launch {
            callLogRepo.updateNote(id, note)
        }

    }

    private fun insertCallLogs() {
        viewModelScope.launch {
            var count = 0L
            val projection = arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            )

            // Query the CallLog content provider
            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
            )?.use { cursor ->
                val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
                val dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
                val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)

                while (cursor.moveToNext()) {
                    val number = cursor.getString(numberIndex)
                    val type = when (cursor.getInt(typeIndex)) {
                        CallLog.Calls.INCOMING_TYPE -> "Incoming"
                        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                        CallLog.Calls.MISSED_TYPE -> "Missed"
                        CallLog.Calls.REJECTED_TYPE -> "Rejected"
                        else -> "Unknown"
                    }
                    val date = Date(cursor.getLong(dateIndex))
                    val duration = cursor.getString(durationIndex)

                    // Add call log entry to list
                    val contactName = getContactName(number) ?: "Unknown"
                    count++
                    callLogRepo.insertCallLog(
                        CallLogEntry(
                            count,
                            number,
                            contactName,
                            type,
                            date,
                            duration
                        )
                    )
                }
            }
        }
    }

    // Function to get contact name from phone number
    private fun getContactName(phoneNumber: String): String? {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                return cursor.getString(nameIndex)
            }
        }
        return null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}