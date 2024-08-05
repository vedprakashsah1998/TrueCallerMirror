/*
 *
 *  Created by Ved Prakash on 16/05/23, 5:32 pm
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 15/05/23, 9:43 am
 *  Organization: Honeysys
 *
 */

package com.infinity8.mvvm_clean_base.utils.diff

import android.annotation.SuppressLint
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Diffs items allowing for nice optimal list changes and nice transitions if items are [Differentiable],
 * otherwise [ListAdapter.submitList] will perform just as well as a regular [RecyclerView.Adapter.notifyDataSetChanged]
 *
 * It is highly recommended that type [T] is a Data class,
 * or at the very least overrides [Any.equals] and [Any.hashCode]
 *
 */
inline fun <reified T : Any> RecyclerView.Adapter<*>.createAsyncListDifferWithDiffCallback(): AsyncListDiffer<T> {
    val diffCallback = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            if (oldItem is Diffable && newItem is Diffable) oldItem.diffId == newItem.diffId
            else false

        @SuppressLint("DiffUtilEquals") // Fallback to Object.equals is necessary
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            if (oldItem is Diffable && newItem is Diffable) oldItem.areContentsTheSame(newItem)
            else oldItem == newItem

        override fun getChangePayload(oldItem: T, newItem: T): Any? =
            if (oldItem is Diffable && newItem is Diffable) oldItem.getChangePayload(newItem)
            else null
    }
    return AsyncListDiffer(this, diffCallback)
}