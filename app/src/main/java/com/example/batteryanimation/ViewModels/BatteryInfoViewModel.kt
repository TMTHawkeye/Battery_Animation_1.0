package com.example.batteryanimation.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.batteryanimation.ModelClasses.SwitchStates
import com.example.batteryanimation.Repositories.BatteryInfoRepository
import kotlinx.coroutines.Dispatchers

class BatteryInfoViewModel(val batteryInfoRepository:BatteryInfoRepository) : ViewModel() {

    val batteryPercentage = batteryInfoRepository.batteryPercentage
    val isCharging = batteryInfoRepository.isCharging
    val remainingTime = batteryInfoRepository.remainingTime
    val deviceTemperature = batteryInfoRepository.batteryTemperature
    val voltage = batteryInfoRepository.batteryVoltage
    val technology = batteryInfoRepository.batteryTechnology
    val health = batteryInfoRepository.batteryHealth
    val capacity = batteryInfoRepository.batteryCapacity

    init {
        batteryInfoRepository.startBatteryMonitoring()
    }

    override fun onCleared() {
//        batteryInfoRepository.stopBatteryMonitoring()
        super.onCleared()
    }


//    val isSwitchOn=batteryInfoRepository.isSwitchOn
//
//    fun updateSwitchState(isSwitchOn:Boolean){
//        batteryInfoRepository.updateSwitchState(isSwitchOn)
//    }

    val switchState = batteryInfoRepository.switchState

    fun updateSwitchState(switchId: String, isChecked: Boolean) {
        val currentState = switchState.value ?: return
        val newState = when (switchId) {
            "low_battery" -> currentState.copy(isLowBatterySwitchOn = isChecked)
            "full_battery" -> currentState.copy(isFullBatterySwitchOn = isChecked)
            "charger_connect" -> currentState.copy(isChargerConnectSwitchOn = isChecked)
            "charger_disconnect" -> currentState.copy(isChargerDisconnectSwitchOn = isChecked)
            else -> currentState
        }
//        switchState.value = newState
        batteryInfoRepository.updateSwitchState(newState)
    }
}