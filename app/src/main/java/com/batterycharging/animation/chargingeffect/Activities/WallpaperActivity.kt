package com.batterycharging.animation.chargingeffect.Activities

import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.batterycharging.animation.chargingeffect.Adapters.WalpaperAdapter
import com.batterycharging.animation.chargingeffect.BuildConfig
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivityWallpaperBinding
import org.smrtobjads.ads.SmartAds
import org.smrtobjads.ads.ads.models.AdmobNative
import org.smrtobjads.ads.ads.models.ApAdError
import org.smrtobjads.ads.callbacks.AperoAdCallback
import java.io.IOException

class WallpaperActivity : BaseActivity() {
    lateinit var binding: ActivityWallpaperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wallpaperNativeAd()

        getListFromAssets{
            setUpAdapter(it)
        }

        binding.backBtnId.setOnClickListener {
            finish()
        }
    }

    private fun wallpaperNativeAd(){
        SmartAds.getInstance().loadNativeAdResultCallback(this@WallpaperActivity,
            BuildConfig.wallpaper_native, R.layout.custom_native_medium, object :
                AperoAdCallback(){
                override fun onNativeAdLoaded(nativeAd: AdmobNative) {
                    super.onNativeAdLoaded(nativeAd)
                    SmartAds.getInstance().populateNativeAdView(this@WallpaperActivity, nativeAd, binding.adViewContainer,binding.splashNativeAd.shimmerContainerNative)
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


    private fun setUpAdapter(wallpapersList: ArrayList<String>?) {
        val layoutManager = GridLayoutManager(this, 2)
        binding.wallpaperRV.setLayoutManager(layoutManager)
        val adapter= WalpaperAdapter(this@WallpaperActivity,wallpapersList)
        binding.wallpaperRV.adapter=adapter

    }

//    private fun populateList(callback:(ArrayList<Int>)->Unit) {
//        var animationsList = ArrayList<Int>()
//
//        animationsList.add(
//            R.drawable.battery_icon_nav,
//        )
//
//        animationsList.add(
//            R.drawable.cars_icon
//        )
//
//        callback(animationsList)
//    }

    fun getListFromAssets(callback: (ArrayList<String>?) -> Unit) {
        try {
            val assetManager: AssetManager = getAssets()
            val wallpapersList = assetManager.list("battery_wallpapers")
                ?.filter { it.endsWith(".png", ignoreCase = true) } // Filter only PNG files
                ?.toCollection(ArrayList())
            callback(wallpapersList)
        } catch (e: IOException) {
            e.printStackTrace()
            callback(null)
        }
    }


}