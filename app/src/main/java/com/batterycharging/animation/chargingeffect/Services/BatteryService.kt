package com.batterycharging.animation.chargingeffect.Services


import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.batterycharging.animation.chargingeffect.BroadCastReceivers.BootReceiver
import com.batterycharging.animation.chargingeffect.Interfaces.OnStateCharge
import com.batterycharging.animation.chargingeffect.R
 import java.util.Timer
import java.util.TimerTask

class BatteryService : Service() {

    private val CHANNEL_ID = "Battery Animations"
    private val FOREGROUND_SERVICE_ID = 101
    private var batteryBoardcastReciver: BootReceiver? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val drawableIcon : Int = R.drawable.app_icon ?: R.drawable.ic_launcher_foreground
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Battery Charging Animation")
            .setContentText("Battery Animation Applied.")
            .setSmallIcon(drawableIcon)
            .build()

        startForeground(FOREGROUND_SERVICE_ID, notification)

        batteryBoardcastReciver = BootReceiver(object : OnStateCharge {
            override fun charge(isCharging: Boolean) {
                if (isCharging) {
                } else {

                }
            }
        })

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryBoardcastReciver, filter)


        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())
        val doAsynchronousTask = object : TimerTask() {
            override fun run() {
                handler.post {
                }
            }
        }
        timer.schedule(doAsynchronousTask, 5000L, 5000L)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Battery Charging Animation is Started", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the BroadcastReceiver to avoid memory leaks
        batteryBoardcastReciver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}