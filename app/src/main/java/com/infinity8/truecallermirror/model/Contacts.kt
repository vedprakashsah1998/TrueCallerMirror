package com.infinity8.truecallermirror.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity
data class Contacts(
    @PrimaryKey
    val id: String,
    val name: String,
    val number: String
) : Parcelable
