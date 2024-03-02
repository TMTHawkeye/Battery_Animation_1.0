package com.batterycharging.animation.chargingeffect.DependencyInjection

import com.batterycharging.animation.chargingeffect.Repositories.AnimationRepository
import com.batterycharging.animation.chargingeffect.Repositories.BatteryInfoRepository
import com.batterycharging.animation.chargingeffect.ViewModels.AnimationViewModel
import com.batterycharging.animation.chargingeffect.ViewModels.BatteryInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule= module {

    single { BatteryInfoRepository(get()) }
    viewModel { BatteryInfoViewModel(get()) }

    single { AnimationRepository(get()) }
    viewModel { AnimationViewModel(get()) }

}