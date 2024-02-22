package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityBatteryFullDialogBinding

class BatteryFullDialogActivity : AppCompatActivity() {
    lateinit var binding: ActivityBatteryFullDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBatteryFullDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeDialogId.setOnClickListener {
            finish()
        }

        binding.cardOk.setOnClickListener {
            finish()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)

    }
}