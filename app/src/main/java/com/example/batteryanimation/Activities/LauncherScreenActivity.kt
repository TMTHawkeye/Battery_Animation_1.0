package com.example.batteryanimation.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieDrawable
import com.example.batteryanimation.HelperClasses.isFirstTimeLaunch
import com.example.batteryanimation.HelperClasses.prEvents
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityLauncherScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LauncherScreenActivity : AppCompatActivity(), CoroutineScope {
    lateinit var binding: ActivityLauncherScreenBinding
    private var coroutineJob: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + coroutineJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        launch {
            binding.loadingLottieId.repeatCount = LottieDrawable.INFINITE
            binding.loadingLottieId.playAnimation()
        }

        coroutineJob = launch {
            delay(5000)
            navigateToNext()
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
                coroutineJob.cancel()
            }
        })

    }


    private fun navigateToNext() {
        prEvents("firstTimeLaunchCheck", "Guide Screen or Welcome Screen activity is displayed!")

        val intent = if (isFirstTimeLaunch()) {
            Intent(this@LauncherScreenActivity, LanguageActivity::class.java)
        } else {
            Intent(this@LauncherScreenActivity, WelcomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}