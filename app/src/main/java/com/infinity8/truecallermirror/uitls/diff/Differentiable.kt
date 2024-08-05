/*
 *
 *  Created by Ved Prakash on 16/05/23, 5:32 pm
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 15/05/23, 9:44 am
 *  Organization: Honeysys
 *
 */
package com.infinity8.mvvm_clean_base.utils.diff

@Deprecated(
    message = "Use the better named Diffable instead",
    replaceWith = ReplaceWith(
        expression = "Diffable",
        imports = arrayOf("com.infinit8.androidx.recyclerview.diff.Diffable")
    )
)
typealias Differentiable = Diffable

interface Diffable {

    val diffId: String

    fun areContentsTheSame(other: Diffable): Boolean = this == other

    fun getChangePayload(other: Diffable): Any? = null

}