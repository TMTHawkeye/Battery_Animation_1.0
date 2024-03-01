package com.example.batteryanimation

import android.app.Application
import com.example.batteryanimation.DependencyInjection.mainModule
import com.google.firebase.FirebaseApp
import io.paperdb.Paper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Paper.init(this@MainApplication);
        startKoin {
            androidContext(this@MainApplication)
            modules(mainModule)
        }


    }
}