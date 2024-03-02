package com.batterycharging.animation.chargingeffect.Activities

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl
import java.util.Locale

open class BaseActivity : AppCompatActivity() {
    val localeDelegate: LocaleHelperActivityDelegate = LocaleHelperActivityDelegateImpl()

    override fun getDelegate() = localeDelegate.getAppCompatDelegate(super.getDelegate())

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localeDelegate.attachBaseContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localeDelegate.onCreate(this)

    }

    override fun onStop() {
        super.onStop()

    }

    override fun onResume() {
        super.onResume()

        localeDelegate.onResumed(this)
    }

    override fun onPause() {
        super.onPause()

        localeDelegate.onPaused()
    }

    override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        val context = super.createConfigurationContext(overrideConfiguration)
        return LocaleHelper.onAttach(context)
    }

    override fun getApplicationContext(): Context =
        localeDelegate.getApplicationContext(super.getApplicationContext())

    open fun updateLocale(context: Activity, locale: Locale) {
        localeDelegate.setLocale(context, locale)
    }
}