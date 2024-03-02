package com.batterycharging.animation.chargingeffect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.batterycharging.animation.chargingeffect.Activities.GuideScreenActivity
import com.batterycharging.animation.chargingeffect.databinding.FragmentSecondGuideBinding


class SecondGuideFragment : Fragment() {

    lateinit var binding: FragmentSecondGuideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSecondGuideBinding.inflate(layoutInflater,container,false)

        binding?.continueCardId?.setOnClickListener {
            (activity as GuideScreenActivity).navigateToNextFragment(2)
        }

        return binding.root
    }

}