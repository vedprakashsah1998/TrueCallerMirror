package com.infinity8.truecallermirror.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.infinity8.truecallermirror.controller.ApiPaginatedListCallback
import com.infinity8.truecallermirror.controller.UICallback
import com.infinity8.truecallermirror.databinding.FragmentAllCallBinding
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.ui.BaseFragment
import com.infinity8.truecallermirror.ui.adapter.AllCallAdapter
import com.infinity8.truecallermirror.ui.adapter.MainLoadStateAdapter
import com.infinity8.truecallermirror.uitls.flowWithLifecycleUI
import com.infinity8.truecallermirror.uitls.handlePaginatedCallback
import com.infinity8.truecallermirror.uitls.setUpAdapter
import com.infinity8.truecallermirror.uitls.showErrorSnackBar
import com.infinity8.truecallermirror.viewmodel.CallLogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCallFragment : BaseFragment<FragmentAllCallBinding>(FragmentAllCallBinding::inflate),
    ApiPaginatedListCallback<CallLogEntry>, UICallback {

    private val callViewModel: CallLogViewModel by activityViewModels()
    private val callPageAdapter: AllCallAdapter by lazy {
        AllCallAdapter(
            requireContext(),
            this@AllCallFragment
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAllCall.setUpAdapter(callPageAdapter.withLoadStateFooter(footer = MainLoadStateAdapter()))

        fetchCallLogs()
        loadProductIntoList()
    }

    override fun recyclerviewItemClick(callLogEntry: CallLogEntry) {

    }

    private fun fetchCallLogs() =
        flowWithLifecycleUI(callViewModel.callListLogs, Lifecycle.State.CREATED) { data ->
            data.handlePaginatedCallback(this@AllCallFragment, this@AllCallFragment)
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
                Log.d("fwoiueroiu: ", "fwoiuero: ${callPageAdapter.snapshot().items.size}")
            }
        }
        /*    Log.d("fowiueroi: ","foiwueroi: ${loadState.refresh is LoadState.NotLoading}")
            if (callPageAdapter.itemCount == 0 &&  loadState.refresh is LoadState.NotLoading) {
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
            }*/

    }

    override fun successPaging(list: PagingData<CallLogEntry>) {
        if (view != null) {
            callPageAdapter.submitData(viewLifecycleOwner.lifecycle, list)
        }
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