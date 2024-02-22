package com.example.batteryanimation.Repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.ModelClasses.AnimationSwitchStates
import com.example.batteryanimation.ModelClasses.SwitchStates
import com.example.batteryanimation.R
import com.google.gson.Gson

class AnimationRepository(val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.SAVED_ANIMATION, Context.MODE_PRIVATE)


    private val _savedAnimation = MutableLiveData<Int>()
    val savedAnimation: LiveData<Int>
        get() = _savedAnimation
    init {
        _savedAnimation.value = sharedPreferences.getInt(Constants.SAVED_ANIMATION_KEY, R.raw.battery_anim_2)
    }

    fun updateAnimation(savedAnimationValue: Int) {
        _savedAnimation.value = savedAnimationValue
        sharedPreferences.edit().putInt(Constants.SAVED_ANIMATION_KEY, savedAnimationValue).apply()
    }


//    private val sharedPreferencesAnimation: SharedPreferences =
//        context.getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)
//
//    private val _isSwitchOn = MutableLiveData<Boolean>()
//    val isSwitchOn: LiveData<Boolean>
//        get() = _isSwitchOn
//
//    init {
//        _isSwitchOn.value = sharedPreferencesAnimation.getBoolean(Constants.SWITCH_STATE_ANIMATION_KEY, true)
//    }
//
//    fun updateSwitchState(isSwitchOn: Boolean) {
//        _isSwitchOn.value = isSwitchOn
//        sharedPreferencesAnimation.edit().putBoolean(Constants.SWITCH_STATE_ANIMATION_KEY, isSwitchOn).apply()
//    }

    private val sharedPreferencesAnimation: SharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)

    private val _switchState = MutableLiveData<AnimationSwitchStates>()
    val switchState: LiveData<AnimationSwitchStates>
        get() = _switchState

    init {
        // Load switch state from SharedPreferences
        val savedSwitchStateString = sharedPreferencesAnimation.getString(Constants.SWITCH_STATE_ANIMATION_KEY, null)
        val defaultSwitchState = AnimationSwitchStates(
            isactiveAnimationSwitchOn = true,
            isbatteryPercentageSwitchOn = false,
            isdouble_tap_closeSwitchOn = false,
//            isChargerDisconnectSwitchOn = false
        )
        _switchState.value = savedSwitchStateString?.let { deserializeSwitchState(it) } ?: defaultSwitchState
    }

    fun updateSwitchState(newSwitchState: AnimationSwitchStates) {
        _switchState.value = newSwitchState

        // Serialize switch state to string and save in SharedPreferences
        val switchStateString = serializeSwitchState(newSwitchState)
        sharedPreferencesAnimation.edit().putString(Constants.SWITCH_STATE_ANIMATION_KEY, switchStateString).apply()
    }

    // Helper function to serialize switch state to string
    private fun serializeSwitchState(switchState: AnimationSwitchStates): String {
        return Gson().toJson(switchState)
    }

    // Helper function to deserialize switch state from string
    private fun deserializeSwitchState(switchStateString: String): AnimationSwitchStates {
        return Gson().fromJson(switchStateString, AnimationSwitchStates::class.java)
    }
}