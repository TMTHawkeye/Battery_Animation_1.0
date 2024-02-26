package com.example.batteryanimation

import android.app.Application
import com.example.batteryanimation.DependencyInjection.mainModule
import io.paperdb.Paper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(mainModule)
        }
        Paper.init(applicationContext);

    }
}