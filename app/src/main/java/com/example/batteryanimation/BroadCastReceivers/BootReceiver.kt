package com.example.batteryanimation.BroadCastReceivers

import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.preference.PreferenceManager
import android.util.Log
import com.example.batteryanimation.Activities.BatteryFullDialogActivity
import com.example.batteryanimation.Activities.BatteryLowDialogActivity
import com.example.batteryanimation.Activities.ChargerConnectDialogActivity
import com.example.batteryanimation.Activities.ChargerDisconnectDialogActivity
import com.example.batteryanimation.Activities.SetAnimationAcivity
import com.example.batteryanimation.Activities.SetWallpaperActivity
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.MainActivity
import com.example.batteryanimation.ModelClasses.AnimationSwitchStates
import com.example.batteryanimation.ModelClasses.SwitchStates
import com.google.gson.Gson

class BootReceiver(private val onStateCharge: OnStateCharge) : BroadcastReceiver() {
    constructor() : this(object : OnStateCharge {
        override fun charge(isCharging: Boolean) {
            if (isCharging) {
            } else {
            }
        }
    })

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val intentString = intent.action
            val resumeIntent = getResumeIntent(context)
            val switchStates = getSwitchStates(context)
            val switchStatesAnmations = getSwitchStatesAnimations(context)
            when (intentString) {
                Intent.ACTION_POWER_CONNECTED -> {
                    // Log for debugging purposes
                    Log.d("Receiver", "Received ACTION_POWER_CONNECTED")

                    if (switchStatesAnmations.isactiveAnimationSwitchOn) {
                        saveAppState(context)
                        if (getActivityIntent(context).equals("animation")) {
                            if (!isActivityRunning(context, SetAnimationAcivity::class.java.name)) {
                                showConcernedActivity(
                                    context, SetAnimationAcivity::class.java, resumeIntent
                                )
                            }
                        } else if (getActivityIntent(context).equals("wallpaper")) {
                            if (!isActivityRunning(
                                    context, SetWallpaperActivity::class.java.name
                                )
                            ) {
                                showConcernedActivity(
                                    context, SetWallpaperActivity::class.java, resumeIntent
                                )
                            }
                        }
                    }

                    if(switchStates.isChargerConnectSwitchOn) {
                        if (!isActivityRunning(
                                context, ChargerConnectDialogActivity::class.java.name
                            )
                        ) {
                            showConcernedActivity(
                                context,
                                ChargerConnectDialogActivity::class.java,
                                resumeIntent
                            )
                        }
                    }

                    onStateCharge.charge(true)
                }

                Intent.ACTION_POWER_DISCONNECTED -> {
                    // Log for debugging purposes
                    Log.d("Receiver", "Received ACTION_POWER_DISCONNECTED")


                    if(switchStates.isChargerDisconnectSwitchOn) {
                        if (!isActivityRunning(
                                context, ChargerDisconnectDialogActivity::class.java.name
                            )
                        ) {
                            showConcernedActivity(
                                context,
                                ChargerDisconnectDialogActivity::class.java,
                                resumeIntent
                            )
                        }
                    }

                    // Notify discharging state
                    onStateCharge.charge(false)
                }

                Intent.ACTION_BATTERY_CHANGED -> {
                    val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryPercentage = (level.toFloat() / scale.toFloat() * 100).toInt()
                    saveBatteryPercentageToSharedPreference(context,batteryPercentage)
                    saveAppState(context)

                    if (switchStates.isFullBatterySwitchOn) {
                        if (batteryPercentage == 100 ) {
                            if (!isActivityRunning(
                                    context, BatteryFullDialogActivity::class.java.name
                                )
                            ) {
                                showConcernedActivity(
                                    context, BatteryFullDialogActivity::class.java, resumeIntent
                                )
                            }
                        }
                    }
                    if (switchStates.isLowBatterySwitchOn) {
                        if (batteryPercentage == 20) {
                            Log.d("Receiver", "Battery is low")
                            if (!isActivityRunning(
                                    context, BatteryLowDialogActivity::class.java.name
                                )
                            ) {
                                showConcernedActivity(
                                    context, BatteryLowDialogActivity::class.java, resumeIntent
                                )
                            }
                        }
                    }
                }


            }
        } catch (e: Exception) {
            Log.e("Receiver", "Error in onReceive: ${e.message}")
        }
    }

    private fun saveBatteryPercentageToSharedPreference(context: Context, batteryPercentage: Int) {
        val sharedPreferences = context.getSharedPreferences(Constants.BATTERY_PREFERENCE_FILE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("battery_percentage", batteryPercentage)
        editor.apply()
    }

    fun showConcernedActivity(context: Context, activity: Class<*>, resumeIntent: PendingIntent) {
        val showActivityIntent = Intent(context, activity)
        showActivityIntent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        showActivityIntent.putExtra(
            "resumeIntent", resumeIntent
        )
        context.startActivity(showActivityIntent)
    }

//    fun showChargerConnectedDialogActivity(context: Context) {
//        if (!isLowBatteryRunning(context)) {
//            saveAppState(context)
//            val showActivityIntent =
//                Intent(context, BatteryLowDialogActivity::class.java)
//            showActivityIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    or Intent.FLAG_ACTIVITY_NEW_TASK)
//            showActivityIntent.putExtra("resumeIntent", resumeIntent)
//            context.startActivity(showActivityIntent)
//        }
//    }

    private fun getResumeIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, MainActivity::class.java)
        resumeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            context, 0, resumeIntent, /*PendingIntent.FLAG_UPDATE_CURRENT or*/
            PendingIntent.FLAG_IMMUTABLE
        )
    }

//    fun getSwitchStateFromSharedPreferences(context: Context): Boolean {
//        val sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)
//        return sharedPreferences.getBoolean(Constants.SWITCH_STATE_ANIMATION_KEY, false)
//    }


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

    private fun isActivityRunning(context: Context, activityString: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningActivities = activityManager.getRunningTasks(1)
        if (runningActivities.isNotEmpty()) {
            val topActivity = runningActivities[0].topActivity
            return topActivity?.className == activityString
        }
        return false
    }

    /*  private fun isFullBatteryRunning(context: Context): Boolean {
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

      private fun isLowBatteryRunning(context: Context,activity: String): Boolean {
          // Check if UpdatingGiffPreviewActivity is currently running
          val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
          val runningActivities = activityManager.getRunningTasks(1)
          if (runningActivities.isNotEmpty()) {
              val topActivity = runningActivities[0].topActivity
              return topActivity?.className == activity
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
      }*/


    /*   fun register(context: Context) {
           val filter = IntentFilter().apply {
               addAction(Intent.ACTION_POWER_CONNECTED)
               addAction(Intent.ACTION_POWER_DISCONNECTED)
           }
           context.registerReceiver(this, filter)
       }

       fun unregister(context: Context) {
           context.unregisterReceiver(this)
       }*/

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

    fun getSwitchStatesAnimations(context: Context): AnimationSwitchStates {
        val sharedPreferences =
            context.getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)
        val savedSwitchStateString =
            sharedPreferences.getString(Constants.SWITCH_STATE_ANIMATION_KEY, null)
        val defaultSwitchState = AnimationSwitchStates(
            isactiveAnimationSwitchOn = false,
            isbatteryPercentageSwitchOn = false,
            isdouble_tap_closeSwitchOn = false,
        )
        return savedSwitchStateString?.let { deserializeSwitchStateAnimation(it) }
            ?: defaultSwitchState
    }

    private fun deserializeSwitchState(switchStateString: String): SwitchStates {
        return Gson().fromJson(switchStateString, SwitchStates::class.java)
    }

    private fun deserializeSwitchStateAnimation(switchStateString: String): AnimationSwitchStates {
        return Gson().fromJson(switchStateString, AnimationSwitchStates::class.java)
    }


}
