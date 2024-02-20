package com.example.batteryanimation.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.batteryanimation.Adapters.SubAnimationAdapter
import com.example.batteryanimation.HelperClasses.getBatteryAnimations
import com.example.batteryanimation.HelperClasses.getCicleAnimations
import com.example.batteryanimation.R
import com.example.batteryanimation.databinding.ActivitySubAnimationBinding

class SubAnimationActivity : AppCompatActivity() {
    lateinit var binding:ActivitySubAnimationBinding
    var listOfAnimations=ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySubAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentFrom=intent.getStringExtra("intentFrom")

        populateList(intentFrom){
            setUpAdapter(it)
        }
    }

    private fun setUpAdapter(animationsList: ArrayList<Int>) {
        val layoutManager = GridLayoutManager(this, 2)
        binding.animationRV.setLayoutManager(layoutManager)
        val adapter= SubAnimationAdapter(this@SubAnimationActivity,animationsList)
        binding.animationRV.adapter=adapter

    }
    private fun populateList(intentFrom:String?,callback:(ArrayList<Int>)->Unit) {

        if(intentFrom.equals(getString(R.string.title_battery))){
            val animList= getBatteryAnimations()
            callback(animList)
        }
        else if (intentFrom.equals(getString(R.string.circle))){
            val animList= getCicleAnimations()
            callback(animList)
        }
        else if (intentFrom.equals(getString(R.string.moods))){

        }

        else if (intentFrom.equals(getString(R.string.characters))){

        }

        else if (intentFrom.equals(getString(R.string.animals))){

        }
        else if (intentFrom.equals(getString(R.string.cars))){

        }

//        callback(listOfAnimations)
    }

}