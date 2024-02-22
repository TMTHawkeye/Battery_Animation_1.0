package com.example.batteryanimation.BroadCastReceivers

import android.app.ActivityManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi
import com.example.batteryanimation.Activities.BatteryFullDialogActivity
import com.example.batteryanimation.Activities.BatteryLowDialogActivity
import com.example.batteryanimation.Activities.SetAnimationAcivity
import com.example.batteryanimation.Activities.SetWallpaperActivity
import com.example.batteryanimation.CustomDialogs.CustomDialogChargerConnected
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.MainActivity
import com.example.batteryanimation.ModelClasses.SwitchStates
import com.example.batteryanimation.databinding.CustomDialogBatteryFullBinding
import com.google.gson.Gson
import java.util.Calendar

class BootReceiver(private val onStateCharge: OnStateCharge) : BroadcastReceiver() {

    lateinit var dialogChargerConnected: CustomDialogChargerConnected

    constructor() : this(object : OnStateCharge {
        override fun charge(isCharging: Boolean) {
            if (isCharging) {
                // Handle charging state
            } else {
                // Handle discharging state
            }
        }
    })

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val intentString = intent.action
            val resumeIntent = getResumeIntent(context) // Get the PendingIntent to resume the app
            /*var batteryFullDetected = false*/ // Declare this variable globally in your class
            val switchStates = getSwitchStates(context)

            when (intentString) {
                Intent.ACTION_POWER_CONNECTED -> {
                    // Log for debugging purposes
                    Log.d("Receiver", "Received ACTION_POWER_CONNECTED")

                    if(getSwitchStateFromSharedPreferences(context)) {
                        saveAppState(context)
                        if (getActivityIntent(context).equals("animation")) {
                            if (!isSetAnimationRunning(context)) {
                                val showActivityIntent =
                                    Intent(context, SetAnimationAcivity::class.java)
                                showActivityIntent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                showActivityIntent.putExtra(
                                    "resumeIntent",
                                    resumeIntent
                                )
                                context.startActivity(showActivityIntent)
                            }
                        } else if (getActivityIntent(context).equals("wallpaper")) {
                            if (!isSetWallpaperRunning(context)) {
                                val showActivityIntent =
                                    Intent(context, SetWallpaperActivity::class.java)
                                showActivityIntent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                showActivityIntent.putExtra(
                                    "resumeIntent",
                                    resumeIntent
                                )
                                context.startActivity(showActivityIntent)
                            }
                        }
                    }

                    onStateCharge.charge(true)
                }

                Intent.ACTION_POWER_DISCONNECTED -> {
                    // Log for debugging purposes
                    Log.d("Receiver", "Received ACTION_POWER_DISCONNECTED")

                    // Notify discharging state
                    onStateCharge.charge(false)
                }

                Intent.ACTION_BATTERY_CHANGED -> {
                    val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryPercentage = (level.toFloat() / scale.toFloat() * 100).toInt()

                    if (switchStates.isFullBatterySwitchOn) {
                        if (batteryPercentage == 100 /*&& !batteryFullDetected*/) {
                            Log.d("Receiver", "Battery is full")
                            /*
                        batteryFullDetected = true // Set the flag to true to prevent further detections
*/
                            if (!isFullBatteryRunning(context)) {
                                saveAppState(context)
                                val showActivityIntent =
                                    Intent(context, BatteryFullDialogActivity::class.java)
                                showActivityIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        or Intent.FLAG_ACTIVITY_NEW_TASK)
                                showActivityIntent.putExtra("resumeIntent", resumeIntent)
                                context.startActivity(showActivityIntent)
//                        context?.unregisterReceiver(this)
                            }
                        }
                    }
                    if (switchStates.isLowBatterySwitchOn) {
                        if (batteryPercentage == 20) {
                            Log.d("Receiver", "Battery is low")
                            if (!isLowBatteryRunning(context)) {
                                saveAppState(context)
                                val showActivityIntent =
                                    Intent(context, BatteryLowDialogActivity::class.java)
                                showActivityIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        or Intent.FLAG_ACTIVITY_NEW_TASK)
                                showActivityIntent.putExtra("resumeIntent", resumeIntent)
                                context.startActivity(showActivityIntent)
//                        context?.unregisterReceiver(this)
                            }
                        }
                    }
                }


            }
        } catch (e: Exception) {
            Log.e("Receiver", "Error in onReceive: ${e.message}")
        }
    }


    private fun getResumeIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, MainActivity::class.java)
        resumeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            context,
            0,
            resumeIntent, /*PendingIntent.FLAG_UPDATE_CURRENT or*/
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun getSwitchStateFromSharedPreferences(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(Constants.SWITCH_STATE_ANIMATION_KEY, false)
    }


    private fun saveAppState(context: Context) {

        val sharedPreferences = context.getSharedPreferences("app_state", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_animation_mode", getActivityIntent(context).equals("animation"))
        editor.apply()
    }


    fun getActivityIntent(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("SetActivity", Context.MODE_PRIVATE)
        return sharedPreferences.getString("intent", "animation")
    }

    private fun isSetAnimationRunning(context: Context): Boolean {
        // Check if UpdatingGiffPreviewActivity is currently running
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningActivities = activityManager.getRunningTasks(1)
        if (runningActivities.isNotEmpty()) {
            val topActivity = runningActivities[0].topActivity
            return topActivity?.className == SetAnimationAcivity::class.java.name
        }
        return false
    }

    private fun isFullBatteryRunning(context: Context): Boolean {
        // Check if UpdatingGiffPreviewActivity is currently running
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningActivities = activityManager.getRunningTasks(1)
        if (runningActivities.isNotEmpty()) {
            val topActivity = runningActivities[0].topActivity
            return topActivity?.className == BatteryFullDialogActivity::class.java.name
        }
        return false
    }

    private fun isLowBatteryRunning(context: Context): Boolean {
        // Check if UpdatingGiffPreviewActivity is currently running
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningActivities = activityManager.getRunningTasks(1)
        if (runningActivities.isNotEmpty()) {
            val topActivity = runningActivities[0].topActivity
            return topActivity?.className == BatteryLowDialogActivity::class.java.name
        }
        return false
    }

    private fun isSetWallpaperRunning(context: Context): Boolean {
        // Check if UpdatingGiffPreviewActivity is currently running
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningActivities = activityManager.getRunningTasks(1)
        if (runningActivities.isNotEmpty()) {
            val topActivity = runningActivities[0].topActivity
            return topActivity?.className == SetWallpaperActivity::class.java.name
        }
        return false
    }


    fun register(context: Context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        context.registerReceiver(this, filter)
    }

    // Function to unregister the receiver
    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }

    fun getSwitchStates(context: Context): SwitchStates {
        val sharedPreferences =
            context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val savedSwitchStateString = sharedPreferences.getString(Constants.SWITCH_STATE_KEY, null)
        val defaultSwitchState = SwitchStates(
            isLowBatterySwitchOn = false,
            isFullBatterySwitchOn = false,
            isChargerConnectSwitchOn = false,
            isChargerDisconnectSwitchOn = false
        )
        return savedSwitchStateString?.let { deserializeSwitchState(it) } ?: defaultSwitchState
    }

    private fun deserializeSwitchState(switchStateString: String): SwitchStates {
        return Gson().fromJson(switchStateString, SwitchStates::class.java)
    }


}
