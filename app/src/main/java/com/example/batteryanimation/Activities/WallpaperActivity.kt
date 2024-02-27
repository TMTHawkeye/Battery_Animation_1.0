package com.example.batteryanimation.Activities

import android.content.res.AssetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.batteryanimation.Adapters.WalpaperAdapter
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityWallpaperBinding
import java.io.IOException

class WallpaperActivity : AppCompatActivity() {
    lateinit var binding:ActivityWallpaperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getListFromAssets{
            setUpAdapter(it)
        }

        binding.backBtnId.setOnClickListener {
            finish()
        }
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