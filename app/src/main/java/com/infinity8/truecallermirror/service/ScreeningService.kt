package com.infinity8.truecallermirror.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log

class ScreeningService: CallScreeningService()  {
    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle.schemeSpecificPart
        Log.d("fowueriou: ","fwoieroi: $phoneNumber")
        // Integrate with a spam detection service (replace with your actual implementation)
        val isSpam = checkSpamNumber(phoneNumber)

        if (isSpam) {
            // Block the call
//            respondToCall(callDetails, CallResponse.Builder().setAction(Call.REJECT).build())
        } else {
            // Allow the call
            respondToCall(callDetails, CallResponse.Builder().build())
        }
    }
    private fun checkSpamNumber(number: String): Boolean {
        // Call your spam detection service API here
        return true // Replace with actual logic
    }
}