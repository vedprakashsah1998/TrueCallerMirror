/*
 *
 *  Created by Ved Prakash on 22/05/23, 4:22 pm
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 22/05/23, 4:22 pm
 *  Organization: Honeysys
 *
 */

package com.infinity8.truecallermirror.controller

import androidx.paging.PagingData

interface ApiPaginatedListCallback<T : Any> {
    fun successPaging(list: PagingData<T>)
    fun <T> loading(result: T) {}
    fun <T> unknownData(result: T) {}

}