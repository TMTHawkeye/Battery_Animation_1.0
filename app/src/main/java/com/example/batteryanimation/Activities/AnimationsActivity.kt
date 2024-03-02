package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.batteryanimation.Adapters.AnimationAdapter
import com.example.batteryanimation.HelperClasses.prEvents
import com.example.batteryanimation.ModelClasses.CategoryModel
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivityAnimationsBinding

class AnimationsActivity : AppCompatActivity() {
    lateinit var binding:ActivityAnimationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAnimationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnId.setOnClickListener {
            prEvents("backBtnId","Back Button from Animation Activity is pressed!")
            finish()
        }
    }

    private fun setUpAdapter(animationsList: ArrayList<CategoryModel>) {
        val layoutManager = GridLayoutManager(this, 2)
        binding.animationRV.setLayoutManager(layoutManager)
        val adapter=AnimationAdapter(this@AnimationsActivity,animationsList)
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