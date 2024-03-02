package com.batterycharging.animation.chargingeffect.Repositories

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.batterycharging.animation.chargingeffect.HelperClasses.Constants
import com.batterycharging.animation.chargingeffect.ModelClasses.SwitchStates
import com.google.gson.Gson


class BatteryInfoRepository(val context: Context) {

    private val _batteryPercentage = MutableLiveData<Int?>()
    val batteryPercentage: LiveData<Int?>
        get() = _batteryPercentage

    private val _isCharging = MutableLiveData<Boolean>()
    val isCharging: LiveData<Boolean>
        get() = _isCharging

    private val _remainingTime = MutableLiveData<Long?>()
    val remainingTime: LiveData<Long?>
        get() = _remainingTime

    private val _batteryTemperature = MutableLiveData<Double?>()
    val batteryTemperature: LiveData<Double?>
        get() = _batteryTemperature

    private val _batteryVoltage = MutableLiveData<Int?>()
    val batteryVoltage: LiveData<Int?>
        get() = _batteryVoltage

    private val _batteryTechnology = MutableLiveData<String?>()
    val batteryTechnology: LiveData<String?>
        get() = _batteryTechnology

    private val _batteryHealth = MutableLiveData<Int?>()
    val batteryHealth: LiveData<Int?>
        get() = _batteryHealth

    private val _batteryCapacity = MutableLiveData<Double?>()
    val batteryCapacity: LiveData<Double?>
        get() = _batteryCapacity

    private val batteryBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateBatteryInfo(intent)
        }
    }

    fun startBatteryMonitoring() {
        context.registerReceiver(batteryBroadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    fun stopBatteryMonitoring() {
        context.unregisterReceiver(batteryBroadcastReceiver)
    }

    private fun updateBatteryInfo(intent: Intent?) {
        val level: Int = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
        val scale: Int = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 0) ?: 0
        val voltage: Int = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        val status: Int? = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
        val temperature: Int = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val temperatureCelsius = temperature / 10.0
        val technology: String? = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
        val health: Int? = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)


        val batteryPercentage = if (level != 0 && scale != 0) {
            (level * 100.0 / scale).toInt()
        } else {
            0
        }

//        Log.d("TAG_remaining", "updateBatteryInfo: $scale and $level")

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        if (isCharging && batteryPercentage!=100) {
            val remainingCapacity = scale - level

            val remainingTimeInSeconds = (remainingCapacity.toDouble() / (voltage * getBatterysCapacity()) * 3600).toLong()
            _remainingTime.postValue(remainingTimeInSeconds)
        }
        else{
            _remainingTime.postValue(null)
        }

        _batteryPercentage.postValue(batteryPercentage)
        _isCharging.postValue(isCharging)
        _batteryTemperature.postValue(temperatureCelsius)
        _batteryVoltage.postValue(voltage)
        _batteryTechnology.postValue(technology)
        _batteryHealth.postValue(health)
        _batteryCapacity.postValue(getBatterysCapacity())
    }

    fun getBatterysCapacity(): Double {
        val mPowerProfile: Any
        var batteryCapacity = 0.0
        val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                .getConstructor(Context::class.java)
                .newInstance(context)
            batteryCapacity = Class
                .forName(POWER_PROFILE_CLASS)
                .getMethod("getBatteryCapacity")
                .invoke(mPowerProfile) as Double
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return batteryCapacity
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    private val _switchState = MutableLiveData<SwitchStates>()
    val switchState: LiveData<SwitchStates>
        get() = _switchState

    init {
        val savedSwitchStateString = sharedPreferences.getString(Constants.SWITCH_STATE_KEY, null)
        val defaultSwitchState = SwitchStates(
            isLowBatterySwitchOn = false,
            isFullBatterySwitchOn = false,
            isChargerConnectSwitchOn = false,
            isChargerDisconnectSwitchOn = false
        )
        _switchState.value = savedSwitchStateString?.let { deserializeSwitchState(it) } ?: defaultSwitchState
    }

    fun updateSwitchState(newSwitchState: SwitchStates) {
        _switchState.value = newSwitchState
        val switchStateString = serializeSwitchState(newSwitchState)
        sharedPreferences.edit().putString(Constants.SWITCH_STATE_KEY, switchStateString).apply()
    }

    private fun serializeSwitchState(switchState: SwitchStates): String {
        return Gson().toJson(switchState)
    }

    private fun deserializeSwitchState(switchStateString: String): SwitchStates {
        return Gson().fromJson(switchStateString, SwitchStates::class.java)
    }


}
