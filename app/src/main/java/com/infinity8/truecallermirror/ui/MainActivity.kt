package com.infinity8.truecallermirror.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.infinity8.truecallermirror.R
import com.infinity8.truecallermirror.controller.Callbacks
import com.infinity8.truecallermirror.databinding.ActivityMainBinding
import com.infinity8.truecallermirror.model.CallLogEntry
import dagger.hilt.android.AndroidEntryPoint
import isPermissionGranted
import requestMultiplePermission

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), Callbacks {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        arrayOf(
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ANSWER_PHONE_CALLS,
            android.Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_PHONE_STATE,
        )
    }
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
         navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        bottomNavItemChangeListener(binding.bottomNavigationView)
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        if (!Settings.canDrawOverlays(this)) {
            overlayPermissionLauncher.launch(intent)
        }

/*
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)*/
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
    private fun bottomNavItemChangeListener(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            if (item.itemId != navView.selectedItemId) {
                navController.popBackStack(item.itemId, inclusive = true, saveState = false)
                navController.navigate(item.itemId)
            }
            true
        }}
    private val requestPermissionsLauncher =
        this@MainActivity.requestMultiplePermission(allowedPermission = {
            Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show()
//            fetchCallLogs()
        }, deniedPermission = {
            Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()

        })


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        if (isPermissionGranted(permissions)) {
            // Permissions are already granted
//            fetchCallLogs()
        } else {
            // Directly request the permissions
            requestPermissionsLauncher.launch(permissions)
        }
    }

/*    private fun fetchCallLogs() {
        flowWithLifecycleUI(callViewModel.callLogs, Lifecycle.State.CREATED) { data ->
            data.handleStateData(this@MainActivity)
        }
    }

    override fun <T> successListResponse(result: List<T>) {
        val list = result.filterIsInstance<CallLogEntry>()
        *//*
                Log.d("fwoueori: ", "fwoiueori: ${list.size}")
        *//*
        displayCallLogs(list)
    }*/


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