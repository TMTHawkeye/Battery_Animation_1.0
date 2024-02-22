package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityBatteryLowDialogBinding

class BatteryLowDialogActivity : AppCompatActivity() {
    lateinit var binding:ActivityBatteryLowDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBatteryLowDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}