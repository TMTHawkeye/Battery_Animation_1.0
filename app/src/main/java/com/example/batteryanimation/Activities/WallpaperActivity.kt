package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.batteryanimation.Adapters.AnimationAdapter
import com.example.batteryanimation.Adapters.SubAnimationAdapter
import com.example.batteryanimation.ModelClasses.CategoryModel
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityWallpaperBinding

class WallpaperActivity : AppCompatActivity() {
    lateinit var binding:ActivityWallpaperBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        populateList{
            setUpAdapter(it)
        }
    }

    private fun setUpAdapter(wallpapersList: ArrayList<Int>) {
        val layoutManager = GridLayoutManager(this, 2)
        binding.wallpaperRV.setLayoutManager(layoutManager)
        val adapter= SubAnimationAdapter(this@WallpaperActivity,wallpapersList)
        binding.wallpaperRV.adapter=adapter

    }

    private fun populateList(callback:(ArrayList<Int>)->Unit) {
        var animationsList = ArrayList<Int>()

        animationsList.add(
            R.drawable.battery_icon_nav,
        )

        animationsList.add(
            R.drawable.cars_icon
        )

        callback(animationsList)
    }
}