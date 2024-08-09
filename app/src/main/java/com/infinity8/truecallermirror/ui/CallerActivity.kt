package com.infinity8.truecallermirror.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.infinity8.truecallermirror.R
import com.infinity8.truecallermirror.databinding.ActivityCallerBinding
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.service.ReminderBroadcastReceiver
import com.infinity8.truecallermirror.ui.fragment.NoteBottomSheetDialogFragment
import com.infinity8.truecallermirror.uitls.formatDuration
import com.infinity8.truecallermirror.viewmodel.CallLogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class CallerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCallerBinding
    val list: MutableList<CallLogEntry> = mutableListOf()
    private val callViewModel: CallLogViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
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
            callViewModel.insertCallLog(latestCallLogEntry)
            latestCallLogEntry
        }

        resultUiOperation(result)
    }

    private fun resultUiOperation(result: Deferred<CallLogEntry?>) {
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
                    val bottomSheetFragment =
                        NoteBottomSheetDialogFragment(mainData.id, mainData.number)
                    bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                }
                binding.alarmIv.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val timePickerDialog = TimePickerDialog(
                        this@CallerActivity,
                        { _, hourOfDay, minute1 ->
                            val selectedTime = "$hourOfDay:$minute1"
                            binding.alarmTxt.text = selectedTime
                            setReminder(hourOfDay,minute1)
                        },
                        hour,
                        minute,
                        false
                    )
                    timePickerDialog.show()
                }
            } ?: run {
                binding.progress.visibility = View.GONE
            }
        }
    }
    private fun setReminder(hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Show a dialog to guide the user to allow the app to schedule exact alarms
            showExactAlarmPermissionDialog()
            return
        }

        try {
            // Set the alarm
            if (calendar.timeInMillis > System.currentTimeMillis()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                // If the selected time is already passed today, schedule it for tomorrow
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }

            Toast.makeText(this, "Reminder set for $hourOfDay:$minute", Toast.LENGTH_SHORT).show()

        } catch (e: SecurityException) {
            // Handle the security exception
            Toast.makeText(this, "Unable to set exact alarm: ${e.message}", Toast.LENGTH_SHORT).show()
            showExactAlarmPermissionDialog()
        }

    }

    private fun showExactAlarmPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exact Alarm Permission Required")
            .setMessage("To set an exact alarm, please allow the app to schedule exact alarms in system settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    startActivity(intent)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
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