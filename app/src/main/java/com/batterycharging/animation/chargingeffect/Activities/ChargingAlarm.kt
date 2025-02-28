package com.batterycharging.animation.chargingeffect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.ViewModels.BatteryInfoViewModel
import com.batterycharging.animation.chargingeffect.databinding.ActivityChargingAlarmBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChargingAlarm : BaseActivity() {
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
            prEvents("lowBatterySwitch","Low battery Switch from ChargingAlarm is pressed!")

            batteryInfoViewModel.updateSwitchState("low_battery", isChecked)
        }
        binding.fullBatterySwitch.setOnCheckedChangeListener { _, isChecked ->
            prEvents("fullBatterySwitch","Full battery Switch from ChargingAlarm is pressed!")

            batteryInfoViewModel.updateSwitchState("full_battery", isChecked)
        }
        binding.chargerConnectSwitch.setOnCheckedChangeListener { _, isChecked ->
            prEvents("chargerConnectSwitch","Charger Connect Switch from ChargingAlarm is pressed!")

            batteryInfoViewModel.updateSwitchState("charger_connect", isChecked)
        }
        binding.chargerDisconnectSwitch.setOnCheckedChangeListener { _, isChecked ->
            prEvents("chargerDisconnectSwitch","Charger disconnect Switch from ChargingAlarm is pressed!")

            batteryInfoViewModel.updateSwitchState("charger_disconnect", isChecked)
        }

        binding.backBtnId.setOnClickListener {
            finish()
        }
    }
}