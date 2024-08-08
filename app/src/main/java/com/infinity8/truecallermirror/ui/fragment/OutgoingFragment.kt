package com.infinity8.truecallermirror.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.infinity8.truecallermirror.controller.ApiPaginatedListCallback
import com.infinity8.truecallermirror.controller.UICallback
import com.infinity8.truecallermirror.databinding.FragmentOutgoingBinding
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.ui.BaseFragment
import com.infinity8.truecallermirror.ui.adapter.AllCallAdapter
import com.infinity8.truecallermirror.ui.adapter.MainLoadStateAdapter
import com.infinity8.truecallermirror.uitls.flowWithLifecycleUI
import com.infinity8.truecallermirror.uitls.handlePaginatedCallback
import com.infinity8.truecallermirror.uitls.setUpAdapter
import com.infinity8.truecallermirror.uitls.showErrorSnackBar
import com.infinity8.truecallermirror.viewmodel.CallLogViewModel


class OutgoingFragment : BaseFragment<FragmentOutgoingBinding>(FragmentOutgoingBinding::inflate),
    UICallback, ApiPaginatedListCallback<CallLogEntry> {
    private val callViewModel: CallLogViewModel by activityViewModels()
    private val callPageAdapter: AllCallAdapter by lazy {
        AllCallAdapter(
            requireContext(),
            this@OutgoingFragment
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvOutgoing.setUpAdapter(callPageAdapter.withLoadStateFooter(footer = MainLoadStateAdapter()))

        fetchCallLogs()
    }

    override fun recyclerviewItemClick(callLogEntry: CallLogEntry) {

    }

    private fun fetchCallLogs() {
        loadProductIntoList()
        flowWithLifecycleUI(callViewModel.callOutgoingLogs, Lifecycle.State.CREATED) { data ->
            data.handlePaginatedCallback(this@OutgoingFragment, this@OutgoingFragment)
        }
    }

    private fun loadProductIntoList() = callPageAdapter.addLoadStateListener { loadState ->
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

    override fun successPaging(list: PagingData<CallLogEntry>) {
        callPageAdapter.submitData(viewLifecycleOwner.lifecycle, list)
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