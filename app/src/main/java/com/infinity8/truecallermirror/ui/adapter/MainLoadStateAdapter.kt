/*
 *
 *   Created by Ved Prakash on 4/1/24, 11:45 AM
 *   Copyright (c) 2024 . All rights reserved.
 *   Last modified 4/1/24, 11:32 AM
 *   Organization: NeoSoft
 *
 */

package com.infinity8.truecallermirror.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infinity8.truecallermirror.databinding.LoadStateViewBinding

internal class MainLoadStateAdapter : LoadStateAdapter<MainLoadStateAdapter.LoadStateViewHolder>() {
    inner class LoadStateViewHolder(val binding: LoadStateViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        LoadStateViewHolder(
            LoadStateViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.binding.progress.isVisible = loadState is LoadState.Loading
    }
}