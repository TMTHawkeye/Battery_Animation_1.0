package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.batteryanimation.HelperClasses.prEvents
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityMyCreationBinding

class MyCreationActivity : AppCompatActivity() {
    lateinit var binding:ActivityMyCreationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMyCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnId.setOnClickListener {
            prEvents("backBtnId","Back btn switch from MyCreationActivity is pressed!")

            finish()
        }
    }
}