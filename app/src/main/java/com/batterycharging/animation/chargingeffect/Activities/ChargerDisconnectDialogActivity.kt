package com.batterycharging.animation.chargingeffect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.Interfaces.OnStateCharge
import com.batterycharging.animation.chargingeffect.databinding.ActivityChargerDisconnectDialogBinding

class ChargerDisconnectDialogActivity : BaseActivity() , OnStateCharge {
    lateinit var binding: ActivityChargerDisconnectDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargerDisconnectDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeDialogId.setOnClickListener {
            prEvents("closeDialogId","Close Button from Charger DisConnected Dialog is pressed!")

            finish()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)

    }

    override fun charge(isCharging: Boolean) {
        finish()
    }
}