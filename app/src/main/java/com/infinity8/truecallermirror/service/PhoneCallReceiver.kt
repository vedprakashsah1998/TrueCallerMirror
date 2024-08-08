package com.infinity8.truecallermirror.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.infinity8.truecallermirror.ui.CallerActivity

class PhoneCallReceiver : BroadcastReceiver() {
    private var incomingNumber: String? = null
    private var number:String?=null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {

                    Log.d("fowiueroiu: ","fwoieroiu: $incomingNumber")
//                    val permissionIntent = Intent(context, CallerActivity::class.java)
//                    permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    context?.startActivity(permissionIntent)
                    /* val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                     val serviceIntent = Intent(context, FloatingWindowService::class.java)
                     serviceIntent.putExtra("incoming_number", incomingNumber)
                     context?.startService(serviceIntent)*/
//                    Log.d("PhoneCallReceiver", "Incoming call from: $incomingNumber")
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Log.d("PhoneCallReceiver", "Call picked up")
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {

                    Log.d("PhoneCallReceiver", "Call ended or idle$number")
                    val endCallIntent = Intent(context, CallerActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context?.startActivity(endCallIntent)
                }
            }
        }
    }


}