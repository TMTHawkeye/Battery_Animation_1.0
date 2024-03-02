package com.batterycharging.animation.chargingeffect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.Interfaces.OnStateCharge
import com.batterycharging.animation.chargingeffect.databinding.ActivityChargerConnectDialogBinding

class ChargerConnectDialogActivity : BaseActivity(), OnStateCharge {
    lateinit var binding: ActivityChargerConnectDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChargerConnectDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.closeDialogId.setOnClickListener {
            prEvents("closeDialogId","Close Button from Charger Connected Dialog is pressed!")

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