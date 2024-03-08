package com.batterycharging.animation.chargingeffect.Activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.batterycharging.animation.chargingeffect.Adapters.LanguageAdapter
import com.batterycharging.animation.chargingeffect.AdsClass
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.HelperClasses.isFirstTimeLaunch
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.Interfaces.SelectedLanguageCallback
import com.batterycharging.animation.chargingeffect.ModelClasses.Language
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivityLanguageBinding
import com.zeugmasolutions.localehelper.LocaleHelper
import io.paperdb.Paper
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.billings.AppPurchase
import org.smrtobjads.ads.callbacks.AperoAdCallback
import java.util.Locale

class LanguageActivity : BaseActivity(), SelectedLanguageCallback {
    lateinit var binding: ActivityLanguageBinding
    var selectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageNativeAd()
        loadSplashNative()
        loadGuideNative()



        val languagesList = getLanguagesList()
        val savedPosition = Paper.book().read<Int?>("LANG_POS", selectedPosition)
        selectedPosition=savedPosition?:0
        val adapter = LanguageAdapter(this@LanguageActivity, languagesList, savedPosition, this)
        binding.languagesRV.layoutManager = LinearLayoutManager(this)
        binding.languagesRV.adapter = adapter

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                prEvents("back_btn","back Button from language activity is pressed!")

                if (isFirstTimeLaunch()) {
//                    finishAffinity()
                    finishAffinity()
                    System.exit(0);
                } else {
                    finish()
                }
            }
        })

        binding.backBtnId.setOnClickListener {
            prEvents("backBtnId","Back btn switch from LanguageActivity is pressed!")

            if (isFirstTimeLaunch()) {
                finishAffinity()
                System.exit(0);

            } else {
                finish()
            }
        }

        binding.doneBtn.setOnClickListener {
            prEvents("doneBtn","Done btn switch from LanguageActivity is pressed!")
            Log.d("TAG_savedPosition", "savedPosition: $selectedPosition")
            if (adapter.savedPosition != -1) {
                Paper.book().write<Int?>("LANG_POS", selectedPosition)

                val locale = Locale(adapter.languageCode)
                Locale.setDefault(locale)
                val configuration: Configuration = resources.configuration
                configuration.locale = locale
                configuration.setLayoutDirection(locale)
                localeDelegate.setLocale(this, locale)
                LocaleHelper.setLocale(this, locale)

                BaseActivity().updateLocale(this as LanguageActivity, locale)

                val intent = if (isFirstTimeLaunch()) {
                    Intent(this, GuideScreenActivity::class.java)
                } else {
                    Intent(this, WelcomeActivity::class.java)

                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                Paper.book().write("LangPref", false)

                startActivity(intent)
                finish()


            }
        }
    }

    fun getLanguagesList(): ArrayList<Language> {
        var languageList = ArrayList<Language>()
        languageList.add(Language("English", getDrawable(R.drawable.english_img), "en"))
        languageList.add(Language("Spanish", getDrawable(R.drawable.spanish_img), "es"))
        languageList.add(Language("French", getDrawable(R.drawable.french_img), "fr"))
        languageList.add(Language("German", getDrawable(R.drawable.german_img), "de"))
        languageList.add(Language("Urdu", getDrawable(R.drawable.urdu_img), "ur"))
        languageList.add(Language("Italian", getDrawable(R.drawable.italian_img), "it"))
//        languageList.add(Language("Arabic",getDrawable(R.drawable.saudi_arabia_flag),"ar"))
        languageList.add(Language("Arabic", getDrawable(R.drawable.arabic_img), "ar"))
//        languageList.add(Language("Korean",getDrawable(R.drawable.korean_flag),"ar"))
//        languageList.add(Language("Urdu",getDrawable(R.drawable.urdu_flag),"ar"))
//        languageList.add(Language("Portugues",getDrawable(R.drawable.portugal_flag),"ar"))
        return languageList
    }

    override fun languageSelected(position: Int) {
        this.selectedPosition = position
    }

    private fun languageNativeAd(){
        AdsClass.getAdApplication().getStorageCommon().nativeAdsLanguage.let { appNative->
            if (appNative == null || appNative.value == null && !AppPurchase.getInstance().isPurchased) {
                SmartAds.getInstance().loadNativeAdResultCallback(applicationContext,
                    BuildConfig.language_native, R.layout.native_ad_template, object :
                        AperoAdCallback(){
                        override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                            super.onNativeAdLoaded(nativeAd)
                            SmartAds.getInstance().populateNativeAdView(this@LanguageActivity, nativeAd, binding.adViewContainer, binding.splashNativeAd.shimmerContainerNative)
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
                    this@LanguageActivity,
                    appNative.value,
                    binding.adViewContainer,
                    binding.splashNativeAd.shimmerContainerNative)
            }
        }

    }

    fun loadSplashNative(nativeAdId: String = BuildConfig.welcome_native) {
        if (AdsClass.getAdApplication().getStorageCommon()?.welcomeNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                R.layout.custom_native_medium,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        AdsClass.getAdApplication()?.getStorageCommon()?.welcomeNative?.postValue(null)
                    }
                }
            )
        }
    }

    fun loadGuideNative(nativeAdId: String = BuildConfig.guide_screen_native) {
        if (AdsClass.getAdApplication().getStorageCommon()?.exitNative?.getValue() == null
            && !AppPurchase.getInstance().isPurchased) {

            SmartAds.getInstance().loadNativeAdResultCallback(
                applicationContext,
                nativeAdId,
                R.layout.custom_native_medium,
                object : AperoAdCallback() {
                    override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                        super.onNativeAdLoaded(nativeAd)
                        AdsClass.getAdApplication()?.getStorageCommon()?.exitNative?.postValue(nativeAd)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        AdsClass.getAdApplication()?.getStorageCommon()?.exitNative?.postValue(null)
                    }
                }
            )
        }
    }



}