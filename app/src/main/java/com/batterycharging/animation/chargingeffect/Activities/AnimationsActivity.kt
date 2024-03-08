package com.batterycharging.animation.chargingeffect.Activities

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.batterycharging.animation.chargingeffect.Adapters.AnimationAdapter
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.HelperClasses.prEvents
import com.batterycharging.animation.chargingeffect.ModelClasses.CategoryModel
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivityAnimationsBinding
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.callbacks.AperoAdCallback

class AnimationsActivity : BaseActivity() {
    lateinit var binding: ActivityAnimationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAnimationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animationNativeAd()

        binding.backBtnId.setOnClickListener {
            prEvents("backBtnId","Back Button from Animation Activity is pressed!")
            finish()
        }
    }

    private fun animationNativeAd(){
        SmartAds.getInstance().loadNativeAdResultCallback(this@AnimationsActivity,
            BuildConfig.wallpaper_native, R.layout.custom_native_medium, object :
                AperoAdCallback(){
                override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                    super.onNativeAdLoaded(nativeAd)
                    SmartAds.getInstance().populateNativeAdView(this@AnimationsActivity, nativeAd, binding.adViewContainer,binding.splashNativeAd.shimmerContainerNative)
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                    binding.adViewContainer.visibility = View.GONE
                }

                override fun onAdFailedToShow(adError: ApAdError?) {
                    super.onAdFailedToShow(adError)
                    binding.adViewContainer.visibility = View.GONE
                }

                override fun onAdImpression() {
                    super.onAdImpression()

                }
            })
    }


    private fun setUpAdapter(animationsList: ArrayList<CategoryModel>) {
        val layoutManager = GridLayoutManager(this, 2)
        binding.animationRV.setLayoutManager(layoutManager)
        val adapter= AnimationAdapter(this@AnimationsActivity,animationsList)
        binding.animationRV.adapter=adapter

    }

    private fun populateList(callback:(ArrayList<CategoryModel>)->Unit) {
        var animationsList = ArrayList<CategoryModel>()

        animationsList.add(
            CategoryModel(
                R.drawable.battery_icon,
                getString(R.string.title_battery)

            )
        )
        animationsList.add(
            CategoryModel(
                R.drawable.circle_icon,
                getString(R.string.circle)
            )
        )
        animationsList.add(
            CategoryModel(
                R.drawable.moods_icon,
                getString(R.string.moods)
            )
        )
        animationsList.add(
            CategoryModel(
                R.drawable.character_icon,
                getString(R.string.characters)
            )
        )
        animationsList.add(
            CategoryModel(
                R.drawable.animal_img,
                getString(R.string.animals)
            )
        )
        animationsList.add(
            CategoryModel(
                R.drawable.cars_icon,
                getString(R.string.cars)
            )
        )
        
        callback(animationsList)
    }

    override fun onResume() {
        super.onResume()
        populateList{
            setUpAdapter(it)
        }
    }
}