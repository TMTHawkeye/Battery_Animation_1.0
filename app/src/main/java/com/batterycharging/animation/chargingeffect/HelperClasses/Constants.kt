package com.batterycharging.animation.chargingeffect.HelperClasses

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.R
import com.google.firebase.analytics.FirebaseAnalytics
//import com.google.firebase.analytics.FirebaseAnalytics
import io.paperdb.Paper
import timber.log.Timber

object Constants {
    const val PREF_NAME = "full_charge_notification"
    const val PREF_NAME_ANIMATION = "enable_animation"
    const val SAVED_ANIMATION="saved_animation"
    const val SAVED_ANIMATION_KEY="saved_animation_key"
    const val SWITCH_STATE_KEY = "switch_state"
    const val SWITCH_STATE_ANIMATION_KEY = "switch_state"
    const val BATTERY_PREFERENCE_FILE = "percentage"
    const val OVERLAY_PERMISSION_REQUEST_CODE = 123
}

fun prEvents(tag: String, s: String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, s)
    } else {
        Timber.tag(tag).e(s)
    }
}


fun sendNotiFication(
    firebaseAnalyticss: FirebaseAnalytics,
    eventNames: String?,
    eventDescription: String?,
) {
    if (eventNames != null && eventDescription != null) {
        val bundles = Bundle()
        bundles.putString(eventNames, eventDescription)
        firebaseAnalyticss.logEvent(eventNames, bundles)
    }
}

fun isFirstTimeLaunch(): Boolean {
    val lang_Staus= Paper.book().read<Boolean>("LangPref",true)
    return lang_Staus!!
}

fun isInternetAvailable(context:Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
    return networkInfo?.isConnectedOrConnecting == true
}

fun isReadStorageAllowed(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >=33) {
        // For Android 13 and higher, check for media images permission
        ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        // For Android 12 or lower, continue checking for READ_EXTERNAL_STORAGE permission
        ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun isWriteStorageAllowed(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >=33) {
        // For Android 13 and higher, check for media images permission
        ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        // For Android 12 or lower, continue checking for WRITE_EXTERNAL_STORAGE permission
        ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun requestStoragePermission(activity: Activity) {
    //android 13
    if (Build.VERSION.SDK_INT >=33) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
            activity.getString(R.string.storagePermissionCode).toInt()
        )
    } else {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            activity.getString(R.string.storagePermissionCode).toInt()
        )
    }
}