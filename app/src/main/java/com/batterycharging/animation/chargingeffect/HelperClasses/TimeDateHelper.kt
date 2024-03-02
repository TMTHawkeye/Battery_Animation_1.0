package com.batterycharging.animation.chargingeffect.HelperClasses

import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getCurrentTime(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentTime = java.time.LocalTime.now()
        String.format("%02d\n%02d", currentTime.hour, currentTime.minute)
    } else {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        String.format("%02d\n%02d", hour, minute)
    }
}


fun getCurrentDateFormatted(): String {
    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("EEEE dd, MMMM yyyy", Locale.getDefault())
    return dateFormat.format(currentDate)
}

fun getBatteryPercentage(intent: Intent?):Int{
    val level: Int = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
    val scale: Int = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 0) ?: 0
    val batteryPercentage = if (level != 0 && scale != 0) {
        (level * 100.0 / scale).toInt()
    } else {
        0
    }
    return batteryPercentage
}


