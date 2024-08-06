package com.infinity8.truecallermirror.service

import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile

class MyInCallService: InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        // Handle a new call
        currentCall = call
        call.registerCallback(callCallback)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        // Cleanup when call is removed
        currentCall = call
        call.unregisterCallback(callCallback)
        if (currentCall == call) {
            currentCall = null
        }
    }

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            when (state) {
                Call.STATE_RINGING -> {
                    // Incoming call
                    // You can auto-answer or prompt the user
                }
                Call.STATE_ACTIVE -> {
                    // Call is active
                }
                Call.STATE_DISCONNECTED -> {
                    // Call ended
                }

                Call.STATE_AUDIO_PROCESSING -> {
                    TODO()
                }

                Call.STATE_CONNECTING -> {
                    TODO()
                }

                Call.STATE_DIALING -> {
                    TODO()
                }

                Call.STATE_DISCONNECTING -> {
                    TODO()
                }

                Call.STATE_HOLDING -> {
                    TODO()
                }

                Call.STATE_NEW -> {
                    TODO()
                }

                Call.STATE_PULLING_CALL -> {
                    TODO()
                }

                Call.STATE_SELECT_PHONE_ACCOUNT -> {
                    TODO()
                }

                Call.STATE_SIMULATED_RINGING -> {
                    TODO()
                }
            }
        }
    }

    fun answerCall(call: Call) {
        call.answer(VideoProfile.STATE_AUDIO_ONLY)
    }
    companion object {
        var currentCall: Call? = null
    }
    fun rejectCall(call: Call) {
        call.reject(false, null)
    }
}