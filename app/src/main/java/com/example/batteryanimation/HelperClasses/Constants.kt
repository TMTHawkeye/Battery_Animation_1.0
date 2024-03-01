package com.example.batteryanimation.HelperClasses

import android.os.Bundle
import android.util.Log
import com.example.batteryanimation.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
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