package com.batterycharging.animation.chargingeffect.Activities

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.batterycharging.animation.chargingeffect.Adapters.GuidePagerAdapter
import com.batterycharging.animation.chargingeffect.AdsClass
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.Fragments.FirstGuideFragment
import com.batterycharging.animation.chargingeffect.Fragments.SecondGuideFragment
import com.batterycharging.animation.chargingeffect.Fragments.ThirdGuideFragment
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivityGuideScreenBinding
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AperoAdCallback

class GuideScreenActivity : BaseActivity() {
    lateinit var binding: ActivityGuideScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        guideNativeAd()


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
                    System.exit(0);
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

    private fun guideNativeAd(){
        AdsClass.getAdApplication().getStorageCommon().exitNative.let { appNative->
            if (appNative == null || appNative.value == null && !AppPurchase.getInstance().isPurchased) {
                SmartAds.getInstance().loadNativeAdResultCallback(applicationContext,
                    BuildConfig.guide_screen_native, R.layout.custom_native_medium, object :
                        AperoAdCallback(){
                        override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                            super.onNativeAdLoaded(nativeAd)
                            SmartAds.getInstance().populateNativeAdView(this@GuideScreenActivity, nativeAd, binding.adViewContainer, binding.splashNativeAd.shimmerContainerNative)
                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            binding.adViewContainer.visibility = View.GONE
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            binding.adViewContainer.visibility = View.GONE
                        }

                    })
            }else{
                SmartAds.getInstance().populateNativeAdView(
                    this@GuideScreenActivity,
                    appNative.value,
                    binding.adViewContainer,
                    binding.splashNativeAd.shimmerContainerNative)
            }
        }

    }


}