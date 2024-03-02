package com.batterycharging.animation.chargingeffect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.databinding.ActivityMyCreationBinding

class MyCreationActivity : BaseActivity() {
    lateinit var binding: ActivityMyCreationBinding
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