package com.example.batteryanimation.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.batteryanimation.Activities.WelcomeActivity
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.FragmentSecondGuideBinding
import com.example.batteryanimation.databinding.FragmentThirdGuideBinding


class ThirdGuideFragment : Fragment() {
    lateinit var binding: FragmentThirdGuideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentThirdGuideBinding.inflate(layoutInflater,container,false)


        binding?.letsGoCardId?.setOnClickListener {
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
        }
        return binding.root
    }

}