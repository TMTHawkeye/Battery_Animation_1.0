package com.example.batteryanimation.HelperClasses

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