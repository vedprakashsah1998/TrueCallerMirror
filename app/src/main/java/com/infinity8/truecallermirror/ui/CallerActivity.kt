package com.infinity8.truecallermirror.ui

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.infinity8.truecallermirror.R
import com.infinity8.truecallermirror.databinding.ActivityCallerBinding
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.ui.fragment.NoteBottomSheetDialogFragment
import com.infinity8.truecallermirror.uitls.formatDuration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class CallerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallerBinding
    val list: MutableList<CallLogEntry> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Use TYPE_APPLICATION_OVERLAY for Android 8.0+
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
        window.attributes = layoutParams
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.progress.visibility = View.VISIBLE

        callerLast()
        binding.closeButton.setOnClickListener {
            finishAffinity()
            finish()
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
        }


    }

    private fun callerLast() {
        binding.progress.visibility = View.VISIBLE
        val result = lifecycleScope.async(Dispatchers.IO) {
            var latestCallLogEntry: CallLogEntry? = null
            val projection = arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            )

            // Query the CallLog content provider for only the latest entry
            contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                    val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
                    val dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
                    val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)

                    val number = cursor.getString(numberIndex)
                    val type = when (cursor.getInt(typeIndex)) {
                        CallLog.Calls.INCOMING_TYPE -> "Incoming"
                        CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                        CallLog.Calls.MISSED_TYPE -> "Missed"
                        CallLog.Calls.REJECTED_TYPE -> "Rejected"
                        else -> "Unknown"
                    }
                    val date = Date(cursor.getLong(dateIndex))
                    val duration = cursor.getString(durationIndex)

                    val contactName = getContactName(number) ?: "Unknown"
                    latestCallLogEntry = CallLogEntry(0, number, contactName, type, date, duration)
                }
            }
            latestCallLogEntry
        }

        lifecycleScope.launch {
            val mainData = result.await()
            mainData?.let {
                binding.apply {
                    progress.visibility = View.GONE
                    callerNumber.text = it.number
                    callerName.text = it.name
                    callType.text = it.type
                    durationTxt.text = formatDuration(it.duration.toInt())
                }
                binding.addNote.setOnClickListener {
                    val bottomSheetFragment = NoteBottomSheetDialogFragment(mainData.id, mainData.number)
                    bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                }
                binding.alarmIv.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val timePickerDialog = TimePickerDialog(
                        this@CallerActivity,
                        { _, hourOfDay, minute ->
                            // Handle the selected time here
                            val selectedTime = "$hourOfDay:$minute"
                            // Do something with the selected time, e.g., set an alarm, display it
                            binding.alarmTxt.text = selectedTime
                        },
                        hour,
                        minute,
                        true // is24HourView
                    )
                    timePickerDialog.show()
                }
            } ?: run {
                binding.progress.visibility = View.GONE
                // Handle the case where no call logs are available
            }
        }
    }

    private fun getContactName(phoneNumber: String): String? {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                return cursor.getString(nameIndex)
            }
        }
        return null
    }

}