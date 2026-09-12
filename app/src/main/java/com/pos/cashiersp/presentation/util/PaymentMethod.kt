package com.pos.cashiersp.presentation.util

import com.google.gson.annotations.SerializedName

enum class PaymentMethod(val uiLabel: String) {
    CASH("Cash"),

    EWALLET("EWallet"),

    CARD("Card"),

    QRIS("QRIS"),

    OTHER("Other"),
}