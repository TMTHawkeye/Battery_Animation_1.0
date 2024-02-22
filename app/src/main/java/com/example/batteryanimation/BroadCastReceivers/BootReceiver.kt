package com.example.batteryanimation.BroadCastReceivers

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.batteryanimation.Activities.SetAnimationAcivity
import com.example.batteryanimation.Activities.SetWallpaperActivity
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.MainActivity

class BootReceiver(private val onStateCharge: OnStateCharge) : BroadcastReceiver() {

//    override fun onReceive(context: Context?, intent: Intent?) {
//        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
//            val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
//            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
//                    || status == BatteryManager.BATTERY_STATUS_FULL
//            onStateCharge.charge(isCharging)
//        }
//    }

    // Default constructor
    constructor() : this(object : OnStateCharge {
        override fun charge(isCharging: Boolean) {
            if (isCharging) {
                // Handle charging state
            } else {
                // Handle discharging state
            }
        }
    })

//    override fun onReceive(context: Context, intent: Intent) {
//        val intentString = intent.action
//        when (intentString) {
//            Intent.ACTION_POWER_CONNECTED -> {
//                // Log for debugging purposes
//                Log.d("UpdatingGiffReceiver", "Received ACTION_POWER_CONNECTED")
//
//                // Check if UpdatingGiffPreviewActivity is already running
//                if (getActivityIntent(context).equals("animation")) {
//                    if (!isSetAnimationRunning(context)) {
//                        val showActivityIntent =
//                            Intent(context, SetAnimationAcivity::class.java)
//                        showActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        context.startActivity(showActivityIntent)
//                    }
//
//                } else if (getActivityIntent(context).equals("wallpaper")) {
//                    if (!isSetWallpaperRunning(context)) {
//
//                        val showActivityIntent = Intent(context, SetWallpaperActivity::class.java)
//                        showActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        context.startActivity(showActivityIntent)
//                    }
//                }
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                        CustomDialogChargerConnected(context).showChargerConnectedDialog()
////                    }
//
//                // Notify charging state
//                onStateCharge.charge(true)
//
//            }
//
//            Intent.ACTION_POWER_DISCONNECTED -> {
//                // Log for debugging purposes
//                Log.d("UpdatingGiffReceiver", "Received ACTION_POWER_DISCONNECTED")
////                Toast.makeText(context, "Charging DisConnected", Toast.LENGTH_SHORT).show()
//
//
//                // Notify discharging state
//                onStateCharge.charge(false)
//            }
//        }
//    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val intentString = intent.action
            val resumeIntent = getResumeIntent(context) // Get the PendingIntent to resume the app

            when (intentString) {
                Intent.ACTION_POWER_CONNECTED -> {
                    // Log for debugging purposes
                    Log.d("UpdatingGiffReceiver", "Received ACTION_POWER_CONNECTED")

                    // Save the state of the app
                    saveAppState(context)

                    // Check if UpdatingGiffPreviewActivity is already running
                    if (getActivityIntent(context).equals("animation")) {
                       /* if (!isSetAnimationRunning(context)) {
                            val showActivityIntent =
                                Intent(context, SetAnimationAcivity::class.java)
                            showActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            showActivityIntent.putExtra("resumeIntent", resumeIntent) // Pass the PendingIntent to the activity
                            context.startActivity(showActivityIntent)
                        }*/
                        if (!isSetAnimationRunning(context)) {
                            val showActivityIntent = Intent(context, SetAnimationAcivity::class.java)
                            showActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            showActivityIntent.putExtra("resumeIntent", resumeIntent) // Pass the PendingIntent to the activity
                            context.startActivity(showActivityIntent)
                        }

                    } else if (getActivityIntent(context).equals("wallpaper")) {
                        if (!isSetWallpaperRunning(context)) {
                            val showActivityIntent = Intent(context, SetWallpaperActivity::class.java)
                            showActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            showActivityIntent.putExtra("resumeIntent", resumeIntent) // Pass the PendingIntent to the activity
                            context.startActivity(showActivityIntent)
                        }
                    }

                    // Notify charging state
                    onStateCharge.charge(true)
                }

                Intent.ACTION_POWER_DISCONNECTED -> {
                    // Log for debugging purposes
                    Log.d("UpdatingGiffReceiver", "Received ACTION_POWER_DISCONNECTED")

                    // Notify discharging state
                    onStateCharge.charge(false)
                }
            }
        } catch (e: Exception) {
            // Log the exception to a file or SharedPreferences for later examination
            Log.e("UpdatingGiffReceiver", "Error in onReceive: ${e.message}")
            // You can also log additional details or the stack trace if needed
            // Log.e("UpdatingGiffReceiver", Log.getStackTraceString(e))
        }
    }


    private fun getResumeIntent(context: Context): PendingIntent {
        // Create an intent to launch the main activity of your app
        val resumeIntent = Intent(context, MainActivity::class.java)
        // Modify the intent to clear the back stack and start the activity as a new task
        resumeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // Create the PendingIntent with FLAG_IMMUTABLE
        return PendingIntent.getActivity(context, 0, resumeIntent, /*PendingIntent.FLAG_UPDATE_CURRENT or*/ PendingIntent.FLAG_IMMUTABLE)
    }


    private fun saveAppState(context: Context) {
        // Save the state of the app to persistent storage (e.g., SharedPreferences, a local database)
        // You can save relevant data such as the current activity, UI state, etc.
        // For simplicity, let's assume we're saving a boolean indicating whether the app was in animation or wallpaper mode
        val sharedPreferences = context.getSharedPreferences("app_state", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_animation_mode", getActivityIntent(context).equals("animation"))
        editor.apply()
    }


    fun getActivityIntent(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("SetActivity", Context.MODE_PRIVATE)
        return sharedPreferences.getString("intent", "animation")
    }

//    override fun onReceive(context: Context, intent: Intent) {
//        val intentString = intent.action
//        when (intentString) {
//            Intent.ACTION_TIME_TICK -> {
//                val currentTime = Calendar.getInstance()
//                val hour = currentTime.get(Calendar.HOUR_OF_DAY)
//                val minute = currentTime.get(Calendar.MINUTE)
//
//                if (hour == 11 && minute == 54) {
//                    // Log for debugging purposes
//                    Log.d("UpdatingGiffReceiver", "$hour and $minute")
//
//                    // Check if UpdatingGiffPreviewActivity is already running
////                    if (!isUpdatingGiffPreviewActivityRunning(context)) {
//                        // Show the dialog when the current time is 11:05 AM
////                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                            CustomDialogChargerConnected(context).showChargerConnectedDialog()
////                        }
//
//                    if (!isUpdatingGiffPreviewActivityRunning(context)) {
//                        val showActivityIntent = Intent(context, SetAnimationAcivity::class.java)
//                        showActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        context.startActivity(showActivityIntent)
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                        CustomDialogChargerConnected(context).showChargerConnectedDialog()
////                    }
//
//                        // Notify charging state
//                        onStateCharge.charge(true)
//                    }
//                        // Notify charging state
////                    }
//                } else {
//                    // Log for debugging purposes
//                    Log.d("UpdatingGiffReceiver", "Current time is not 11:05 AM")
//
//                    // Notify discharging state
//                    onStateCharge.charge(false)
//                }
//            }
//        }
//    }


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
}
