package com.batterycharging.animation.chargingeffect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.batterycharging.animation.chargingeffect.Activities.AnimationsActivity
import com.batterycharging.animation.chargingeffect.Activities.BaseActivity
import com.batterycharging.animation.chargingeffect.Activities.CreateNewAnimationActivity
import com.batterycharging.animation.chargingeffect.Activities.CreatedAnimationsActivity
import com.batterycharging.animation.chargingeffect.Activities.EnableActivity
import com.batterycharging.animation.chargingeffect.Activities.GuideScreenActivity
import com.batterycharging.animation.chargingeffect.Activities.WallpaperActivity
import com.batterycharging.animation.chargingeffect.AdsClass
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.ViewModels.BatteryInfoViewModel
import com.batterycharging.animation.chargingeffect.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AperoAdCallback


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    val batteryInfoViewModel: BatteryInfoViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        homeFragmentNativeAd()


        binding.batteryLottieIdMain.repeatCount = LottieDrawable.INFINITE
        binding.batteryLottieIdMain.playAnimation()
        getBatteryInfo()

        binding.cardBattery.setOnClickListener {
            (context as BaseActivity).showInterstitialAdByTimes("") {
                startActivity(Intent(requireContext(), AnimationsActivity::class.java))
            }
        }

        binding.cardWalpaper.setOnClickListener {
            (context as BaseActivity).showInterstitialAdByTimes("") {
                startActivity(Intent(requireContext(), WallpaperActivity::class.java))
            }
        }

        binding.cardCreateNew.setOnClickListener {
            (context as BaseActivity).showInterstitialAdByTimes("") {
                startActivity(Intent(requireContext(), CreateNewAnimationActivity::class.java))
            }
        }

        binding.cardCreation.setOnClickListener {
            (context as BaseActivity).showInterstitialAdByTimes("") {
                startActivity(Intent(requireContext(), CreatedAnimationsActivity::class.java))
            }
        }

        binding.enableAnimationCardId.setOnClickListener {
            (context as BaseActivity).showInterstitialAdByTimes("") {
                startActivity(Intent(requireContext(), EnableActivity::class.java))
            }
        }

        binding.guideInfoId.setOnClickListener {
            (context as BaseActivity).showInterstitialAdByTimes("") {
                startActivity(
                    Intent(
                        requireContext(),
                        GuideScreenActivity::class.java
                    ).putExtra("fromMain", "FROM_MAIN")
                )
            }
        }


        return binding.root
    }

    fun getBatteryInfo() {
        batteryInfoViewModel.batteryPercentage.observe(
            viewLifecycleOwner,
            Observer { batteryPercentage ->
                binding.batteryPercentageId.text = batteryPercentage.toString() + "%"
            })

        batteryInfoViewModel.isCharging.observe(viewLifecycleOwner, Observer { isCharging ->
            if (isCharging) {
                binding.chargingStatusId.text = requireContext().getString(R.string.connected)
            } else {
                binding.chargingStatusId.text = requireContext().getString(R.string.disconnect)

            }
        })

    }

    private fun homeFragmentNativeAd() {
        AdsClass.getAdApplication().getStorageCommon().homeNative.let { appNative ->
            if (appNative == null || appNative.value == null && !AppPurchase.getInstance().isPurchased) {
                SmartAds.getInstance().loadNativeAdResultCallback(requireContext(),
                    BuildConfig.home_fragment_native, R.layout.custom_native_medium, object :
                        AperoAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                            super.onNativeAdLoaded(nativeAd)
                            SmartAds.getInstance().populateNativeAdView(
                                requireContext(),
                                nativeAd,
                                binding.adViewContainer,
                                binding.splashNativeAd.shimmerContainerNative
                            )
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
            } else {
                SmartAds.getInstance().populateNativeAdView(
                    requireContext(),
                    appNative.value,
                    binding.adViewContainer,
                    binding.splashNativeAd.shimmerContainerNative
                )
            }
        }

    }


}