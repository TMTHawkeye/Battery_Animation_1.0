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
    lateinit var binding:ActivityChargingAlarmBinding
    val batteryInfoViewModel: BatteryInfoViewModel by viewModel()

  private val stopAlarmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        // Unregister the BroadcastReceiver when the activity is destroyed
//        stopAlarmReceiver?.let { unregisterReceiver(it) }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        stopAlarmReceiver?.let { unregisterReceiver(it) }
//
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChargingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

//    BootReceiver().register(this@ChargingAlarm)
    val filter = IntentFilter("com.example.batteryanimation.STOP_CHARGING_ALARM")
    registerReceiver(stopAlarmReceiver, filter)
        batteryInfoViewModel.isSwitchOn.observe(this@ChargingAlarm, Observer{ isSwitchOn ->
            binding.fullBatterySwitch.isChecked = isSwitchOn
        })

        binding.fullBatterySwitch.setOnCheckedChangeListener { _, isChecked ->
            batteryInfoViewModel.updateSwitchState(isChecked)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                startActivity(Intent(this@ChargingAlarm,MainActivity::class.java))
            }

        })
//        batteryInfoViewModel.batteryPercentage.observeForever { batteryPercentage ->
//            if (batteryInfoViewModel.isSwitchOn.value == true && batteryPercentage == 99) {
//                Toast.makeText(requireContext(), "Charging completed successfully!", Toast.LENGTH_LONG).show()
//            }
//        }
    }

//    override fun charge(isCharging: Boolean) {
//        if(!isCharging){
//            val filter = IntentFilter().apply {
//                addAction("STOP_CHARGING_ALARM")
//            }
//        registerReceiver(broadcastReceiver, filter)
//        }
//
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        BootReceiver().unregister(this@ChargingAlarm)
//    }

//    override fun onPause() {
//        super.onPause()
//        BootReceiver().unregister(this@ChargingAlarm)
//    }
}