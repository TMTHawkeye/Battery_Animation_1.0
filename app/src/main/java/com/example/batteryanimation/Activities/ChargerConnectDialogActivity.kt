package com.example.batteryanimation.Activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.ModelClasses.SwitchStates
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityChargerConnectDialogBinding
import com.google.gson.Gson

class ChargerConnectDialogActivity : AppCompatActivity(),OnStateCharge {
    lateinit var binding:ActivityChargerConnectDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChargerConnectDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.closeDialogId.setOnClickListener {
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