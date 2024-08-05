/*
 *
 *   Created by Ved Prakash on 3/29/24, 3:08 PM
 *   Copyright (c) 2024 . All rights reserved.
 *   Last modified 3/29/24, 1:01 PM
 *   Organization: NeoSoft
 *
 */

package com.infinity8.truecallermirror.uitls


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.infinity8.truecallermirror.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat


fun Number.formatDecimal(): String {
    val df = DecimalFormat("#.00")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this)
}


/**
 * this extension function is used to start activity
 * with clear task back stack
 * usage:
 * startNewActivityScreen<MainActivity>()
 */
inline fun <reified T : Activity> Context.startNewActivityScreen() = startActivity(
    Intent(this, T::class.java)
)

/**
 * this extension function is used to start activity
 * usage:
 * startScreen<MainActivity>()
 */
inline fun <reified T : Activity> Fragment.startScreen() =
    startActivity(Intent(activity, T::class.java))


/**
 * this extension function used to collect data with flow
 * usage:     viewLifecycleOwner.launchWithLifecycle(
categoryViewModel.postStateDb,
Lifecycle.State.RESUMED
) { data ->
use data with different state
OutCome<List<DataModel>>
}
 */
inline fun <T> LifecycleOwner.launchWithLifecycle(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.CREATED,
    crossinline action: (T) -> Unit,
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(state) {
            flow.collectLatest { data ->
                action(data)
            }
        }
    }
}

inline fun <T> LifecycleOwner.flowWithLifecycleUI(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.CREATED,
    crossinline action: (T) -> Unit,
) {
    lifecycleScope.launch {
        flow.flowWithLifecycle(lifecycle, state).collect { data ->
            action(data)
        }
    }
}

inline fun <reified T, R> Outcome<T>.mapOutcome(transform: (T) -> R) =
    when (this) {
        is Outcome.Success -> Outcome.Success(transform(data))
        is Outcome.Failure -> Outcome.Failure(error)
        is Outcome.Progress -> Outcome.Progress(true)
        is Outcome.Unknown -> Outcome.Unknown(message)

    }

/**
 * this extension function is used to load imageView using glide library
 *
 *    imageView.loadImageWithGlide(imageUrl or bitmap)
 */
fun ImageView.loadImageWithGlide(
    url: Any?, onSuccess: () -> Unit,
    onFailure: () -> Unit,
    onLoading: () -> Unit,
) {
    onLoading()
    Glide.with(context)
        .load(url).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                onFailure()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                onSuccess()
                return false
            }

        })
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
        .into(this)
}

fun ImageView.loadImage(url: Any) {
    Glide.with(context).load(url)
        .placeholder(R.mipmap.ic_launcher)
        .transform(CircleCrop())
        .override(100, 100)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .into(this)
}

fun ImageView.loadImageNormal(url: Any) {
    Glide.with(context).load(url).placeholder(R.mipmap.ic_launcher)
        .diskCacheStrategy(DiskCacheStrategy.DATA).into(
            this
        )
}

/**
 * this method is used to show toast message
 */
fun Context.showToast(message: String) = Toast.makeText(
    this, message, Toast.LENGTH_LONG
).show()

fun Fragment.showToast(message: String) {
    activity?.showToast(message)
}


/**
 * this method is used show snackBar
 */
fun View.showSnackBar(e: Throwable) = Snackbar.make(
    this,
    e.message.toString(),
    Snackbar.LENGTH_LONG
).show()

fun View.showSnackBar(message: String) = Snackbar.make(
    this,
    message,
    Snackbar.LENGTH_LONG
).show()

fun View.snackBarError(message: String?) = Snackbar.make(this, message.toString(), 2000).apply {
    view.setBackgroundResource(R.color.color_red)
}.show()

fun View.snackBarSuccess(message: String?) = Snackbar.make(this, message.toString(), 4000).apply {
    view.setBackgroundResource(R.color.text_color_green_light)
}.show()

fun FragmentActivity.showSnackBar(message: String) {
    findViewById<View>(android.R.id.content)?.showSnackBar(message)
}

fun Fragment.showSnackBar(message: String) {
    view?.showSnackBar(message)
}

fun FragmentActivity.showErrorSnackBar(message: String) {
    findViewById<View>(android.R.id.content)?.snackBarError(message)
}

fun Fragment.showErrorSnackBar(message: String) {
    view?.snackBarError(message)
}

fun View.showErrorSnackBar(message: String) = snackBarError(message)

fun FragmentActivity.showSnackBarSuccess(message: String) {
    findViewById<View>(android.R.id.content)?.snackBarSuccess(message)
}


fun Fragment.showSuccessSnackBar(message: String) {
    view?.snackBarSuccess(message)
}