package com.infinity8.truecallermirror.uitls

/**
 * this sealed class handle the ui state for the application
 */
sealed class Outcome<out T> {
    data class Progress<out T>(val loading: Boolean, val partialData: T? = null) : Outcome<T>()
    data class Success<out T>(val data: T) : Outcome<T>()
    data class Failure(val error: Throwable) : Outcome<Nothing>()
    data class Unknown<out T>(val message: String? = null) : Outcome<T>()

    companion object {
        fun <T> loading(isLoading: Boolean = true, partialData: T? = null): Outcome<T> =
            Progress(isLoading, partialData)

        fun <T> success(data: T): Outcome<T> = Success(data)
        fun failure(error: Throwable): Outcome<Nothing> = Failure(error)
        fun <T> unknown(message: String): Outcome<T> = Unknown(message)
    }
}