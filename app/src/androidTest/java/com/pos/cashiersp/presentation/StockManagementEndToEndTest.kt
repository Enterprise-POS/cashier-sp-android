package com.pos.cashiersp.presentation

import com.pos.cashiersp.di.RetrofitModule
import com.pos.cashiersp.di.RoomModule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules

@HiltAndroidTest
@UninstallModules(RetrofitModule::class, RoomModule::class)
class StockManagementEndToEndTest {
}