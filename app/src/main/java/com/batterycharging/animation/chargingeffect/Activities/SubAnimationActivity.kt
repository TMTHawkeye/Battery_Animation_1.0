package com.batterycharging.animation.chargingeffect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.batterycharging.animation.chargingeffect.Adapters.SubAnimationAdapter
import com.batterycharging.animation.chargingeffect.HelperClasses.getAnimalAnimations
import com.batterycharging.animation.chargingeffect.HelperClasses.getBatteryAnimations
import com.batterycharging.animation.chargingeffect.HelperClasses.getCarsAnimations
import com.batterycharging.animation.chargingeffect.HelperClasses.getCharactersAnimations
import com.batterycharging.animation.chargingeffect.HelperClasses.getCicleAnimations
import com.batterycharging.animation.chargingeffect.HelperClasses.getMoodsAnimations
import com.batterycharging.animation.chargingeffect.R
import com.batterycharging.animation.chargingeffect.databinding.ActivitySubAnimationBinding

class SubAnimationActivity : BaseActivity() {
    lateinit var binding: ActivitySubAnimationBinding
    var listOfAnimations=ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySubAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnId.setOnClickListener {
            finish()
        }

    }

    private fun setUpAdapter(animationsList: ArrayList<Int>, intentFrom: String?) {
        val layoutManager = GridLayoutManager(this, 2)
        binding.animationRV.setLayoutManager(layoutManager)
        val adapter= SubAnimationAdapter(this@SubAnimationActivity,animationsList,intentFrom)
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
            val moodsList= getMoodsAnimations()
            callback(moodsList)
        }

        else if (intentFrom.equals(getString(R.string.characters))){
            val charactersList= getCharactersAnimations()
            callback(charactersList)
        }

        else if (intentFrom.equals(getString(R.string.animals))){
            val animalsList= getAnimalAnimations()
            callback(animalsList)
        }
        else if (intentFrom.equals(getString(R.string.cars))){
            val carsList= getCarsAnimations()
            callback(carsList)
        }

//        callback(listOfAnimations)
    }

    override fun onResume() {
        super.onResume()
        val intentFrom=intent.getStringExtra("intentFrom")
        binding.subAnimationTitleId.text=intentFrom
        populateList(intentFrom){
            setUpAdapter(it,intentFrom)
        }
    }

}