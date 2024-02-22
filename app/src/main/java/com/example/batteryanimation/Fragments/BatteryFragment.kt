package com.example.batteryanimation.Fragments

import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.FragmentBatteryBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class BatteryFragment : Fragment() {
    lateinit var binding: FragmentBatteryBinding
    val batteryInfoViewModel: BatteryInfoViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBatteryBinding.inflate(layoutInflater, container, false)
        getBatteryInfo()


        return binding.root
    }

    fun getBatteryInfo() {
        batteryInfoViewModel.remainingTime.observe(viewLifecycleOwner, Observer { remainingTime ->
            if (remainingTime != null) {
                val hours = remainingTime / (1000 * 60 * 60)
                val minutes = (remainingTime % (1000 * 60 * 60)) / (1000 * 60)
                val remainingTimeString = getString(R.string.battery_in_h_min, "$hours", "$minutes")
                binding.batteryRemainingId.text = remainingTimeString
                Log.d("TAG", " getBatteryInfo: $remainingTime")
            }

        })

        batteryInfoViewModel.batteryPercentage.observe(
            viewLifecycleOwner,
            Observer { batteryPercentage ->
                binding.batteryPercentageId.text = batteryPercentage.toString() + "%" ?: "100%"

            })

        batteryInfoViewModel.isCharging.observe(viewLifecycleOwner, Observer { isCharging ->
            if (isCharging) {
                binding.chargingStatusId.text = requireContext().getString(R.string.connected)
                binding.batteryRemainingId.visibility = View.VISIBLE

            } else {
                binding.chargingStatusId.text = requireContext().getString(R.string.disconnect)
                binding.batteryRemainingId.visibility = View.INVISIBLE

            }
        })

        batteryInfoViewModel.deviceTemperature.observe(viewLifecycleOwner, Observer {
            binding.temperatureTV.text = it.toString()+" degrees" ?: "0.0"
        })

        batteryInfoViewModel.voltage.observe(viewLifecycleOwner, Observer {
            binding.voltageTV.text = it.toString()+" mV" ?: "0 Mv"
        })

        batteryInfoViewModel.technology.observe(viewLifecycleOwner, Observer {
            binding.technologyTV.text = it.toString()
        })

        batteryInfoViewModel.health.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                if(it==2){
                    binding.healthTV.text = requireContext().getString(R.string.good)
                }
                else{
                    binding.healthTV.text = requireContext().getString(R.string.bad)

                }
            }
        })

        batteryInfoViewModel.capacity.observe(viewLifecycleOwner, Observer {
            binding.capacityTV.text = it.toString()+" mAh" ?: "3000 mAh"
        })

        batteryInfoViewModel.isCharging.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.pluggedTV.text = requireContext().getString(R.string.plugged)
            } else {
                binding.pluggedTV.text = requireContext().getString(R.string.unplugged)

            }
        })
    }

}