package com.example.batteryanimation.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.batteryanimation.Activities.GuideScreenActivity
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.FragmentFirstGuideBinding
import com.example.batteryanimation.databinding.FragmentSecondGuideBinding


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