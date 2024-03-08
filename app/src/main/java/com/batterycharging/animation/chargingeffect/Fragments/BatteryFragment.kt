package com.batterycharging.animation.chargingeffect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.R
 import com.batterycharging.animation.chargingeffect.ViewModels.BatteryInfoViewModel
import com.batterycharging.animation.chargingeffect.databinding.FragmentBatteryBinding
import com.google.android.gms.ads.LoadAdError
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.callbacks.AdCallback
import org.smrtobjads.ads.callbacks.AperoAdCallback


class BatteryFragment : Fragment() {
    lateinit var binding: FragmentBatteryBinding
    val batteryInfoViewModel: BatteryInfoViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBatteryBinding.inflate(layoutInflater, container, false)

        SmartAds.getInstance().loadBannerFragment(requireActivity(), BuildConfig.battery_fragment_banner,binding.welcomeNativecontainer,object :
            AdCallback(){
            override fun onAdFailedToLoad(i: LoadAdError?) {
                super.onAdFailedToLoad(i)
                binding.welcomeNativecontainer.visibility = View.GONE
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                binding.welcomeNativecontainer.visibility = View.VISIBLE
            }
        })



        getBatteryInfo()


        return binding.root
    }

    fun getBatteryInfo() {
       /* batteryInfoViewModel.remainingTime.observe(viewLifecycleOwner, Observer { remainingTime ->
            if (remainingTime != null) {
                val hours = remainingTime / (1000 * 60 * 60)
                val minutes = (remainingTime % (1000 * 60 * 60)) / (1000 * 60)
                Log.d("TAG", " getBatteryInfo: $remainingTime")

                val remainingTimeString = getString(R.string.battery_in_h_min, "$hours", "$minutes")
                binding.batteryRemainingId.text = remainingTimeString
            }

        })*/

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