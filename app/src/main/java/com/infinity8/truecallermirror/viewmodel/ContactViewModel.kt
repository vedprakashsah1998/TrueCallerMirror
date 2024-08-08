package com.infinity8.truecallermirror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.infinity8.truecallermirror.model.Contacts
import com.infinity8.truecallermirror.repository.ContactRepo
import com.infinity8.truecallermirror.uitls.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val contactRepo: ContactRepo) : ViewModel() {
    private val _contactFlow: MutableStateFlow<Outcome<PagingData<Contacts>>> =
        MutableStateFlow(Outcome.Progress(true))
    val contactFlow: StateFlow<Outcome<PagingData<Contacts>>> = _contactFlow

    init {
        insertContact()
        getContacts()
    }


    private fun insertContact() {
        viewModelScope.launch {
            contactRepo.insertContacts()
        }
    }

    private fun getContacts() {
        viewModelScope.launch {
            try {
                contactRepo.getContacts()
                    .cachedIn(viewModelScope)
                    .distinctUntilChanged()
                    .onStart { _contactFlow.value = Outcome.Progress(true) }
                    .onCompletion { _contactFlow.value = Outcome.Progress(false) }
                    .catch { e ->
                        _contactFlow.value = Outcome.Failure(e)
                    }
                    .collectLatest { pagingData ->
                        _contactFlow.value = Outcome.Success(pagingData)
                    }
            } catch (e: Exception) {
                _contactFlow.value = Outcome.Failure(e)
            }
        }
    }

}