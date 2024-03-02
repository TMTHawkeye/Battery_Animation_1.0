package com.batterycharging.animation.chargingeffect

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.batterycharging.animation.chargingeffect.Activities.MainActivity
 import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FireBaseMessage: FirebaseMessagingService() {
    var notifications_Id: Long = 0
    val TAG = String::class.java.simpleName
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.data.isNotEmpty().let {
            if (remoteMessage.data.isNotEmpty()) {
                val msg: String = remoteMessage.data["message"].toString()
                sendsNotification(msg)
            }
        }
        remoteMessage.notification?.let {
            sendsNotification(remoteMessage.notification?.body)
        }
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registoriationToServer(token)
    }
    private fun registoriationToServer(token: String?) {
        Log.d(TAG, "registoriationToServer($token)")
    }
    private fun sendsNotification(messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val channelId = getString(R.string.defaultnotificationccc)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

}
