package com.batterycharging.animation.chargingeffect.Activities

 import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
 import android.util.Log
 import android.view.View
 import androidx.recyclerview.widget.GridLayoutManager
 import com.batterycharging.animation.chargingeffect.Adapters.CreatedAnimationsAdapter
 import com.batterycharging.animation.chargingeffect.BuildConfig
 import com.batterycharging.animation.chargingeffect.Interfaces.NoDataCallBack
 import com.batterycharging.animation.chargingeffect.ModelClasses.CreatedWallpaperModel
 import com.batterycharging.animation.chargingeffect.databinding.ActivityCreatedAnimationsBinding
  import io.paperdb.Paper
 import org.smrtobjads.ads.SmartAds
 import org.smrtobjads.ads.ads.models.ApAdError
 import org.smrtobjads.ads.callbacks.AperoAdCallback


class CreatedAnimationsActivity : BaseActivity() , NoDataCallBack {
    lateinit var binding: ActivityCreatedAnimationsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreatedAnimationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SmartAds.getInstance().loadBanner(this@CreatedAnimationsActivity, BuildConfig.my_creation_banner,object :
            AperoAdCallback(){
            override fun onAdFailedToLoad(adError: ApAdError?) {
                super.onAdFailedToLoad(adError)
                binding.welcomeNativecontainer.visibility = View.GONE
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                binding.welcomeNativecontainer.visibility = View.VISIBLE
            }
        })

        retrieveListFromPaperDb(){
            if(it?.size!=0) {
                Log.d("TAG_size", "onCreate: ${it?.size}")
                val layoutManager = GridLayoutManager(this, 2)
                binding.creationsRV.layoutManager = layoutManager
                binding.creationsRV.adapter =  CreatedAnimationsAdapter(this@CreatedAnimationsActivity, it, this)
            }
            else{
                binding.creationsRV.visibility= View.GONE
                binding.noCreationId.visibility=View.VISIBLE
            }
        }


        binding.backBtnId.setOnClickListener {
            finish()
        }

    }

    private fun retrieveListFromPaperDb(callBack:(ArrayList<CreatedWallpaperModel>?)->Unit){
        val wallpaperList: ArrayList<CreatedWallpaperModel>?=
            Paper.book().read("wallpaperModels", ArrayList())
        callBack(wallpaperList)
    }

    override fun noDataFound(noDataFound: Boolean) {
        if(noDataFound){
            binding.creationsRV.visibility= View.GONE
            binding.noCreationId.visibility=View.VISIBLE
        }
    }


}