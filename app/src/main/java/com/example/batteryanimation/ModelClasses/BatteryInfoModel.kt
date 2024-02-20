package com.example.batteryanimation.ModelClasses

import android.os.Parcelable
import java.io.Serializable

data class BatteryInfoModel(val remainingTime:String?,val batteryPercentage:Int?,val chargingStatus:Boolean) :Serializable