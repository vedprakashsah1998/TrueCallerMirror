package com.infinity8.truecallermirror.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
@Entity
data class CallLogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val number: String,
    val name: String,
    val type: String,
    val date: Date,
    val duration: String
) : Parcelable