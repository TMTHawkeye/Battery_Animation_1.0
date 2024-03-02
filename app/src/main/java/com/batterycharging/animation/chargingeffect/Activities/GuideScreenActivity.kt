package com.batterycharging.animation.chargingeffect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.batterycharging.animation.chargingeffect.Adapters.GuidePagerAdapter
import com.batterycharging.animation.chargingeffect.Fragments.FirstGuideFragment
import com.batterycharging.animation.chargingeffect.Fragments.SecondGuideFragment
import com.batterycharging.animation.chargingeffect.Fragments.ThirdGuideFragment
import com.batterycharging.animation.chargingeffect.databinding.ActivityGuideScreenBinding

class GuideScreenActivity : BaseActivity() {
    lateinit var binding: ActivityGuideScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intentFrom = intent.getStringExtra("fromMain")

        val fragmentList = listOf(FirstGuideFragment(), SecondGuideFragment(), ThirdGuideFragment.newInstance(intentFrom?:""))
        val adapter = GuidePagerAdapter(supportFragmentManager, fragmentList)
        binding.viewPager.adapter = adapter

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (intentFrom.equals("FROM_MAIN")) {
                    finish()
                } else {
                    finishAffinity()
                }
            }
        })
    }


    fun navigateToNextFragment(currentItem: Int) {
        binding.viewPager.currentItem = currentItem
    }

    fun navigateToPreviousFragment() {
        binding.viewPager.currentItem = 0
    }

}