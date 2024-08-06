package com.infinity8.truecallermirror.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infinity8.truecallermirror.R
import com.infinity8.truecallermirror.controller.UICallback
import com.infinity8.truecallermirror.databinding.AllCallLogItemBinding
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.uitls.diff.DiffCallback
import com.infinity8.truecallermirror.uitls.formatDuration
import com.infinity8.truecallermirror.uitls.setImageViewDrawable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class AllCallAdapter(val context: Context, val uiCallback: UICallback) :
    PagingDataAdapter<CallLogEntry, AllCallAdapter.AllCallViewHolder>(
        DiffCallback()
    ) {

    inner class AllCallViewHolder(private val binding: AllCallLogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(callLogEntry: CallLogEntry?) {
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val date: Date? = inputFormat.parse(callLogEntry?.date.toString())
            val outputFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val timeString = date?.let { outputFormat.format(it) }

            binding.apply {
                if (callLogEntry?.name != "Unknown") {
                    number.text = callLogEntry?.name
                } else {
                    number.text = callLogEntry.number
                }
                time.text = timeString
                when (callLogEntry?.type) {
                    "Incoming" -> {
                        icon.setImageViewDrawable(R.drawable.incomming, context)
                    }

                    "Outgoing" -> {
                        icon.setImageViewDrawable(R.drawable.outgoing_24, context)
                    }

                    "Missed" -> {
                        icon.setImageViewDrawable(R.drawable.call_missed_24, context)
                        val color = ContextCompat.getColor(context, R.color.color_red)
                        icon.setColorFilter(color)
                    }

                    "Rejected" -> {
                        icon.setImageViewDrawable(R.drawable.rejected_24, context)
                    }

                    "Unknown" -> {
                        icon.setImageViewDrawable(R.drawable.unknown, context)

                    }
                }
                duration.text = formatDuration(callLogEntry?.duration?.toInt() ?: 0)
            }
        }

    }

    override fun onBindViewHolder(holder: AllCallViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AllCallViewHolder(
            AllCallLogItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

}