package com.pos.cashiersp.presentation

sealed class Screen(val route: String) {
    object Greeting: Screen("Greeting")
}