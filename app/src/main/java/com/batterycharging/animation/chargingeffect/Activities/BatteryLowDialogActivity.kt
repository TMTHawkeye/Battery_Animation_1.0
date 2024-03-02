package com.batterycharging.animation.chargingeffect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.databinding.ActivityBatteryLowDialogBinding

class BatteryLowDialogActivity : BaseActivity() {
    lateinit var binding: ActivityBatteryLowDialogBinding
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