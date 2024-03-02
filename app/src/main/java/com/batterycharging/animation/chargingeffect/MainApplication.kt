package com.batterycharging.animation.chargingeffect

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.batterycharging.animation.chargingeffect.DependencyInjection.mainModule
import com.google.firebase.FirebaseApp
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate
import io.paperdb.Paper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    private val localeAppDelegate = LocaleHelperApplicationDelegate()

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Paper.init(this@MainApplication);
        startKoin {
            androidContext(this@MainApplication)
            modules(mainModule)
        }


    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAppDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAppDelegate.onConfigurationChanged(this)
    }
    override fun getApplicationContext(): Context =
        LocaleHelper.onAttach(super.getApplicationContext())
}