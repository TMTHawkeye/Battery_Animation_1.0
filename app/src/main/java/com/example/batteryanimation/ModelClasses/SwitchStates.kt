package com.example.batteryanimation.ModelClasses

// SwitchState.kt
data class SwitchStates(
    val isLowBatterySwitchOn: Boolean,
    val isFullBatterySwitchOn: Boolean,
    val isChargerConnectSwitchOn: Boolean,
    val isChargerDisconnectSwitchOn: Boolean
)
