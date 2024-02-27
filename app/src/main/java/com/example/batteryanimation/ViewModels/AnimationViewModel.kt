package com.example.batteryanimation.ViewModels

import androidx.lifecycle.ViewModel
import com.example.batteryanimation.Repositories.AnimationRepository

class AnimationViewModel(val animationRepository: AnimationRepository):ViewModel() {

    val savedAnimation=animationRepository.savedAnimation

    fun updateAnimation(savedAnimation:Int){
        animationRepository.updateAnimation(savedAnimation)
    }


//    val isSwitchOn=animationRepository.isSwitchOn
//
//    fun updateSwitchState(isSwitchOn:Boolean){
//        animationRepository.updateSwitchState(isSwitchOn)
//    }

    val switchState = animationRepository.switchState

    fun updateSwitchState(switchId: String, isChecked: Boolean) {
        val currentState = switchState.value ?: return
        val newState = when (switchId) {
            "activeAnimation" -> currentState.copy(isactiveAnimationSwitchOn = isChecked)
            "batteryPercentage" -> currentState.copy(isbatteryPercentageSwitchOn = isChecked)
            "double_tap_close" -> currentState.copy(isdouble_tap_closeSwitchOn = isChecked)
//            "animationDuration" -> currentState.copy(isChargerDisconnectSwitchOn = isChecked)
            else -> currentState
        }
//        switchState.value = newState
        animationRepository.updateSwitchState(newState)
    }

    fun updateAnimationDuration(duration: Int) {
        animationRepository.updateAnimationDuration(duration)
    }
}