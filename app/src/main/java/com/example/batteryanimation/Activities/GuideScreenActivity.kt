package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.batteryanimation.Adapters.GuidePagerAdapter
import com.example.batteryanimation.Fragments.FirstGuideFragment
import com.example.batteryanimation.Fragments.SecondGuideFragment
import com.example.batteryanimation.Fragments.ThirdGuideFragment
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityGuideScreenBinding

class GuideScreenActivity : AppCompatActivity() {
    lateinit var binding:ActivityGuideScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGuideScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentList = listOf(FirstGuideFragment(), SecondGuideFragment(),ThirdGuideFragment())
        val adapter = GuidePagerAdapter(supportFragmentManager, fragmentList)
        binding.viewPager.adapter = adapter


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }

        })

    }


    fun navigateToNextFragment(currentItem:Int) {
        binding.viewPager.currentItem = currentItem
    }

    fun navigateToPreviousFragment() {
        binding.viewPager.currentItem = 0
    }

}