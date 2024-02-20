package com.example.batteryanimation.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.batteryanimation.Activities.AnimationsActivity
import com.example.batteryanimation.Activities.WallpaperActivity
import com.example.batteryanimation.R
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import com.example.batteryanimation.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {
    lateinit var binding:FragmentHomeBinding
    val batteryInfoViewModel:BatteryInfoViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(layoutInflater,container,false)
//        getBatteryInfo()

        binding.cardBattery.setOnClickListener {
            startActivity(Intent(requireContext(),AnimationsActivity::class.java))
        }

        binding.cardWalpaper.setOnClickListener {
            startActivity(Intent(requireContext(),WallpaperActivity::class.java))
        }

        binding.cardCreateNew.setOnClickListener {
            startActivity(Intent(requireContext(),WallpaperActivity::class.java))
        }

        binding.cardCreation.setOnClickListener {
            startActivity(Intent(requireContext(),WallpaperActivity::class.java))
        }


        return binding.root
    }

    fun getBatteryInfo(){
        batteryInfoViewModel.batteryPercentage.observe(viewLifecycleOwner, Observer { batteryPercentage ->
            binding.batteryPercentageId.text=batteryPercentage.toString()+"%"
        })

        batteryInfoViewModel.isCharging.observe(viewLifecycleOwner, Observer { isCharging ->
            if(isCharging){
                binding.chargingStatusId.text=requireContext().getString(R.string.connected)
            }
            else{
                binding.chargingStatusId.text=requireContext().getString(R.string.disconnect)

            }
        })
    }



}