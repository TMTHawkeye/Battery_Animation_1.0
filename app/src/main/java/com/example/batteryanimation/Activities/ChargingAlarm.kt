package com.example.batteryanimation.Activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import com.example.batteryanimation.BroadCastReceivers.BootReceiver
import com.example.batteryanimation.Interfaces.OnStateCharge
import com.example.batteryanimation.MainActivity
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.ActivityChargingAlarmBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChargingAlarm : AppCompatActivity() {
    lateinit var binding: ActivityChargingAlarmBinding
    val batteryInfoViewModel: BatteryInfoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        batteryInfoViewModel.switchState.observe(this, Observer { switchState ->
            // Update UI based on the switch state
            binding.lowBatterySwitch.isChecked = switchState.isLowBatterySwitchOn
            binding.fullBatterySwitch.isChecked = switchState.isFullBatterySwitchOn
            binding.chargerConnectSwitch.isChecked = switchState.isChargerConnectSwitchOn
            binding.chargerDisconnectSwitch.isChecked = switchState.isChargerDisconnectSwitchOn
        })

        // Set up listeners for switch state changes
        binding.lowBatterySwitch.setOnCheckedChangeListener { _, isChecked ->
            batteryInfoViewModel.updateSwitchState("low_battery", isChecked)
        }
        binding.fullBatterySwitch.setOnCheckedChangeListener { _, isChecked ->
            batteryInfoViewModel.updateSwitchState("full_battery", isChecked)
        }
        binding.chargerConnectSwitch.setOnCheckedChangeListener { _, isChecked ->
            batteryInfoViewModel.updateSwitchState("charger_connect", isChecked)
        }
        binding.chargerDisconnectSwitch.setOnCheckedChangeListener { _, isChecked ->
            batteryInfoViewModel.updateSwitchState("charger_disconnect", isChecked)
        }

    }
}