package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivitySetWallpaperBinding

class SetWallpaperActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetWallpaperBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySetWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}