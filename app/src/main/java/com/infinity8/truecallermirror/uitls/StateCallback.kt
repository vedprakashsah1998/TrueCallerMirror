/*
 *
 *   Created by Ved Prakash on 3/29/24, 3:08 PM
 *   Copyright (c) 2024 . All rights reserved.
 *   Last modified 3/29/24, 3:07 PM
 *   Organization: NeoSoft
 *
 */

package com.infinity8.truecallermirror.uitls

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import com.infinity8.truecallermirror.controller.ApiPaginatedListCallback
import com.infinity8.truecallermirror.controller.Callbacks

/**
 * @param callbacks = this variable used for setting different
 * callbacks from different states of ui
 * @param view = this is not used now but later it will
 * be helpful for showing toast or any other things
 * this method is very helpful for handling the different
 * states of ui and network as well.
 */
fun <T> Outcome<T>.handleStateData(
    callbacks: Callbacks,
) {
    when (this) {
        is Outcome.Progress -> {
            Log.d("PROGRESS STATUS: ", "Progress:${loading} ")
            callbacks.loadingNetwork(loading)
        }

        is Outcome.Success -> {
            callbacks.successResponse(data)
            if (data is List<*>) {
                callbacks.successListResponse(data)

            } else {
                // Handle other types of data
                callbacks.successResponse(data)
            }
        }

        is Outcome.Failure -> {
            Log.d("FAILURE RESPONSE: ", "Failure: $error")
            callbacks.failureResponse(error)
            callbacks.loadingNetwork(false)
        }

        is Outcome.Unknown -> {
            Log.d("UNKNOWN_BEHAVIOUR: ", "Unknown")
            callbacks.loadingNetwork(false)
            callbacks.unknownBehaviour(message)
        }
    }
}


fun <T : Any> Outcome<PagingData<T>>.handlePaginatedCallback(
    apiPaginatedListCallback: ApiPaginatedListCallback<T>,
    fragment: Fragment,
) {
    when (this) {
        is Outcome.Progress -> {
            Log.d("PROGRESS STATUS: ", "Progress:${loading} ")
            apiPaginatedListCallback.loading(this.loading)

        }

        is Outcome.Success -> {
            Log.d("Success STATUS: ", "Progress:${data} ")

            apiPaginatedListCallback.loading(false)
            apiPaginatedListCallback.successPaging(data)
        }

        is Outcome.Failure -> {
            Log.d("FAILURE RESPONSE: ", "Failure: ${error.message}")
            apiPaginatedListCallback.loading(false)
            fragment.showErrorSnackBar(this.error.message.toString())
        }

        is Outcome.Unknown -> {
            Log.d("UNKNOWN_BEHAVIOUR: ", "Unknown")

        }
    }

}