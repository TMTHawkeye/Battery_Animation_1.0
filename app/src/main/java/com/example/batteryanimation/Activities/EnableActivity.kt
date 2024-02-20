package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityEnableBinding

class EnableActivity : AppCompatActivity() {
    lateinit var binding:ActivityEnableBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEnableBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}