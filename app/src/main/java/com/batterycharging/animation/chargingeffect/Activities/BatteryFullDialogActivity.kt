package com.batterycharging.animation.chargingeffect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.databinding.ActivityBatteryFullDialogBinding

class BatteryFullDialogActivity : BaseActivity() {
    lateinit var binding: ActivityBatteryFullDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBatteryFullDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeDialogId.setOnClickListener {
            prEvents("closeDialogId","Close Button from Battery Full Dialog is pressed!")
            finish()
        }

        binding.cardOk.setOnClickListener {
            prEvents("cardOk","Ok Button from Battery Full Dialog is pressed!")

            finish()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)

    }
}