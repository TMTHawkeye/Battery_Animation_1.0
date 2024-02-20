package com.example.batteryanimation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    val isSwitchOn=batteryInfoRepository.isSwitchOn

    fun updateSwitchState(isSwitchOn:Boolean){
        batteryInfoRepository.updateSwitchState(isSwitchOn)
    }
}