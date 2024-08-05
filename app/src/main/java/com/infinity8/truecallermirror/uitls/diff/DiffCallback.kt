/*
 *
 *  Created by Ved Prakash on 9/7/23, 2:15 PM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 9/7/23, 2:15 PM
 *  Organization: Honeysys
 *
 */

package com.infinity8.mvvm_clean_base.utils.diff

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class DiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        // Implement your logic here to compare items based on unique identifiers
        // For example, if your model class has an ID field, you can use it here
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals") // Fallback to Object.equals is necessary
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        // Implement your logic here to compare the contents of items
        // This is used to check if the item's content has changed, not just the ID
        return oldItem == newItem
    }
}