package com.example.batteryanimation.ViewModels

import androidx.lifecycle.ViewModel
import com.example.batteryanimation.Repositories.AnimationRepository

class AnimationViewModel(val animationRepository: AnimationRepository):ViewModel() {

    val savedAnimation=animationRepository.savedAnimation

    fun updateAnimation(savedAnimation:Int){
        animationRepository.updateAnimation(savedAnimation)
    }


    val isSwitchOn=animationRepository.isSwitchOn

    fun updateSwitchState(isSwitchOn:Boolean){
        animationRepository.updateSwitchState(isSwitchOn)
    }
}