package com.infinity8.truecallermirror.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.infinity8.truecallermirror.controller.ApiPaginatedListCallback
import com.infinity8.truecallermirror.controller.UICallback
import com.infinity8.truecallermirror.databinding.FragmentContactBinding
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.model.Contacts
import com.infinity8.truecallermirror.ui.BaseFragment
import com.infinity8.truecallermirror.ui.adapter.ContactsAdapter
import com.infinity8.truecallermirror.ui.adapter.MainLoadStateAdapter
import com.infinity8.truecallermirror.uitls.flowWithLifecycleUI
import com.infinity8.truecallermirror.uitls.handlePaginatedCallback
import com.infinity8.truecallermirror.uitls.setUpAdapter
import com.infinity8.truecallermirror.uitls.showErrorSnackBar
import com.infinity8.truecallermirror.viewmodel.ContactViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactFragment : BaseFragment<FragmentContactBinding>(FragmentContactBinding::inflate),
    UICallback, ApiPaginatedListCallback<Contacts> {

    private val contactViewModel: ContactViewModel by activityViewModels()
    private val contactsAdapter: ContactsAdapter by lazy {
        ContactsAdapter(
            requireContext(),
            this@ContactFragment
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvContact.setUpAdapter(contactsAdapter.withLoadStateFooter(footer = MainLoadStateAdapter()))

        fetchCallLogs()
    }

    override fun recyclerviewItemClick(callLogEntry: CallLogEntry) {

    }

    private fun fetchCallLogs() {
        loadProductIntoList()
        flowWithLifecycleUI(contactViewModel.contactFlow, Lifecycle.State.CREATED) { data ->
            data.handlePaginatedCallback(this@ContactFragment, this@ContactFragment)
        }
    }

    private fun loadProductIntoList() = contactsAdapter.addLoadStateListener { loadState ->
        when (val currentState = loadState.refresh) {
            is LoadState.Loading -> {
                binding.progress.visibility = View.VISIBLE
            }

            is LoadState.Error -> {
                val extractedException = currentState.error
                showErrorSnackBar(extractedException.message.toString())
                binding.progress.visibility = View.GONE
            }

            is LoadState.NotLoading -> {
                binding.progress.visibility = View.GONE
            }
        }

    }

    override fun successPaging(list: PagingData<Contacts>) {
        contactsAdapter.submitData(viewLifecycleOwner.lifecycle, list)
    }

    override fun <T> loading(result: T) {
        val loadRes = result as Boolean
        if (loadRes) {
            binding.progress.visibility = View.VISIBLE
        } else {
            binding.progress.visibility = View.GONE
        }
    }


}