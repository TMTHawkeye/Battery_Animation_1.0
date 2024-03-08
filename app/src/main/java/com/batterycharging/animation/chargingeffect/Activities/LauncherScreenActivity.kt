package com.batterycharging.animation.chargingeffect.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieDrawable
import com.batterycharging.animation.chargingeffect.AdsClass
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.HelperClasses.PreloadAdsUtils
import com.batterycharging.animation.chargingeffect.HelperClasses.isFirstTimeLaunch
import com.batterycharging.animation.chargingeffect.HelperClasses.isInternetAvailable
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivityLauncherScreenBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.smrtobjads.ads.AdsConsentManager
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.UMPResultListener
import org.smrtobjads.ads.ads.AppOpenManager
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.ads.models.ApInterstitialAd
import org.smrtobjads.ads.adsutils.DataStoreUtils
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AdCallback
import org.smrtobjads.ads.callbacks.AperoAdCallback
import kotlin.coroutines.CoroutineContext

class LauncherScreenActivity : BaseActivity(), CoroutineScope, UMPResultListener {
    lateinit var binding: ActivityLauncherScreenBinding
    private var coroutineJob: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val CONSENT_PREFERENCE_KEY = "user_consent"
    var enableUmp = true

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + coroutineJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        launch {
            binding.loadingLottieId.repeatCount = LottieDrawable.INFINITE
            binding.loadingLottieId.playAnimation()
        }

//        coroutineJob = launch {
//            delay(5000)
//            navigateToNext()
//        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
                System.exit(0);
                coroutineJob.cancel()
            }
        })


        if (enableUmp) {
            val adsConsentManager = AdsConsentManager(this)
            adsConsentManager.requestUMP({
                Log.d("TAG_response", "onCreate: $it")
                runOnUiThread {
                    PreloadAdsUtils.getInstance().loadIntersAlternate(
                        this,
                        BuildConfig.welcome_interstitial,
                        BuildConfig.home_interstitial,
                        2
                    )
                    if (DataStoreUtils.getLanguageSelected(this, false)) {
                        loadLanguageNative()
                    }
                    loadSplashNative()

                    handleFetchedRemoteConfig()
//                    loadAdsSplash()
                    loadWelcomeInter()
                }
            }, false)
        } else {
            PreloadAdsUtils.getInstance().loadIntersAlternate(
                this,
                BuildConfig.welcome_interstitial,
                BuildConfig.home_interstitial,
                2)

            if ( DataStoreUtils.getLanguageSelected(this, false)){
                loadLanguageNative()
            }
            handleFetchedRemoteConfig()
//            loadAdsSplash()
            loadSplashNative()
            loadWelcomeInter()
        }

    }

    private fun handleFetchedRemoteConfig() {
        if (isInternetAvailable(this@LauncherScreenActivity)) {
            loadAdsSplash()
        }else{
            Handler().postDelayed({
                navigateToNext()
            },3000)
        }
//        AppPurchase.getInstance().setBillingListener({ integer: Int -> loadAdsSplash() }, 1000)
    }

    private val TIMEOUT_SPLASH: Long = 30000
    private val TIME_DELAY_SPLASH: Long = 7000

    //    private val typeAdsSplash = "inter"
    private val typeAdsSplash = "app_open_start"
    private fun loadAdsSplash() {
        if (AppPurchase.getInstance().isPurchased(this)) {
            AdsClass.getAdApplication().isAdCloseSplash?.postValue(true)
            navigateToNext()
        } else {
            AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(false)

//            if (typeAdsSplash == "app_open_start"){
            AppOpenManager.getInstance().loadOpenAppAdSplash(
                this,
                TIMEOUT_SPLASH,
                TIME_DELAY_SPLASH,
                false,
                object : AdCallback() {
                    override fun onAdSplashReady() {
                        super.onAdSplashReady()
                        Log.d("TAG_appOpen", "onAdSplashReady: splash ready app open")

                        if (isDestroyed || isFinishing) return
                        showAdsOpenAppSplash()
                    }

                    override fun onNextAction() {
                        super.onNextAction()
                        Log.d("TAG_appOpen", "onNextAction: nextaction app open")

                        if (isDestroyed || isFinishing) return
                        AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                        navigateToNext()
                    }

                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        Log.d("TAG_appOpen", "onAdFailedToLoad: failed_to load app open")
                        if (isDestroyed || isFinishing) return
                        AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                        navigateToNext()
                    }
                }
            )
//            }else{
//                SmartAds.getInstance().loadSplashInterstitialAds(
//                    this,
//                    BuildConfig.splash_intersitial_id,
//                    TIMEOUT_SPLASH,
//                    TIME_DELAY_SPLASH,
//                    false,
//                    object : AperoAdCallback() {
//                        override fun onAdFailedToLoad(adError: ApAdError?) {
//                            super.onAdFailedToLoad(adError)
//                            if (isDestroyed || isFinishing) return
//                            AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
//                        }
//
//                        override fun onNextAction() {
//                            super.onNextAction()
//                            if (isDestroyed || isFinishing) return
//                            AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
//                            LoadTheNextActivity()
//                        }
//
//                        override fun onAdSplashReady() {
//                            super.onAdSplashReady()
//                            if (isDestroyed || isFinishing) return
//                            showInterSplash()
//                        }
//                    }
//                )
//            }
        }
    }
    private fun showAdsOpenAppSplash() {
        AppOpenManager.getInstance().showAppOpenSplash(
            this,
            object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    if (isDestroyed || isFinishing) return
                    navigateToNext()
                }

                override fun onAdFailedToShow(adError: AdError?) {
                    super.onAdFailedToShow(adError)
                    if (isDestroyed || isFinishing) return
                    AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                    navigateToNext()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    if (isDestroyed || isFinishing) return
                    AdsClass.getAdApplication()?.isAdCloseSplash?.postValue(true)
                    navigateToNext()
                }
            }
        )
    }

    private fun navigateToNext() {
        prEvents("firstTimeLaunchCheck", "Guide Screen or Welcome Screen activity is displayed!")

        val intent = if (isFirstTimeLaunch()) {
            Intent(this@LauncherScreenActivity, LanguageActivity::class.java)
        } else {
            Intent(this@LauncherScreenActivity, WelcomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    fun loadLanguageNative(nativeAdId: String = BuildConfig.language_native) {
        if (AdsClass.getAdApplication().getStorageCommon()?.nativeAdsLanguage?.getValue() == null
            && !AppPurchase.getInstance().isPurchased
        ) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                R.layout.native_ad_template,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        AdsClass.getAdApplication()
                            ?.getStorageCommon()?.nativeAdsLanguage?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        AdsClass.getAdApplication()
                            ?.getStorageCommon()?.nativeAdsLanguage?.postValue(null)
                    }
                }
            )
        }
    }


    fun loadSplashNative(nativeAdId: String = BuildConfig.welcome_native) {
        if (AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased
        ) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                R.layout.custom_native_medium,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative?.postValue(
                            nativeAd
                        )
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative?.postValue(
                            null
                        )
                    }
                }
            )
        }
    }

    private fun loadWelcomeInter() {
        SmartAds.getInstance()
            .getInterstitialAds(this, BuildConfig.welcome_interstitial, object : AperoAdCallback() {
                override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                    super.onInterstitialLoad(interstitialAd)
                    AdsClass.getAdApplication()?.getStorageCommon()?.welcomeInterstitialAd =
                        interstitialAd
                }
            })
    }

    override fun onCheckUMPSuccess(consentResult: Boolean) {
        saveConsentState(consentResult)

    }

    private fun saveConsentState(consentGiven: Boolean) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putBoolean(CONSENT_PREFERENCE_KEY, consentGiven)
        editor.apply()
    }
}