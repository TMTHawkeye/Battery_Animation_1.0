package com.example.batteryanimation.Repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.batteryanimation.HelperClasses.Constants
import com.example.batteryanimation.R

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


    private val sharedPreferencesAnimation: SharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME_ANIMATION, Context.MODE_PRIVATE)

    private val _isSwitchOn = MutableLiveData<Boolean>()
    val isSwitchOn: LiveData<Boolean>
        get() = _isSwitchOn

    init {
        _isSwitchOn.value = sharedPreferencesAnimation.getBoolean(Constants.SWITCH_STATE_ANIMATION_KEY, true)
    }

    fun updateSwitchState(isSwitchOn: Boolean) {
        _isSwitchOn.value = isSwitchOn
        sharedPreferencesAnimation.edit().putBoolean(Constants.SWITCH_STATE_ANIMATION_KEY, isSwitchOn).apply()
    }
}