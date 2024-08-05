/*
 *
 *   Created by Ved Prakash on 3/29/24, 3:08 PM
 *   Copyright (c) 2024 . All rights reserved.
 *   Last modified 3/29/24, 3:05 PM
 *   Organization: NeoSoft
 *
 */

package com.infinity8.truecallermirror.controller

interface Callbacks {

    fun <T> successResponse(result: T) {}
    fun  <T> successListResponse(result: List<T>) {}
    fun <T> loadingNetwork(result: T) {}
    fun <T> failureResponse(result: T) {}
    fun <T> unknownBehaviour(result: T) {}

}