package com.pos.cashiersp.presentation.settings

sealed class SettingsEvent {
    object OnClickPrinterSettings : SettingsEvent()
    object OnClickSelectTenant : SettingsEvent()
    object OnClickSelectStore : SettingsEvent()
}