package com.batterycharging.animation.chargingeffect.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieDrawable
import com.batterycharging.animation.chargingeffect.AdsClass
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.HelperClasses.isInternetAvailable
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.ads.models.ApInterstitialAd
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AperoAdCallback
import kotlin.coroutines.CoroutineContext

class WelcomeActivity : BaseActivity(), CoroutineScope {
    var delayDuration = 5000L
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var welcomeInterstitialAd: ApInterstitialAd? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + coroutineJob

    private var coroutineJob: Job = Job()
    lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isInternetAvailable(this@WelcomeActivity)) {
            binding.frameLayout.visibility = View.GONE
            binding.welcomeLottieId.visibility = View.VISIBLE
            launch {
                binding.welcomeLottieId.repeatCount = LottieDrawable.INFINITE
                binding.welcomeLottieId.playAnimation()
            }
            delayDuration = 3000L
        } else {
            binding.frameLayout.visibility = View.VISIBLE
            binding.welcomeLottieId.visibility = View.VISIBLE
            launch {
                binding.welcomeLottieId.repeatCount = LottieDrawable.INFINITE
                binding.welcomeLottieId.playAnimation()
            }
            delayDuration = 6000L

//            delayDuration = splashViewModel.repo.welcomeNative.welcomeDelay

//            if (splashViewModel.repo.homeNative.show || splashViewModel.repo.homeNative.isPreloading) {
            loadHomeNative()
//            }

//            if (splashViewModel.repo.welcomeNative.show || splashViewModel.repo.welcomeNative.isPreloading ) {
            welcomeNativeAd()
//            }else {
//                delayDuration = 4000L
//                binding.frameLayout.visibility = View.GONE
//                binding.splashIconId.visibility = View.VISIBLE
//            }
        }

        coroutineScope.launch {
            delay(delayDuration)
            binding.progressSplashId.visibility = View.GONE
            binding.letsGoCardId.visibility = View.VISIBLE
        }


        welcomeInterstitialAd =
            AdsClass.getAdApplication().getStorageCommon()?.welcomeInterstitialAd
        if (welcomeInterstitialAd == null) {
            Log.d("TAG_inter", "interstitial value : null")
            loadWelcomeInter()
        }

        binding.letsGoCardId.setOnClickListener {
            showInterstitial()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
                System.exit(0);

            }

        })



    }

    fun showInterstitial() {
        welcomeInterstitialAd.let {
            if (it == null) {
                navigateToNext()
            } else {
                if (it.isReady) {
                    SmartAds.getInstance()
                        .forceShowInterstitial(this, it, object : AperoAdCallback() {
                            override fun onNextAction() {
                                Log.i("ad", "onAdClosed: start content and finish main")
                                navigateToNext()
                            }

                            override fun onAdFailedToShow(adError: ApAdError?) {
                                super.onAdFailedToShow(adError)
                                Log.i("ad", "onAdFailedToShow:" + adError!!.message)
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                            }

                        }, false)
                } else {
                    navigateToNext()
                    /*if (it.isLoadFail) {
                            Log.d("TAG_welcomeInter", "isLoadFail: ")
                            navigateToNext()
                        } else if (it.isNotReady) {
                            Log.d("TAG_welcomeInter", "isNotReady: ")
                            navigateToNext()
                        } else if (it.isLoading) {
                            Log.d("TAG_welcomeInter", "isLoading: ")

                        } else {

                        }*/
//                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
            }
        }
    }

    private fun navigateToNext() {
        AdsClass.getAdApplication().getStorageCommon().welcomeInterstitialAd = null
        startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))


    }


    private fun loadWelcomeInter() {

        SmartAds.getInstance()
            .getInterstitialAds(this, BuildConfig.welcome_interstitial, object : AperoAdCallback() {
                override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                    super.onInterstitialLoad(interstitialAd)
                    welcomeInterstitialAd = interstitialAd
                    AdsClass.getAdApplication().getStorageCommon().welcomeInterstitialAd =
                        interstitialAd
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                }
            })
    }

    fun loadHomeNative(nativeAdId: String = BuildConfig.home_fragment_native) {
        if (AdsClass.getAdApplication().getStorageCommon()?.homeNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased
        ) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                R.layout.custom_native_medium,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        AdsClass.getAdApplication()?.getStorageCommon()?.homeNative?.postValue(
                            nativeAd
                        )
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        AdsClass.getAdApplication()?.getStorageCommon()?.homeNative?.postValue(null)
                    }
                }
            )
        }
    }

    fun welcomeNativeAd() {
        AdsClass.getAdApplication().getStorageCommon()?.welcomeNative.let { appNative ->
            if (appNative == null || appNative.value == null && !AppPurchase.getInstance().isPurchased) {
                SmartAds.getInstance().loadNativeAdResultCallback(this@WelcomeActivity,
                    BuildConfig.welcome_native,
                    R.layout.custom_native_medium,
                    object :
                        AperoAdCallback() {
                        override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                            super.onNativeAdLoaded(nativeAd)
                            SmartAds.getInstance().populateNativeAdView(
                                this@WelcomeActivity,
                                nativeAd,
                                binding.frameLayout,
                                binding.splashNativeAd.shimmerContainerNative
                            )
//                            binding.welcomeLottieId.visibility = View.INVISIBLE

                        }

                        override fun onAdFailedToLoad(adError: ApAdError?) {
                            super.onAdFailedToLoad(adError)
                            binding.frameLayout.visibility = View.GONE
                            binding.welcomeLottieId.visibility = View.VISIBLE
                        }

                        override fun onAdFailedToShow(adError: ApAdError?) {
                            super.onAdFailedToShow(adError)
                            binding.frameLayout.visibility = View.GONE
                            binding.welcomeLottieId.visibility = View.VISIBLE
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()

                        }
                    })
            } else {
//                binding.welcomeLottieId.visibility = View.INVISIBLE

                SmartAds.getInstance().populateNativeAdView(
                    this@WelcomeActivity,
                    appNative.value,
                    binding.frameLayout,
                    binding.splashNativeAd.shimmerContainerNative
                )
            }
        }

    }


}