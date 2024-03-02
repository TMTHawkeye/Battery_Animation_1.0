package com.batterycharging.animation.chargingeffect.ViewModels

import androidx.lifecycle.ViewModel
import com.batterycharging.animation.chargingeffect.Repositories.BatteryInfoRepository

class BatteryInfoViewModel(val batteryInfoRepository: BatteryInfoRepository) : ViewModel() {

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
        batteryInfoRepository.updateSwitchState(newState)
    }
}