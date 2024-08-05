package com.infinity8.truecallermirror.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import com.infinity8.truecallermirror.R
import com.infinity8.truecallermirror.controller.Callbacks
import com.infinity8.truecallermirror.model.CallLogEntry
import com.infinity8.truecallermirror.uitls.flowWithLifecycleUI
import com.infinity8.truecallermirror.uitls.handleStateData
import com.infinity8.truecallermirror.viewmodel.CallLogViewModel
import dagger.hilt.android.AndroidEntryPoint
import isPermissionGranted
import requestMultiplePermission

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Callbacks {
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        arrayOf(
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ANSWER_PHONE_CALLS
        )
    } else {
        arrayOf(
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_PHONE_STATE,
        )
    }
    private val callViewModel: CallLogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        if (!Settings.canDrawOverlays(this)) {
            overlayPermissionLauncher.launch(intent)
        }
        requestPermissions()
    }

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Settings.canDrawOverlays(this)) {
                // Permission granted, proceed with the app
                Toast.makeText(this, "Overlay permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this, "Overlay permission required", Toast.LENGTH_SHORT).show()
            }
        }
    private val requestPermissionsLauncher =
        this@MainActivity.requestMultiplePermission(allowedPermission = {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
            fetchCallLogs()
        }, deniedPermission = {
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()

        })


    private fun requestPermissions() {
        if (isPermissionGranted(permissions)) {
            // Permissions are already granted
            fetchCallLogs()
        } else {
            // Directly request the permissions
            requestPermissionsLauncher.launch(permissions)
        }
    }

    private fun fetchCallLogs() {
        flowWithLifecycleUI(callViewModel.callLogs, Lifecycle.State.CREATED) { data ->
            data.handleStateData(this@MainActivity)
        }
    }

    override fun <T> successListResponse(result: List<T>) {
        val list = result.filterIsInstance<CallLogEntry>()
        /*
                Log.d("fwoueori: ", "fwoiueori: ${list.size}")
        */
        displayCallLogs(list)
    }


    private fun displayCallLogs(callLogs: List<CallLogEntry>) {
        // For demonstration, printing logs in logcat
        /*     for (call in callLogs) {
                 Log.d(
                     "CallLog",
                     "Number: ${call.number}, Type: ${call.type}, Date: ${call.date}, Duration: ${call.duration},Contact Name: ${call.name}"
                 )
             }*/

        // Implement your UI update or further processing logic here
    }
}