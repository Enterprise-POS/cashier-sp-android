package com.pos.cashiersp.presentation.util

sealed interface ConnectionResult {
    object ConnectionEstablished : ConnectionResult
    data class Error(val message: String) : ConnectionResult
    object Connecting : ConnectionResult
}