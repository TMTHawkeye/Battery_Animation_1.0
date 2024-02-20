package com.example.batteryanimation.HelperClasses

import androidx.annotation.RawRes
import com.example.batteryanimation.R


fun getBatteryAnimations() :ArrayList<Int>{
    var listOfAnimations=ArrayList<Int>()
    listOfAnimations.add(R.raw.battery_anim_1)
    listOfAnimations.add(R.raw.battery_anim_2)
    listOfAnimations.add(R.raw.battery_anim_3)
    listOfAnimations.add(R.raw.battery_anim_4)
//    listOfAnimations.add(R.raw.battery_anim_5)
    return listOfAnimations
}

fun getCicleAnimations() :ArrayList<Int>{
    var listOfAnimations=ArrayList<Int>()
    listOfAnimations.add(R.raw.circle_anim_1)
    listOfAnimations.add(R.raw.circle_anim_2)
    listOfAnimations.add(R.raw.circle_anim_3)
//    listOfAnimations.add(R.raw.circle_anim_4)
//    listOfAnimations.add(R.raw.circle_anim_5)
    listOfAnimations.add(R.raw.circle_anim_6)
    listOfAnimations.add(R.raw.circle_anim_7)
    listOfAnimations.add(R.raw.circle_anim_8)
    listOfAnimations.add(R.raw.circle_anim_9)
//    listOfAnimations.add(R.raw.circle_anim_10)
    return listOfAnimations
}

