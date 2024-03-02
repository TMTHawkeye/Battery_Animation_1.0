package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.batteryanimation.HelperClasses.prEvents
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityBatteryLowDialogBinding

class BatteryLowDialogActivity : AppCompatActivity() {
    lateinit var binding:ActivityBatteryLowDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBatteryLowDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeDialogId.setOnClickListener {
            prEvents("closeDialogId","Close Button from Battery Low Dialog is pressed!")

            finish()
        }

        binding.cardOk.setOnClickListener {
            prEvents("cardOk","Ok Button from Battery Low Dialog is pressed!")

            finish()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)
    }
}