package com.batterycharging.animation.chargingeffect.ViewModels

import androidx.lifecycle.ViewModel
import com.batterycharging.animation.chargingeffect.Repositories.AnimationRepository

class AnimationViewModel(val animationRepository: AnimationRepository):ViewModel() {

    val switchState = animationRepository.switchState

    fun updateSwitchState(switchId: String, isChecked: Boolean) {
        val currentState = switchState.value ?: return
        val newState = when (switchId) {
            "activeAnimation" -> currentState.copy(isactiveAnimationSwitchOn = isChecked)
            "batteryPercentage" -> currentState.copy(isbatteryPercentageSwitchOn = isChecked)
            "double_tap_close" -> currentState.copy(isdouble_tap_closeSwitchOn = isChecked)
            else -> currentState
        }
        animationRepository.updateSwitchState(newState)
    }

    fun updateAnimationDuration(duration: Int) {
        animationRepository.updateAnimationDuration(duration)
    }
}