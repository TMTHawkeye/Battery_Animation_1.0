package com.batterycharging.animation.chargingeffect.Repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.batterycharging.animation.chargingeffect.HelperClasses.Constants
import com.batterycharging.animation.chargingeffect.ModelClasses.AnimationSwitchStates
import com.google.gson.Gson

class AnimationRepository(val context: Context) {

    private val sharedPreferencesAnimation: SharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)

    private val _switchState = MutableLiveData<AnimationSwitchStates>()
    val switchState: LiveData<AnimationSwitchStates>
        get() = _switchState
    init {
        val savedSwitchStateString = sharedPreferencesAnimation.getString(Constants.SWITCH_STATE_ANIMATION_KEY, null)
        val defaultSwitchState = AnimationSwitchStates(
            isactiveAnimationSwitchOn = false,
            isbatteryPercentageSwitchOn = false,
            isdouble_tap_closeSwitchOn = false,
            animationDuration = 3000
        )
        _switchState.value = savedSwitchStateString?.let { deserializeSwitchState(it) } ?: defaultSwitchState
    }
    fun updateSwitchState(newSwitchState: AnimationSwitchStates) {
        _switchState.value = newSwitchState

        val switchStateString = serializeSwitchState(newSwitchState)
        sharedPreferencesAnimation.edit().putString(Constants.SWITCH_STATE_ANIMATION_KEY, switchStateString).apply()
    }
    fun updateAnimationDuration(duration: Int) {
        val currentState = switchState.value ?: return
        val newState = currentState.copy(animationDuration = duration)
        updateSwitchState(newState)
    }
    private fun serializeSwitchState(switchState: AnimationSwitchStates): String {
        return Gson().toJson(switchState)
    }
    private fun deserializeSwitchState(switchStateString: String): AnimationSwitchStates {
        return Gson().fromJson(switchStateString, AnimationSwitchStates::class.java)
    }
}