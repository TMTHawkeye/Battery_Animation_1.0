package com.batterycharging.animation.chargingeffect

import com.batterycharging.animation.chargingeffect.HelperClasses.sendNotiFication
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class TimberFirebase() : Timber.Tree(), KoinComponent {

    private val iFirebaseAnalytics : FirebaseAnalytics by inject()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        sendNotiFication(iFirebaseAnalytics,tag,message)
    }
}