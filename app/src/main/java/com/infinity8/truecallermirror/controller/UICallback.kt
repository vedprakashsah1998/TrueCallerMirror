/*
 *
 *   Created by Ved Prakash on 4/16/24, 1:02 PM
 *   Copyright (c) 2024 . All rights reserved.
 *   Last modified 4/16/24, 1:02 PM
 *   Organization: NeoSoft
 *
 */

package com.infinity8.truecallermirror.controller

import com.infinity8.truecallermirror.model.CallLogEntry


interface UICallback {
    fun recyclerviewItemClick(callLogEntry: CallLogEntry) {}
}