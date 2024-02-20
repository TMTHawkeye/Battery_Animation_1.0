package com.example.batteryanimation.Services
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.batteryanimation.Activities.ChargingAlarm
import com.example.batteryanimation.BroadCastReceivers.BootReceiver
import com.example.batteryanimation.CustomDialogs.CustomDialogChargerConnected
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.MainActivity
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.CustomDialogChargerConnectedBinding
import com.example.batteryanimation.databinding.CustomDialogChargerDisconnectedBinding
import org.koin.android.ext.android.inject
import java.util.Timer
import java.util.TimerTask

class BatteryService : Service() {

    private val CHANNEL_ID = "Battery Animations"
    private val FOREGROUND_SERVICE_ID = 101
    private var batteryBoardcastReciver: BootReceiver? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("stringValues")
        var  selectedAnimationResId = intent?.getIntExtra("selectedAnimation", 0) ?: 0

        return START_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Software Updater")
            .setContentText("Battery Animation Applied.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(FOREGROUND_SERVICE_ID, notification)

        batteryBoardcastReciver = BootReceiver(object : OnStateCharge {
            override fun charge(isCharging: Boolean) {
                if (isCharging) {
                    // Device is connected to the charger
                } else {
                    // Device is disconnected from the charger
                }
            }
        })

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(batteryBoardcastReciver, filter)

        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())
        val doAsynchronousTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    // Perform background task here
                }
            }
        }
        timer.schedule(doAsynchronousTask, 5000L, 5000L)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Battery Charging Animation is Started",
                NotificationManager.IMPORTANCE_DEFAULT
            )
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
/*
import android.app.*

class UpdatingGiffService : Service() {

    private val CHANNEL_ID = "Battery_Animations"
    private val FOREGROUND_SERVICE_ID = 101
    private var batteryBroadcastReceiver: UpdatingGiffReceiver? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("stringValues")
        val selectedAnimationResId = intent?.getIntExtra("selectedAnimation", 0) ?: 0

        return START_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification()
        startForeground(FOREGROUND_SERVICE_ID, notification)

        batteryBroadcastReceiver = UpdatingGiffReceiver(object : OnStateCharge {
            override fun charge(isCharging: Boolean) {
                if (isCharging) {
                    // Device is connected to the charger
                } else {
                    // Device is disconnected from the charger
                }
            }
        })

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        registerReceiver(batteryBroadcastReceiver, filter)

        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())
        val doAsynchronousTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    // Perform background task here
                }
            }
        }
        timer.schedule(doAsynchronousTask, 5000L, 5000L)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Battery Charging Animation is Started",
                NotificationManager.IMPORTANCE_DEFAULT
            )


            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Software Updater")
                .setContentText("Battery Animation Applied.")
                .setSmallIcon(R.mipmap.app_icon)
                .build()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the BroadcastReceiver to avoid memory leaks
        batteryBroadcastReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}*/


//    private var batteryBoardcastReciver: BootReceiver? = null
//    private val batteryInfoViewModel: BatteryInfoViewModel by inject()
//    private var wasCharging = false
//    private val dialogChargerConnected: CustomDialogChargerConnected by lazy {
//        CustomDialogChargerConnected(this)
//    }
//    override fun onCreate() {
//        super.onCreate()
//
//        createNotificationChannel()
////        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
////            .setContentTitle("Software Updater")
////            .setContentText("Battery Animation Applied.")
////            .setSmallIcon(R.mipmap.ic_launcher)
////            .build()
//
//        startForeground(FOREGROUND_SERVICE_ID, createNotification())
//
//        batteryBoardcastReciver = BootReceiver(object : OnStateCharge {
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun charge(isCharging: Boolean) {
//                if (isCharging) {
//                    Log.d("TAG_service", "charge: true")
////                    batteryBoardcastReciver?.register(applicationContext)
////                    showChargerConnectedDialog()
////                    val showActivityIntent = Intent(applicationContext, ChargingAlarm::class.java)
////                    showActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
////                    applicationContext.startActivity(showActivityIntent)
//                } else {
//                    Log.d("TAG_service", "charge: false")
////                    batteryBoardcastReciver?.unregister(applicationContext)
//
////                    showDisconnectedDialog()
//
//                }
//            }
//        })
//
//        val filter = IntentFilter()
//        filter.addAction(Intent.ACTION_POWER_CONNECTED)
//        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
//        registerReceiver(batteryBoardcastReciver, filter)
//
//
//
////        val timer = Timer()
////        val handler = Handler(Looper.getMainLooper())
////        val doAsynchronousTask = object : TimerTask() {
////            override fun run() {
////                handler.post {
////                    // Perform background task here
////                }
////            }
////        }
////        timer.schedule(doAsynchronousTask, 5000L, 5000L)
////        batteryInfoViewModel = BatteryInfoViewModel(application)
//    }
//
////    override fun onBind(intent: Intent?): IBinder? {
////        return null
////    }
//
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "Battery Charging Animation is Started",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        // Unregister the BroadcastReceiver to avoid memory leaks
//        batteryBoardcastReciver?.let {
//            unregisterReceiver(it)
//        }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//
//
////    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
////        // Create the notification channel (required for API 26+)
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            createNotificationChannel()
////        }
////
////        // Start the service as a foreground service
////        startForeground(FOREGROUND_SERVICE_ID, createNotification())
////
////        // Start observing charging state
////        batteryInfoViewModel.isCharging.observeForever { newChargingState ->
////            if (newChargingState != null && newChargingState != wasCharging) {
////                wasCharging = newChargingState
////                if (newChargingState) {
////                    // Start the ChargingAlarm activity when charging begins
////                    val activityIntent = Intent(this, ChargingAlarm::class.java).apply {
////                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                    }
////                    startActivity(activityIntent)
////                } else {
////                    sendBroadcast(Intent("STOP_CHARGING_ALARM"))
////
////                }
////            }
////        }
////
////        // Return START_STICKY to ensure the service restarts if it's killed by the system
////        return START_STICKY
////    }
//
////    private fun createNotificationChannel() {
////        val channelId = "BatteryServiceChannel"
////        val channelName = "Battery Service Channel"
////        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
////        val notificationManager = getSystemService(NotificationManager::class.java)
////        notificationManager.createNotificationChannel(channel)
////    }
//
//    private fun createNotification(): Notification {
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Foreground Service")
//            .setContentText("Battery Service is running")
//            .setContentIntent(pendingIntent)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//
//        return builder.build()
//    }
//
//    companion object {
//        const val CHANNEL_ID = "BatteryServiceChannel"
//        const val FOREGROUND_SERVICE_ID = 101
//    }
//
//
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun showChargerConnectedDialog() {
////        val dialog_binding = CustomDialogChargerConnectedBinding.inflate(LayoutInflater.from(applicationContext))
////        val dialog = Dialog(applicationContext, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth)
////
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
////        dialog.setCancelable(false)
////        dialog.setContentView(dialog_binding.root)
////
////        val window: Window = dialog.window!!
////        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
////        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
////        window.setGravity(Gravity.CENTER)
////        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
////
////        dialog.show()
////
////        Handler(Looper.getMainLooper()).postDelayed({
////            dialog.dismiss()
////        }, 5000)
////
////        dialog_binding.closeDialogId.setOnClickListener {
////            dialog.dismiss()
////        }
////    }
//
////    @RequiresApi(Build.VERSION_CODES.O)
////    private fun showDisconnectedDialog() {
////        val dialog_binding = CustomDialogChargerDisconnectedBinding.inflate(LayoutInflater.from(applicationContext))
////        val dialog = Dialog(applicationContext, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth)
////
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
////        dialog.setCancelable(false)
////        dialog.setContentView(dialog_binding.root)
////
////        val window: Window = dialog.window!!
////        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
////        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
////        window.setGravity(Gravity.CENTER)
////        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
////
////        dialog.show()
////
////        Handler(Looper.getMainLooper()).postDelayed({
////            dialog.dismiss()
////        }, 5000)
////
////        dialog_binding.closeDialogId.setOnClickListener {
////            dialog.dismiss()
////        }
////    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun showChargerConnectedDialog() {
//        dialogChargerConnected.showChargerConnectedDialog()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun showDisconnectedDialog() {
//        dialogChargerConnected.dialog_binding.closeDialogId.setOnClickListener {
//            dialogChargerConnected.closeDialog()
//        }
//    }
//}

