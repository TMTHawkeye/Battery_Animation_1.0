package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.AnimationViewModel
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.ActivityEnableBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableActivity : AppCompatActivity() {
    lateinit var binding:ActivityEnableBinding
    val animationViewModel: AnimationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEnableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animationViewModel.isSwitchOn.observe(this, Observer {
            binding.activeAnimationSwitch.isChecked = it
        })

        binding.activeAnimationSwitch.setOnCheckedChangeListener { _, isChecked ->
            animationViewModel.updateSwitchState(isChecked)
        }




    }
}