package com.example.batteryanimation.BroadCastReceivers

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.batteryanimation.Activities.ChargingAlarm
import com.example.batteryanimation.Activities.SetAnimationAcivity
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.Services.BatteryService
import java.io.FileNotFoundException

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
    override fun onReceive(context: Context, intent: Intent) {
        val intentString = intent.action
        when (intentString) {
            Intent.ACTION_POWER_CONNECTED -> {
                // Log for debugging purposes
                Log.d("UpdatingGiffReceiver", "Received ACTION_POWER_CONNECTED")

                // Check if UpdatingGiffPreviewActivity is already running
                if (!isUpdatingGiffPreviewActivityRunning(context)) {
                    val showActivityIntent = Intent(context, SetAnimationAcivity::class.java)
                    showActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(showActivityIntent)
                    // Notify charging state
                    onStateCharge.charge(true)
                }
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                // Log for debugging purposes
                Log.d("UpdatingGiffReceiver", "Received ACTION_POWER_DISCONNECTED")
                Toast.makeText(context, "Charging DisConnected", Toast.LENGTH_SHORT).show()
                // Notify discharging state
                onStateCharge.charge(false)
            }
        }
    }

    private fun isUpdatingGiffPreviewActivityRunning(context: Context): Boolean {
        // Check if UpdatingGiffPreviewActivity is currently running
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningActivities = activityManager.getRunningTasks(1)
        if (runningActivities.isNotEmpty()) {
            val topActivity = runningActivities[0].topActivity
            return topActivity?.className == SetAnimationAcivity::class.java.name
        }
        return false
    }

    // Function to register the receiver
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
