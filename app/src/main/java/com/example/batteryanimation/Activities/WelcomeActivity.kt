package com.example.batteryanimation.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieDrawable
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WelcomeActivity : AppCompatActivity() , CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + coroutineJob

    private var coroutineJob: Job = Job()
    lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launch {
            binding.welcomeLottieId.repeatCount = LottieDrawable.INFINITE
            binding.welcomeLottieId.playAnimation()
        }

        binding.letsGoCardId.setOnClickListener {
            launch {
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }

        })

    }
}