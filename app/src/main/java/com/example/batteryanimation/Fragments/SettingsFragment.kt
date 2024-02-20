package com.example.batteryanimation.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.batteryanimation.Activities.ChargingAlarm
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsFragment : Fragment() {
    lateinit var binding:FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(layoutInflater,container,false)

        binding.cardChargingNotificationId.setOnClickListener {
            startActivity(Intent(requireContext(),ChargingAlarm::class.java))
        }

//        batteryInfoViewModel.isSwitchOn.observe(viewLifecycleOwner, Observer{ isSwitchOn ->
//            binding.fullChargeNotificationSwitch.isChecked = isSwitchOn
//        })

//        binding.fullChargeNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
//            batteryInfoViewModel.updateSwitchState(isChecked)
//        }
//        batteryInfoViewModel.batteryPercentage.observeForever { batteryPercentage ->
//            if (batteryInfoViewModel.isSwitchOn.value == true && batteryPercentage == 99) {
//                Toast.makeText(requireContext(), "Charging completed successfully!", Toast.LENGTH_LONG).show()
//            }
//        }


        return binding.root
    }

}