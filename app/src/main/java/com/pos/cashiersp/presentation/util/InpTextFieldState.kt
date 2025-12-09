package com.pos.cashiersp.presentation.util

data class InpTextFieldState(
    val text: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true,
    val isError: Boolean = false,
)
