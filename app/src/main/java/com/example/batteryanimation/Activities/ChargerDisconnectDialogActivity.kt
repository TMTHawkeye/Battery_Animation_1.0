package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.batteryanimation.HelperClasses.prEvents
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityChargerConnectDialogBinding
import com.example.batteryanimation.databinding.ActivityChargerDisconnectDialogBinding

class ChargerDisconnectDialogActivity : AppCompatActivity() ,OnStateCharge{
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