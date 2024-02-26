package com.example.batteryanimation.DependencyInjection

import com.example.batteryanimation.Repositories.AnimationRepository
import com.example.batteryanimation.Repositories.BatteryInfoRepository
import com.example.batteryanimation.ViewModels.AnimationViewModel
import com.example.batteryanimation.ViewModels.BatteryInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule= module {

    single { BatteryInfoRepository(get()) }
    viewModel { BatteryInfoViewModel(get()) }

    single { AnimationRepository(get()) }
    viewModel { AnimationViewModel(get()) }

}